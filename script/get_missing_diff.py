import os
import json
import subprocess

ROOT = os.path.join(os.path.dirname(__file__), '..')

data = None
with open(os.path.join(ROOT, 'docs', 'bugswarm.json')) as fd:
    data = json.load(fd)

travis_jobs = {}
travis_jobs_path = os.path.join(ROOT, 'docs', 'travis_data.json')
if os.path.exists(travis_jobs_path):
    with open(travis_jobs_path) as fd:
        travis_jobs = json.load(fd)


count = 0
for bug in data:
    failed_job = travis_jobs[str(bug['failed_job']['job_id'])]
    passed_job = travis_jobs[str(bug['passed_job']['job_id'])]
    repo = failed_job['repository_slug']

    bug_id = "%s-%s" % (bug['repo'].replace('/', '-'), failed_job['id'])

    bug_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id)

    diff_path = os.path.join(bug_path, 'patch.diff')
    if os.path.exists(diff_path):
        
        continue
    count+=1
    cmd = "bugswarm run --image-tag %s --use-sandbox --pipe-stdin <<< \"cd ~/build/passed/; rm -rf ~/build/failed/%s; rm -rf ~/build/passed/%s; git diff --no-index ~/build/failed/ ~/build/passed/ | sed -e 's/home\/travis\/build\/passed\///g' | sed -e 's/home\/travis\/build\/failed\///g' > /bugswarm-sandbox/patch.diff;\"" % (bug_id, repo, repo)
    subprocess.call(cmd, shell=True)

    bugswarm_diff_path = os.path.expanduser("~/bugswarm-sandbox/patch.diff")
    if os.path.exists(bugswarm_diff_path):
        os.rename(bugswarm_diff_path, diff_path)
    
print(count)

