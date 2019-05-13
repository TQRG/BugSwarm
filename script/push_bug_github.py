import os
import json
import subprocess
from shutil import copyfile

ROOT = os.path.join(os.path.dirname(__file__), '..')
SCRIPT_PATH = os.path.join(ROOT, 'script', 'push_change.sh')

copyfile(SCRIPT_PATH, os.path.expanduser("~/bugswarm-sandbox/push_change.sh"))

data = None
with open(os.path.join(ROOT, 'docs', 'bugswarm.json')) as fd:
    data = json.load(fd)

images = []
with open(os.path.join(ROOT, 'docs', 'cloned_branches.txt')) as fd:
    images = fd.read().splitlines()


travis_jobs = {}
travis_jobs_path = os.path.join(ROOT, 'docs', 'travis_data.json')
if os.path.exists(travis_jobs_path):
    with open(travis_jobs_path) as fd:
        travis_jobs = json.load(fd)


count = 0
for bug in sorted(data, reverse=True):
    failed_job = travis_jobs[str(bug['failed_job']['job_id'])]
    passed_job = travis_jobs[str(bug['passed_job']['job_id'])]
    repo = failed_job['repository_slug']

    bug_id = "%s-%s" % (bug['repo'].replace('/', '-'), failed_job['id'])
    if bug_id in images:
        continue
    print(bug_id)
    
    bug_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id)

    diff_path = os.path.join(bug_path, 'patch.diff')

    count+=1
    cmd = "bugswarm run --image-tag %s --use-sandbox --pipe-stdin <<< \"sh /bugswarm-sandbox/push_change.sh %s %s;\"" % (bug_id, repo, bug_id)
    subprocess.call(cmd, shell=True)

    bugswarm_diff_path = os.path.expanduser("~/bugswarm-sandbox/patch.diff")
    if os.path.exists(bugswarm_diff_path):
        os.rename(bugswarm_diff_path, diff_path)
        pass
    
print(count)

