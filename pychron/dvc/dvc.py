# ===============================================================================
# Copyright 2015 Jake Ross
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ===============================================================================
# ============= enthought library imports =======================
import glob

from apptools.preferences.preference_binding import bind_preference
from traits.api import Instance, Str, Set, List, provides
# ============= standard library imports ========================
from math import isnan
from datetime import datetime
from uncertainties import nominal_value
from uncertainties import std_dev
from git import Repo
from itertools import groupby
import shutil
import time
import os
import json
# ============= local library imports  ==========================
from pychron.core.i_datastore import IDatastore
from pychron.core.helpers.filetools import remove_extension, list_subdirectories
from pychron.core.progress import progress_loader
from pychron.database.interpreted_age import InterpretedAge
from pychron.dvc import dvc_dump, dvc_load
from pychron.dvc.defaults import TRIGA, HOLDER_24_SPOKES, LASER221, LASER65
from pychron.dvc.dvc_analysis import DVCAnalysis, repository_path, analysis_path, PATH_MODIFIERS, \
    AnalysisNotAnvailableError
from pychron.dvc.dvc_database import DVCDatabase
from pychron.dvc.meta_repo import MetaRepo
from pychron.envisage.browser.record_views import InterpretedAgeRecordView
from pychron.git_archive.repo_manager import GitRepoManager, format_date, get_repository_branch
from pychron.github import Organization
from pychron.loggable import Loggable
from pychron.paths import paths
from pychron.pychron_constants import RATIO_KEYS, INTERFERENCE_KEYS

TESTSTR = {'blanks': 'auto update blanks', 'iso_evo': 'auto update iso_evo'}


def repository_has_staged(ps):
    if not hasattr(ps, '__iter__'):
        ps = (ps,)

    changed = []
    repo = GitRepoManager()
    for p in ps:
        pp = os.path.join(paths.repository_dataset_dir, p)
        repo.open_repo(pp)
        if repo.has_unpushed_commits():
            changed.append(p)

    return changed


def push_repositories(ps):
    repo = GitRepoManager()
    for p in ps:
        pp = os.path.join(paths.repository_dataset_dir, p)
        repo.open_repo(pp)
        repo.push()


def get_review_status(record):
    ms = 0
    for m in ('blanks', 'intercepts', 'icfactors'):
        p = analysis_path(record.record_id, record.repository_identifier, modifier=m)
        date = ''
        with open(p, 'r') as rfile:
            obj = json.load(rfile)
            reviewed = obj.get('reviewed', False)
            if reviewed:
                dt = datetime.fromtimestamp(os.path.getmtime(p))
                date = dt.strftime('%m/%d/%Y')
                ms += 1

        setattr(record, '{}_review_status'.format(m), (reviewed, date))

    ret = 'Intermediate'  # intermediate
    if not ms:
        ret = 'Default'  # default
    elif ms == 3:
        ret = 'All'  # all

    record.review_status = ret


def find_interpreted_age_path(idn, repositories, prefixlen=3):
    prefix = idn[:prefixlen]
    suffix = '{}.ia.json'.format(idn[prefixlen:])

    for e in repositories:
        pathname = '{}/{}/{}/ia/{}'.format(paths.repository_dataset_dir, e, prefix, suffix)
        ps = glob.glob(pathname)
        if ps:
            return ps[0]


def make_remote_url(org, name):
    return '{}/{}/{}.git'.format(paths.git_base_origin, org, name)


class DVCException(BaseException):
    def __init__(self, attr):
        self._attr = attr

    def __repr__(self):
        return 'DVCException: neither DVCDatabase or MetaRepo have {}'.format(self._attr)

    def __str__(self):
        return self.__repr__()


class Tag(object):
    name = None
    path = None

    @classmethod
    def from_analysis(cls, an):
        tag = cls()
        tag.name = an.tag
        tag.record_id = an.record_id
        tag.repository_identifier = an.repository_identifier
        tag.path = analysis_path(an.record_id, an.repository_identifier, modifier='tags')

        return tag

    def dump(self):
        obj = {'name': self.name}
        if not self.path:
            self.path = analysis_path(self.record_id, self.repository_identifier, modifier='tags', mode='w')

        # with open(self.path, 'w') as wfile:
        #     json.dump(obj, wfile, indent=4)
        dvc_dump(obj, self.path)


class DVCInterpretedAge(InterpretedAge):
    def from_json(self, obj):
        for a in ('age', 'age_err', 'kca', 'kca_err', 'age_kind', 'kca_kind', 'mswd',
                  'sample', 'material', 'identifier', 'nanalyses', 'irradiation'):
            setattr(self, a, obj[a])


class GitSessionCTX(object):
    def __init__(self, parent, repository_identifier, message):
        self._parent = parent
        self._repository_id = repository_identifier
        self._message = message
        self._parent.get_repository(repository_identifier)

    def __enter__(self):
        pass

    def __exit__(self, exc_type, exc_val, exc_tb):
        if exc_type is None:
            if self._parent.is_dirty():
                self._parent.repository_commit(self._repository_id, self._message)


@provides(IDatastore)
class DVC(Loggable):
    db = Instance('pychron.dvc.dvc_database.DVCDatabase')
    meta_repo = Instance('pychron.dvc.meta_repo.MetaRepo')

    meta_repo_name = Str
    organization = Str
    github_user = Str
    github_password = Str
    default_team = Str

    current_repository = Instance(GitRepoManager)
    auto_add = True
    pulled_repositories = Set
    selected_repositories = List

    def __init__(self, bind=True, *args, **kw):
        super(DVC, self).__init__(*args, **kw)

        if bind:
            self._bind_preferences()
            # self.synchronize()
            # self._defaults()

    def initialize(self, inform=False):
        self.debug('Initialize DVC')
        self.open_meta_repo()

        # update meta repo.
        self.meta_pull()

        if self.db.connect():
            # self._defaults()
            return True

    def open_meta_repo(self):
        mrepo = self.meta_repo
        root = os.path.join(paths.dvc_dir, self.meta_repo_name)
        if os.path.isdir(os.path.join(root, '.git')):
            self.debug('Opening Meta Repo')
            mrepo.open_repo(root)
        else:
            url = self.make_url(self.meta_repo_name)
            path = os.path.join(paths.dvc_dir, self.meta_repo_name)
            self.meta_repo.clone(url, path)

    def synchronize(self, pull=True):
        """
        pull meta_repo changes

        :return:
        """
        if pull:
            self.meta_repo.pull()
        else:
            self.meta_repo.push()

    def load_analysis_backend(self, ln, arar_age):
        db = self.db
        with db.session_ctx():
            ip = db.get_identifier(ln)
            dblevel = ip.level
            irrad = dblevel.irradiation.name
            level = dblevel.name
            prodname = dblevel.production.name
            pos = ip.position

        j = self.meta_repo.get_flux(irrad, level, pos)
        prod = self.meta_repo.get_production(prodname)
        cs = self.meta_repo.get_chronology(irrad)

        x = datetime.now()
        now = time.mktime(x.timetuple())
        arar_age.trait_set(j=j,
                           production_ratios=prod.to_dict(RATIO_KEYS),
                           interference_corrections=prod.to_dict(INTERFERENCE_KEYS),
                           chron_segments=cs.get_chron_segments(x),
                           irradiation_time=cs.irradiation_time,
                           timestamp=now)
        return True

    # database
    # analysis manual edit
    # def manual_intercepts(self, runid, experiment_identifier, values, errors):
    #     return self._manual_edit(runid, experiment_identifier, values, errors, 'intercepts')
    #
    # def manual_blanks(self, runid, experiment_identifier, values, errors):
    #     return self._manual_edit(runid, experiment_identifier, values, errors, 'blanks')
    #
    # def manual_baselines(self, runid, experiment_identifier, values, errors):
    #     return self._manual_edit(runid, experiment_identifier, values, errors, 'baselines')
    #
    # def manual_baselines(self, runid, experiment_identifier, values, errors):
    #     return self._manual_edit(runid, experiment_identifier, values, errors, 'baselines')

    def manual_edit(self, runid, repository_identifier, values, errors, modifier):
        path = analysis_path(runid, repository_identifier, modifier=modifier)
        with open(path, 'r') as rfile:
            obj = json.load(rfile)
            for k, v in values.iteritems():
                o = obj[k]
                o['manual_value'] = v
                o['use_manual_value'] = True
            for k, v in errors.iteritems():
                o = obj[k]
                o['manual_error'] = v
                o['use_manual_error'] = True

        dvc_dump(obj, path)
        return path

    def revert_manual_edits(self, runid, repository_identifier):
        ps = []
        for mod in ('intercepts', 'blanks', 'baselines', 'icfactors'):
            path = analysis_path(runid, repository_identifier, modifier=mod)
            with open(path, 'r') as rfile:
                obj = json.load(rfile)
                for item in obj.itervalues():
                    if isinstance(item, dict):
                        item['use_manual_value'] = False
                        item['use_manual_error'] = False
            ps.append(path)
            dvc_dump(obj, path)

        msg = '<MANUAL> reverted to non manually edited'
        self.commit_manual_edits(repository_identifier, ps, msg)

    def commit_manual_edits(self, repository_identifier, ps, msg):
        if self.repository_add_paths(repository_identifier, ps):
            self.repository_commit(repository_identifier, msg)

    # analysis processing
    def analysis_has_review(self, ai, attr):
        return True
        # test_str = TESTSTR[attr]
        # repo = self._get_experiment_repo(ai.experiment_id)
        # for l in repo.get_log():
        #     if l.message.startswith(test_str):
        #         self.debug('{} {} reviewed'.format(ai, attr))
        #         return True
        # else:
        #     self.debug('{} {} not reviewed'.format(ai, attr))

    def update_analyses(self, ans, modifier, msg):
        key = lambda x: x.repository_identifier
        ans = sorted(ans, key=key)
        mod_repositories = []
        for expid, ais in groupby(ans, key=key):
            paths = map(lambda x: analysis_path(x.record_id, x.repository_identifier, modifier=modifier), ais)
            # print expid, modifier, paths
            if self.repository_add_paths(expid, paths):
                self.repository_commit(expid, msg)
                mod_repositories.append(expid)

        # ais = map(analysis_path, ais)
        #     if self.experiment_add_analyses(exp, ais):
        #         self.experiment_commit(exp, msg)
        #         mod_experiments.append(exp)
        return mod_repositories

    def update_tag(self, an):
        tag = Tag.from_analysis(an)
        tag.dump()

        expid = an.repository_identifier
        return self.repository_add_paths(expid, tag.path)

    def save_icfactors(self, ai, dets, fits, refs):
        if fits and dets:
            self.info('Saving icfactors for {}'.format(ai))
            ai.dump_icfactors(dets, fits, refs, reviewed=True)

    def save_blanks(self, ai, keys, refs):
        if keys:
            self.info('Saving blanks for {}'.format(ai))
            ai.dump_blanks(keys, refs, reviewed=True)

    def save_fits(self, ai, keys):
        if keys:
            self.info('Saving fits for {}'.format(ai))
            ai.dump_fits(keys, reviewed=True)

    def save_j(self, irradiation, level, pos, identifier, j, e, decay, analyses, add=True):
        self.info('Saving j for {}{}:{} {}, j={} +/-{}'.format(irradiation, level,
                                                               pos, identifier, j, e))
        self.meta_repo.update_flux(irradiation, level, pos, identifier, j, e, decay, analyses, add)

        db = self.db
        with db.session_ctx():
            ip = db.get_identifier(identifier)
            ip.j = j
            ip.j_err = e

    def find_interpreted_ages(self, identifiers, repositories):
        ias = []
        for idn in identifiers:
            path = find_interpreted_age_path(idn, repositories)
            if path:
                obj = dvc_load(path)
                name = obj.get('name')
                ias.append(InterpretedAgeRecordView(idn, path, name))

        return ias

    def find_references(self, times, atypes, hours, exclude=None, make_records=True, **kw):
        records = self.db.find_references(times, atypes, hours, exclude=exclude, **kw)

        if records:
            if make_records:
                records = self.make_analyses(records)
            return records

    def make_interpreted_ages(self, ias):
        def func(x, prog, i, n):
            if prog:
                prog.change_message('Making Interpreted age {}'.format(x.name))
            obj = dvc_load(x.path)
            ia = DVCInterpretedAge()
            ia.from_json(obj)
            return ia

        return progress_loader(ias, func, step=25)

    def make_analyses(self, records, calculate_f_only=False):
        if not records:
            return

        # load repositories
        exps = {r.repository_identifier for r in records}
        if self.pulled_repositories:
            exps = exps - self.pulled_repositories

            self.pulled_repositories.union(exps)
        else:
            self.pulled_repositories = exps

        st = time.time()

        make_record = self._make_record
        meta_repo = self.meta_repo

        def func(*args):
            return make_record(meta_repo=meta_repo,
                               calculate_f_only=calculate_f_only, *args)

        ret = progress_loader(records, func, threshold=1, step=25)
        et = time.time() - st
        n = len(records)

        self.debug('Make analysis time, total: {}, n: {}, average: {}'.format(et, n, et / float(n)))
        return ret

    # adders db
    # def add_analysis(self, **kw):
    #     with self.db.session_ctx():
    #         self.db.add_material(**kw)

    # updaters
    # def update_chronology(self, name, doses):
    # self.meta_repo.update_chronology(name, doses)
    #
    # def update_scripts(self, name, path):
    # self.meta_repo.update_scripts(name, path)
    #
    # def update_experiment_queue(self, name, path):
    #     self.meta_repo.update_experiment_queue(name, path)

    # repositories
    def repository_add_paths(self, repository_identifier, paths):
        repo = self._get_repository(repository_identifier)
        return repo.add_paths(paths)

    def repository_commit(self, repository, msg):
        self.debug('Experiment commit: {} msg: {}'.format(repository, msg))
        repo = self._get_repository(repository)
        repo.commit(msg)

    def remote_repositories(self, attributes=None):
        org = self._organization_factory()
        if attributes:
            return org.repos(attributes)
        else:
            return org.repo_names

    def check_github_connection(self):
        org = self._organization_factory()
        try:
            return org.info is not None
        except BaseException:
            pass

    def make_url(self, name):
        return make_remote_url(self.organization, name)

    def git_session_ctx(self, repository_identifier, message):
        return GitSessionCTX(self, repository_identifier, message)

    def sync_repo(self, name):
        """
        pull or clone an repo

        """
        root = os.path.join(paths.repository_dataset_dir, name)
        exists = os.path.isdir(os.path.join(root, '.git'))

        if exists:
            repo = self._get_repository(name)
            repo.pull()
        else:
            url = self.make_url(name)
            GitRepoManager.clone_from(url, root)

        return True

    def rollback_repository(self, expid):
        repo = self._get_repository(expid)

        cpaths = repo.get_local_changes()
        # cover changed paths to a list of analyses

        # select paths to revert
        rpaths = ('.',)
        repo.cmd('checkout', '--', ' '.join(rpaths))
        for p in rpaths:
            self.debug('revert changes for {}'.format(p))

        head = repo.get_head(hexsha=False)
        msg = 'Changes to {} reverted to Commit: {}\n' \
              'Date: {}\n' \
              'Message: {}'.format(expid, head.hexsha[:10],
                                   format_date(head.committed_date),
                                   head.message)
        self.information_dialog(msg)

    # IDatastore
    def get_greatest_aliquot(self, identifier):
        return self.db.get_greatest_aliquot(identifier)

    def get_greatest_step(self, identifier, aliquot):
        return self.db.get_greatest_step(identifier, aliquot)

    def is_connected(self):
        return self.db.connected

    def connect(self, *args, **kw):
        return self.db.connect(*args, **kw)

    # meta repo
    def set_identifier(self, *args):
        self.meta_repo.set_identifier(*args)

    def update_chronology(self, name, doses):
        self.meta_repo.update_chronology(name, doses)
        self.meta_commit('updated chronology for {}'.format(name))

    def meta_pull(self, **kw):
        self.meta_repo.smart_pull(**kw)

    def meta_push(self):
        self.meta_repo.push()

    def meta_add_all(self):
        self.meta_repo.add_unstaged(paths.meta_root, add_all=True)

    def meta_commit(self, msg):
        changes = self.meta_repo.has_staged()
        if changes:
            self.debug('meta repo has changes: {}'.format(changes))
            self.meta_repo.report_status()
            self.meta_repo.commit(msg)
            self.meta_repo.clear_cache = True
        else:
            self.debug('no changes to meta repo')

    # get
    def get_local_repositories(self):
        return list_subdirectories(paths.repository_dataset_dir)

    def get_repository(self, exp):
        return self._get_repository(exp)

    def get_meta_head(self):
        return self.meta_repo.get_head()

    def get_irradiation_geometry(self, irrad, level):
        with self.db.session_ctx():
            dblevel = self.db.get_irradiation_level(irrad, level)
            return self.meta_repo.get_irradiation_holder_holes(dblevel.holder)

    def get_irradiation_names(self):
        with self.db.session_ctx():
            irrads = self.db.get_irradiations()
            names = [i.name for i in irrads]

        return names

    # add
    def add_interpreted_age(self, ia):

        a = ia.get_ma_scaled_age()
        mswd = ia.preferred_mswd

        if isnan(mswd):
            mswd = 0

        d = dict(age=float(nominal_value(a)),
                 age_err=float(std_dev(a)),
                 display_age_units=ia.age_units,
                 age_kind=ia.preferred_age_kind,
                 kca_kind=ia.preferred_kca_kind,
                 kca=float(ia.preferred_kca_value),
                 kca_err=float(ia.preferred_kca_error),
                 mswd=float(mswd),
                 include_j_error_in_mean=ia.include_j_error_in_mean,
                 include_j_error_in_plateau=ia.include_j_error_in_plateau,
                 include_j_error_in_individual_analyses=ia.include_j_error_in_individual_analyses,
                 sample=ia.sample,
                 material=ia.material,
                 identifier=ia.identifier,
                 nanalyses=ia.nanalyses,
                 irradiation=ia.irradiation)

        d['analyses'] = [dict(uuid=ai.uuid, tag=ai.tag, plateau_step=ia.get_is_plateau_step(ai))
                         for ai in ia.all_analyses]

        self._add_interpreted_age(ia, d)

    def add_repository_association(self, expid, runspec):
        db = self.db
        with db.session_ctx():
            dban = db.get_analysis_uuid(runspec.uuid)
            for e in dban.repository_associations:
                if e.repository == expid:
                    break
            else:
                db.add_repository_association(expid, dban)

            src_expid = runspec.repository_identifier
            if src_expid != expid:
                repo = self._get_repository(expid)

                for m in PATH_MODIFIERS:
                    src = analysis_path(runspec.record_id, src_expid, modifier=m)
                    dest = analysis_path(runspec.record_id, expid, modifier=m, mode='w')

                    shutil.copyfile(src, dest)
                    repo.add(dest, commit=False)
                repo.commit('added repository association')

    def add_measured_position(self, *args, **kw):
        with self.db.session_ctx():
            self.db.add_measured_position(*args, **kw)

    def add_material(self, name):
        with self.db.session_ctx():
            self.db.add_material(name)

    def add_sample(self, name, project, material):
        with self.db.session_ctx():
            self.db.add_sample(name, project, material)

    def add_irradiation_position(self, irrad, level, pos, identifier=None):
        with self.db.session_ctx():
            dbip = self.db.add_irradiation_position(irrad, level, pos, identifier)
            # self.db.commit()

            self.meta_repo.add_position(irrad, level, pos)
            return dbip

    def add_irradiation_level(self, name, irradiation, holder, production_name):
        with self.db.session_ctx():
            self.db.add_irradiation_level(name, irradiation, holder, production_name)
            # self.db.commit()

        self.meta_repo.add_level(irradiation, name)
        self.meta_repo.update_level_production(irradiation, name, production_name)
        return True

    def clone_repository(self, identifier):
        root = os.path.join(paths.repository_dataset_dir, identifier)
        if not os.path.isdir(root):
            self.debug('cloning {}'.format(root))
            url = self.make_url(identifier)
            Repo.clone_from(url, root)
        else:
            self.debug('{} already exists'.format(identifier))

    def add_repository(self, identifier, principal_investigator):
        self.debug('trying to add repository identifier={}, pi={}'.format(identifier, principal_investigator))
        org = self._organization_factory()
        if identifier in org.repo_names:
            self.warning_dialog('Repository "{}" already exists'.format(identifier))
        else:
            root = os.path.join(paths.repository_dataset_dir, identifier)

            if os.path.isdir(root):
                self.warning_dialog('{} already exists.'.format(root))
            else:
                self.info('Creating repository. {}'.format(identifier))
                # with open('/Users/ross/Programming/githubauth.txt') as rfile:
                #     usr = rfile.readline().strip()
                #     pwd = rfile.readline().strip()

                org.create_repo(identifier, self.github_user, self.github_password,
                                auto_init=True)

                # url = '{}/{}/{}.git'.format(paths.git_base_origin, self.organization, identifier)
                Repo.clone_from(self.make_url(identifier), root)
                self.db.add_repository(identifier, principal_investigator)
                return True

    def add_irradiation(self, name, doses=None, add_repo=False, principal_investigator=None):
        with self.db.session_ctx():
            if self.db.get_irradiation(name):
                self.warning('irradiation {} already exists'.format(name))
                return

            self.db.add_irradiation(name)

        self.meta_repo.add_irradiation(name)
        self.meta_repo.add_chronology(name, doses)

        root = os.path.join(paths.meta_root, name)
        p = os.path.join(root, 'productions')
        if not os.path.isdir(p):
            os.mkdir(p)
        with open(os.path.join(root, 'productions.json'), 'w') as wfile:
            json.dump({}, wfile)

        if add_repo and principal_investigator:
            self.add_repository('Irradiation-{}'.format(name), principal_investigator)

    def add_load_holder(self, name, path_or_txt):
        with self.db.session_ctx():
            self.db.add_load_holder(name)
        self.meta_repo.add_load_holder(name, path_or_txt)

    def copy_production(self, pr):
        """

        @param pr: irrad_ProductionTable object
        @return:
        """
        pname = pr.name.replace(' ', '_')
        path = os.path.join(paths.meta_root, 'productions', '{}.json'.format(pname))
        if not os.path.isfile(path):
            obj = {}
            for attr in INTERFERENCE_KEYS + RATIO_KEYS:
                obj[attr] = [getattr(pr, attr), getattr(pr, '{}_err'.format(attr))]
            dvc_dump(obj, path)

    # private
    def _add_interpreted_age(self, ia, d):
        p = analysis_path(ia.identifier, ia.repository_identifier, modifier='ia', mode='w')
        dvc_dump(d, p)

    def _load_repository(self, expid, prog, i, n):
        if prog:
            prog.change_message('Loading repository {}. {}/{}'.format(expid, i, n))
            # repo = GitRepoManager()
            # repo.open_repo(expid, root=paths.experiment_dataset_dir)

        self.sync_repo(expid)

    def _make_record(self, record, prog, i, n, meta_repo=None, calculate_f=False, calculate_f_only=False):
        if prog:
            prog.change_message('Loading analysis {}. {}/{}'.format(record.record_id, i, n))

        expid = record.repository_identifier
        if not expid:
            exps = record.repository_ids
            self.debug('Analysis {} is associated multiple repositories '
                       '{}'.format(record.record_id, ','.join(exps)))
            expid = None
            if self.selected_repositories:
                rr = [si for si in self.selected_repositories if si in exps]
                if rr:
                    if len(rr) > 1:
                        expid = self._get_requested_experiment_id(rr)
                    else:
                        expid = rr[0]

            if expid is None:
                expid = self._get_requested_experiment_id(exps)

        if isinstance(record, DVCAnalysis):
            a = record
        else:
            try:
                a = DVCAnalysis(record.record_id, expid)
            except AnalysisNotAnvailableError:
                self.info('Analysis {} not available. Trying to clone repository "{}'.format(record.record_id, expid))
                self.sync_repo(expid)
                try:
                    a = DVCAnalysis(record.record_id, expid)
                except AnalysisNotAnvailableError:
                    self.warning_dialog('Analysis {} not in repository {}'.format(record.record_id, expid))
                    return

            # get repository branch
            a.branch = get_repository_branch(os.path.join(paths.repository_dataset_dir, expid))

            a.set_tag(record.tag)

            # load irradiation
            if a.irradiation and a.irradiation not in ('NoIrradiation',):
                chronology = meta_repo.get_chronology(a.irradiation)
                a.set_chronology(chronology)

                pname, prod = meta_repo.get_production(a.irradiation, a.irradiation_level)
                a.set_production(pname, prod)

                j, lambda_k = meta_repo.get_flux(record.irradiation, record.irradiation_level,
                                                 record.irradiation_position_position)
                a.j = j
                if lambda_k:
                    a.arar_constants.lambda_k = lambda_k

                if calculate_f_only:
                    a.calculate_F()
                else:
                    a.calculate_age()
        return a

    def _organization_factory(self):
        org = Organization(self.organization,
                           usr=self.github_user,
                           pwd=self.github_password)
        return org

    def _get_repository(self, repository_identifier):
        repo = self.current_repository
        path = repository_path(repository_identifier)

        if repo is None or repo.path != path:
            self.debug('make new repomanager for {}'.format(path))
            repo = GitRepoManager()
            repo.path = path
            repo.open_repo(path)
            self.current_repository = repo

        return repo

    def _bind_preferences(self):

        prefid = 'pychron.dvc'
        for attr in ('meta_repo_name', 'organization', 'github_user', 'github_password', 'default_team'):
            bind_preference(self, attr, '{}.{}'.format(prefid, attr))

        prefid = 'pychron.dvc.db'
        for attr in ('username', 'password', 'name', 'host', 'kind', 'path'):
            bind_preference(self.db, attr, '{}.{}'.format(prefid, attr))

        self._meta_repo_name_changed()

    def _meta_repo_name_changed(self):
        paths.meta_root = os.path.join(paths.dvc_dir, self.meta_repo_name)

    def _defaults(self):
        self.debug('writing defaults')
        # self.db.create_all(Base.metadata)
        with self.db.session_ctx():
            self.db.add_save_user()
            for tag, func in (('irradiation holders', self._add_default_irradiation_holders),
                              ('productions', self._add_default_irradiation_productions),
                              ('load holders', self._add_default_load_holders)):

                d = os.path.join(self.meta_repo.path, tag.replace(' ', '_'))
                if not os.path.isdir(d):
                    os.mkdir(d)

                if self.auto_add:
                    func()
                elif self.confirmation_dialog('You have no {}. Would you like to add some defaults?'.format(tag)):
                    func()

    def _add_default_irradiation_productions(self):
        ds = (('TRIGA.txt', TRIGA),)
        self._add_defaults(ds, 'productions')

    def _add_default_irradiation_holders(self):
        ds = (('24Spokes.txt', HOLDER_24_SPOKES),)
        self._add_defaults(ds, 'irradiation_holders', )

    def _add_default_load_holders(self):
        ds = (('221.txt', LASER221),
              ('65.txt', LASER65))
        self._add_defaults(ds, 'load_holders', self.db.add_load_holder)

    def _add_defaults(self, defaults, root, dbfunc=None):
        commit = False
        repo = self.meta_repo
        for name, txt in defaults:
            p = os.path.join(repo.path, root, name)
            if not os.path.isfile(p):
                with open(p, 'w') as wfile:
                    wfile.write(txt)
                repo.add(p, commit=False)
                commit = True
                if dbfunc:
                    name = remove_extension(name)
                    dbfunc(name)

        if commit:
            repo.commit('added default {}'.format(root.replace('_', ' ')))

    def __getattr__(self, item):
        try:
            return getattr(self.db, item)
        except AttributeError:
            try:
                return getattr(self.meta_repo, item)
            except AttributeError, e:
                # print e, item
                raise DVCException(item)

    # defaults
    def _db_default(self):
        return DVCDatabase(kind='mysql',
                           username='root',
                           password='Argon',
                           host='localhost',
                           name='pychronmeta')

    def _meta_repo_default(self):
        return MetaRepo()


if __name__ == '__main__':
    paths.build('_dev')
    idn = '24138'
    exps = ['Irradiation-NM-272']
    print find_interpreted_age_path(idn, exps)
    # d = DVC(bind=False)
    # with open('/Users/ross/Programming/githubauth.txt') as rfile:
    #     usr = rfile.readline().strip()
    #     pwd = rfile.readline().strip()
    # d.github_user = usr
    # d.github_password = pwd
    # d.organization = 'NMGRLData'
    # d.add_experiment('Irradiation-NM-273')
# ============= EOF =============================================
