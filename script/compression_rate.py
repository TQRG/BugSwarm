import os
import json

ROOT = os.path.join(os.path.dirname(__file__), '..')

data = None
with open(os.path.join(ROOT, 'docs', 'original_bugswarm_builds.json')) as fd:
    data = json.load(fd)

downloaded_images = []
with open(os.path.join(ROOT, 'docs', 'downloaded_images.json')) as fd:
    downloaded_images = json.load(fd)

space_on_disk = 643160473600 # bytes

downloaded_size = 0
for bug in data:
    bug_id = "%s-%s" % (bug['repo'].replace('/', '-'), bug['failed_job']['job_id'])
    bug_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id)

    manifest_path = os.path.join(bug_path, 'docker_manifest.json')
    if os.path.exists(manifest_path):
        with open(manifest_path) as fd:
            image_manifest = json.load(fd)
            for layer in image_manifest['layers']:
                downloaded_size += layer['size']


total_size = 0
total_manifest_size = 0
for image in downloaded_images:
    total_size += float(image['Size'])
    bug_id = image['Tag']

    image_manifest_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id, 'docker_manifest.json')
    with open(image_manifest_path, 'r') as fd:
        image_manifest = json.load(fd)
        for layer in image_manifest['layers']:
            total_manifest_size += layer['size']
print(total_size/(total_manifest_size/1073741824))
ratio = space_on_disk/(total_manifest_size)
print(space_on_disk / 470 * len(data) / 1073741824)
print(downloaded_size/(space_on_disk / 1073741824))
print('Ratio', ratio)
print('Total size', downloaded_size * ratio / 1073741824)