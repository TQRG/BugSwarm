import os
import json
import requests
import re
import hashlib

ROOT = os.path.join(os.path.dirname(__file__), '..')

md5_diff = {}

def get_file_extension(f):
    extension = os.path.basename(f).lower()
    try:
        index = extension.rindex('.')
        extension = extension[index+1:]
    except Exception:
        pass
    extension = extension.strip()
    return extension

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


data = None
with open(os.path.join(ROOT, 'docs', 'original_bugswarm_builds.json')) as fd:
    data = json.load(fd)

failure_types = None
with open(os.path.join(ROOT, 'docs', 'failure_types.json')) as fd:
    failure_types = json.load(fd)

images = []
with open(os.path.join(ROOT, 'docs', 'cloned_branches.txt')) as fd:
    images = fd.read().splitlines()

travis_jobs = {}
travis_jobs_path = os.path.join(ROOT, 'docs', 'travis_data.json')
if os.path.exists(travis_jobs_path):
    with open(travis_jobs_path) as fd:
        travis_jobs = json.load(fd)

output = []
to_clean = []
commits = {}

for bug in data:
    failed_job = travis_jobs[str(bug['failed_job']['job_id'])]
    passed_job =  travis_jobs[str(bug['passed_job']['job_id'])]
    commit_id = failed_job['commit']['sha']
    lang = bug['lang']
    type_extension = 'java'
    if lang == 'Python':
        type_extension = 'py'

    bug_id = "%s-%s" % (bug['repo'].replace('/', '-'), failed_job['id'])

    diff = ''
    diff_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id, 'patch.diff')
    if os.path.exists(diff_path):
        with open(diff_path, encoding = "ISO-8859-1") as fd:
            diff = re.sub(r'index .{10}\.\..{10}', 'index a..b', fd.read())
    manifest_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id, 'docker_manifest.json')
    bug['available'] = os.path.exists(manifest_path)
    if bug['available']:
        with open(manifest_path, 'r') as fd:
            image_manifest = json.load(fd)
            bug['size'] = 0
            for layer in image_manifest['layers']:
                bug['size'] += layer['size']/1073741824
    bug['files'] = get_changed_files(diff)
    if bug_id in failure_types:
        bug['failure_type'] = failure_types[bug_id]
    tests = []
    file_types = {}
    for action in bug['files']:
        if action == 'binary':
            continue
        for f  in bug['files'][action]:
            if 'tests/' in f or 'test/' in f or 'Test.java' in f or 'test_' in f or 'spec/' in f or 'checks.py' in f:
                tests.append(f)
                continue
            extension = get_file_extension(f)

            if extension not in file_types:
                file_types[extension] = 0    
            file_types[extension] += 1

    bug['tests'] = tests
    bug['changed_test'] = len(tests) > 0
    bug['has_source'] = type_extension in file_types
    bug['only_source'] = bug['has_source'] and len(file_types) == 1

    bug['unique'] = commit_id not in commits

    if commit_id not in commits:
        commits[commit_id] = 0
    commits[commit_id] += 1
    
    added_lines = 0
    removed_lines = 0
    patch_size = 0
    nb_files = 0

    m = hashlib.md5()
    m.update(diff.encode('utf-8'))
    key = m.hexdigest()
    if key in md5_diff:
        if len(diff) > 0:
            bug['unique_diff'] = False
    else:
        md5_diff[key] = []
        bug['unique_diff'] = True
    md5_diff[key].append(bug_id)

    
    lines = diff.splitlines()
    action = None
    for i in range(0, len(lines) - 1):
        line = lines[i]
        next_line = lines[i+1]
        if len(line) == 0:
            continue
        elif '---' == line[:3] and '+++' == next_line[:3]:
            action = 'modified'
            if line[4:] == '/dev/null':
                action = 'added'
            elif next_line[4:] == '/dev/null':
                action = 'deleted'
            nb_files += 1
        elif '+++' == line[:3] or line[0] == ' ':
            continue
        elif line[0] == '+' and action == 'modified':
            added_lines +=1
            patch_size += 1
        elif line[0] == '-' and action == 'modified':
            patch_size += 1
            removed_lines += 1
    
    if (added_lines == 1 and (removed_lines == 1 or removed_lines == 0)) or (removed_lines == 1 and (added_lines == 1 or added_lines == 0)):
        bug['repairPatterns'] = {
            'singleLine': 1
        }
    bug['b_metrics'] = bug['metrics']
    bug['metrics'] = {
        'addedLines': added_lines,
        'removedLines': removed_lines,
        'patchSize': patch_size,
        'nbFiles': nb_files
    }
    bug['benchmark'] = 'BugSwarm'
    bug['bugId'] = bug_id

    output.append(bug)

for f in sorted(to_clean):
    if f in images:
        images.remove(f)
    diff_path = os.path.join(ROOT, 'docs', 'BugSwarm', f, 'patch.diff')
    os.remove(diff_path)
with open(os.path.join(ROOT, 'docs', 'cloned_branches.txt'), 'w') as fd:
    fd.write('\n'.join(images))
with open(os.path.join(ROOT, 'docs', 'bugswarm.json'), 'w') as fd:
    json.dump(output, fd)