import os
import json
import requests
import re

ROOT = os.path.join(os.path.dirname(__file__), '..')

data = None
with open(os.path.join(ROOT, 'docs', 'bugswarm.json')) as fd:
    data = json.load(fd)

travis_jobs = {}
travis_jobs_path = os.path.join(ROOT, 'docs', 'travis_data.json')
if os.path.exists(travis_jobs_path):
    with open(travis_jobs_path) as fd:
        travis_jobs = json.load(fd)

commits = []
commits_path = os.path.join(ROOT, 'docs', 'commits.json')
if os.path.exists(commits_path):
    with open(commits_path) as fd:
        commits = json.load(fd)


def get_changed_files(diff):
    files = {
        "added":  [],
        "deleted": [],
        "modified": [],
        "renamed": [],
        "binary": {
            "added":  [],
            "deleted": [],
            "modified": [],
        }
    }
    diff_splitted = diff.split('\n')
    
    for i in range(0, len(diff_splitted) - 1):
        line = diff_splitted[i]
        next_line = diff_splitted[i+1]
        if '---' == line[:3]:
            if '+++' == next_line[:3]:
                action = 'modified'
                if line[4:] == '/dev/null':
                    action = 'added'
                elif next_line[4:] == '/dev/null':
                    action = 'deleted'
                f = line[6:]
                if action == 'added':
                    f = next_line[6:]
                if action == 'modified':
                    if f != next_line[6:]:
                        action = 'renamed'
                files[action].append(f)
        elif 'Binary files ' == next_line[:13]:
            m = re.match(r'Binary files (.+) and (.+) differ', next_line)
            if m is not None:
                action = 'modified'
                if m.group(1) == '/dev/null':
                    action = 'added'
                elif m.group(2) == '/dev/null':
                    action = 'deleted'
                f = m.group(1)
                if action == 'added':
                    f = m.group(2)
                files['binary'][action].append(f)
    return files


count = 0
for bug in data:
    failed_job = travis_jobs[str(bug['failed_job']['job_id'])]
    repo = failed_job['repository_slug']
    commit_id = failed_job['commit']['sha']

    count += 1

    bug_id = "%s-%s" % (bug['repo'].replace('/', '-'), failed_job['id'])

    diff_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id, 'patch.diff')

    if not os.path.exists(diff_path):
        continue
    diff = None
    with open(diff_path, encoding = "ISO-8859-1") as fd:
        diff = fd.read()
    all_files = get_changed_files(diff)
    files = list(set(all_files['modified']))

    root_buggy = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id, 'buggy_files/')
    for (root,d,fs)  in os.walk(root_buggy):
        for f in fs:
            if os.path.join(root, f).replace(root_buggy, '') not in files:
                os.remove(os.path.join(root, f))
                print(os.path.join(root, f).replace(root_buggy, ''))
    index_file = 0
    for f in files:
        index_file += 1
        if f == '':
            continue
        output_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id, 'buggy_files', f)
        if os.path.exists(output_path):
            continue
        r = requests.get("https://raw.githubusercontent.com/%s/%s/%s" % (repo, commit_id, f))
        print('%s %s/%s %s %s %s/%s' %(bug_id, count, len(data), f, r.status_code, index_file, len(files)))
        if r.status_code != 200:
            for c in commits:
                if c['branch'] == bug_id:
                    r = requests.get("https://raw.githubusercontent.com/%s/%s/%s" % ("tdurieux/BugSwarm", c['failed'], f))
                    print('%s %s/%s %s %s %s/%s' %(bug_id, count, len(data), f, r.status_code, index_file, len(files)))
                    break
            if r.status_code != 200:
                continue
        if not os.path.exists(os.path.dirname(output_path)):
            os.makedirs(os.path.dirname(output_path))
        with open(output_path,'wb') as fd:
            try:
                fd.write(r.content)
            except Exception as identifier:
                print(identifier)
    
