import os
import json
import requests

ROOT = os.path.join(os.path.dirname(__file__), '..')

images = []
with open(os.path.join(ROOT, 'docs', 'commits.txt')) as fd:
    lines = fd.read().splitlines()
    for l in lines:
        (commit, branch) = l.split('\t')
        if branch == 'HEAD' or branch == 'master':
            continue
        images.append({
            'branch': branch,
            'commit': commit
        })

count = 0
for image in images:
    bug_id = image['branch']

    bug_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id)
    diff_path = os.path.join(bug_path, 'patch.diff')

    if os.path.exists(diff_path):
        continue

    url = "https://github.com/%s/commit/%s.diff" % ('tdurieux/BugSwarm', image['commit'])
    r = requests.get(url)
    if r.status_code != 200:
        if os.path.exists(diff_path):
            os.remove(diff_path)
            continue
    count += 1
    content = r.content
    print(count, int(count*100/3091), bug_id, url, r.status_code)

    if not os.path.exists(bug_path):
        os.makedirs(bug_path)
    
    with open(diff_path, 'wb') as fd:
        fd.write(content)
    
print(count)