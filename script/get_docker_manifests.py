import os
import json
import requests

repo = "bugswarm/images"


def get_manifests(tag):
    login_template = "https://auth.docker.io/token?service=registry.docker.io&scope=repository:{repository}:pull"
    token = requests.get(login_template.format(repository=repo), json=True).json()["token"]
    get_manifest_template = "https://registry.hub.docker.com/v2/{repository}/manifests/{tag}"
    manifest = requests.get(
        get_manifest_template.format(repository=repo, tag=tag),
        headers={
            "Authorization": "Bearer {}".format(token),
            "Accept": "application/vnd.docker.distribution.manifest.v2+json"},
        json=True).json()
    return manifest

ROOT = os.path.join(os.path.dirname(__file__), '..')

data = None
with open(os.path.join(ROOT, 'docs', 'bugswarm.json')) as fd:
    data = json.load(fd)

count = 0
for bug in data:
    bug_id = "%s-%s" % (bug['repo'].replace('/', '-'), bug['failed_job']['job_id'])
    bug_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id)

    count += 1

    manifest_path = os.path.join(bug_path, 'docker_manifest.json')
    if os.path.exists(manifest_path):
        data = None
        with open(manifest_path) as fd:
            data = json.load(fd)
        if 'errors' not in data:            
            continue
        os.remove(manifest_path)
    manifest = get_manifests(bug_id)
    if 'errors' in manifest:
        print(bug_id)
        continue
    size = 0
    if 'layers' in manifest:
        for layer in manifest['layers']:
            size += layer['size']
    print(count, int(count*100/3091), bug_id, size/1000000000)

    with open(manifest_path, 'w') as fd:
        json.dump(manifest, fd, indent=True)

