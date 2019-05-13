import os
import json
import requests
import random

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
random.shuffle(data)
for bug in data:
    passed_job = travis_jobs[str(bug['passed_job']['job_id'])]
    failed_job = travis_jobs[str(bug['failed_job']['job_id'])]
    repo = failed_job['repository_slug']
    commit_id = failed_job['commit']['sha']

    count += 1

    bug_id = "%s-%s" % (bug['repo'].replace('/', '-'), failed_job['id'])

    failing_output_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id, 'failing.log')
    passing_output_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id, 'passing.log')

    old_output_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id, 'travis.log')
    if os.path.exists(old_output_path):
        os.rename(old_output_path, failing_output_path)
    
    print('%s/%s' %(count, len(data)))

    if not os.path.exists(failing_output_path):
        r = requests.get("https://api.travis-ci.org/jobs/%s/log" % pas['id'], headers={'Accept': 'application/vnd.travis-ci.2+json, */*; q=0.01'})

        with open(failing_output_path,'wb') as fd:
            try:
                fd.write(r.content)
            except Exception as identifier:
                print(identifier)
    if not os.path.exists(passing_output_path):
        r = requests.get("https://api.travis-ci.org/jobs/%s/log" % passed_job['id'], headers={'Accept': 'application/vnd.travis-ci.2+json, */*; q=0.01'})

        with open(passing_output_path,'wb') as fd:
            try:
                fd.write(r.content)
            except Exception as identifier:
                print(identifier)