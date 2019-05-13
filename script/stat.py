import os
import json
import requests
import hashlib
import re
import operator
import datetime

def median(lst):
    n = len(lst)
    if n < 1:
            return None
    if n % 2 == 1:
            return sorted(lst)[n//2]
    else:
            return sum(sorted(lst)[n//2-1:n//2+1])/2.0

ROOT = os.path.join(os.path.dirname(__file__), '..')


global_file_types = {
    "added":  {},
    "deleted": {},
    "modified": {},
    "renamed": {},
}
count_file_actions = {
    "added":  {'Java': 0,'Python': 0},
    "deleted": {'Java': 0,'Python': 0},
    "modified": {'Java': 0,'Python': 0},
    "renamed": {'Java': 0,'Python': 0},
}
count_patch_operations = {
    "added":  {
        '+': {'Java': 0,'Python': 0},
        '-': {'Java': 0,'Python': 0}
    },
    "deleted": {
        '+': {'Java': 0,'Python': 0},
        '-': {'Java': 0,'Python': 0}
    },
    "modified": {
        '+': {'Java': 0,'Python': 0},
        '-': {'Java': 0,'Python': 0}
    }
}

count_age = {
    "Java": [],
    "Python": []
}
count_missing = {'Java': 0,'Python': 0}
count_fix_time_avg = {'dup': {'Java': [],'Python': []}, 'unique': {'Java': [],'Python': []}}
count_execution_time_failing = {'dup': {'Java': [],'Python': []}, 'unique': {'Java': [],'Python': []}}
count_execution_time_passing = {'dup': {'Java': [],'Python': []}, 'unique': {'Java': [],'Python': []}}
count_fix_time = {
    "Java": {
        # 1h
        3600: 0,
        # 6h
        21600: 0,
        # 12h
        43200: 0,
        # 24h
        86400: 0,
        # 36h
        129600: 0,
        # 48h
        172800: 0,
        # 1 week
        604800: 0,
        # 1 month
        2592000: 0,
        # +
        99999999999999: 0
    },
    "Python": {
        # 1h
        3600: 0,
        # 6h
        21600: 0,
        # 12h
        43200: 0,
        # 24h
        86400: 0,
        # 36h
        129600: 0,
        # 48h
        172800: 0,
        # 1 week
        604800: 0,
        # 1 month
        2592000: 0,
        # +
        99999999999999: 0
    }
}
commit_messages = {}
md5_diff = {}
layers = {'Java': {},'Python': {}}
total_size = {'Java': 0,'Python': 0}
layer_size = {'Java': 0,'Python': 0}
count = {'dup': {'Java': 0,'Python': 0}, 'unique': {'Java': 0,'Python': 0}}
projects = {'dup': {'Java': [],'Python': []}, 'unique': {'Java': [],'Python': []}}
buggy_commit = {'dup': {'Java': {},'Python': {}}, 'unique': {'Java': {},'Python': {}}}
correct_commit = {'dup': {'Java': {},'Python': {}}, 'unique': {'Java': {},'Python': {}}}
count_no_source_change = {'dup': {'Java': 0,'Python': 0}, 'unique': {'Java': 0,'Python': 0}}
count_test_failings = {'dup': {'Java': 0,'Python': 0}, 'unique': {'Java': 0,'Python': 0}}
count_categories = {}
count_duplicate_message = {'dup': {'Java': 0,'Python': 0}, 'unique': {'Java': 0,'Python': 0}}

count_duplicate_diff = {'dup': {'Java': 0,'Python': 0}, 'unique': {'Java': 0,'Python': 0}}
count_duplicate_commit = {'dup': {'Java': 0,'Python': 0}, 'unique': {'Java': 0,'Python': 0}}
count_duplicate_commit_passed = {'dup': {'Java': 0,'Python': 0}, 'unique': {'Java': 0,'Python': 0}}
count_only_test_change = {'dup': {'Java': 0,'Python': 0}, 'unique': {'Java': 0,'Python': 0}}
count_only_source_change = {'dup': {'Java': 0,'Python': 0}, 'unique': {'Java': 0,'Python': 0}}
count_test_change = {'dup': {'Java': 0,'Python': 0}, 'unique': {'Java': 0,'Python': 0}}
count_diff_test_change = {'dup': {'Java': 0,'Python': 0}, 'unique': {'Java': 0,'Python': 0}}
empty = {'dup': {'Java': 0,'Python': 0}, 'unique': {'Java': 0,'Python': 0}}

data = None
with open(os.path.join(ROOT, 'docs', 'original_bugswarm_builds.json')) as fd:
    data = json.load(fd)

categories = None
with open(os.path.join(ROOT, 'docs', 'category.json')) as fd:
    categories = json.load(fd)

travis_jobs = {}
travis_jobs_path = os.path.join(ROOT, 'docs', 'travis_data.json')
if os.path.exists(travis_jobs_path):
    with open(travis_jobs_path) as fd:
        travis_jobs = json.load(fd)

images = []
with open(os.path.join(ROOT, 'cloned_branches.txt')) as fd:
    images = fd.read().splitlines()

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

to_clean = []
final_bench = []

def get_file_extension(f):
    extension = os.path.basename(f).lower()
    try:
        index = extension.rindex('.')
        extension = extension[index+1:]
    except Exception:
        pass
    extension = extension.strip()
    return extension

for bug in data:
    passed_job = travis_jobs[str(bug['passed_job']['job_id'])]
    failed_job = travis_jobs[str(bug['failed_job']['job_id'])]
    repo = failed_job['repository_slug']
    commit_id = failed_job['commit']['sha']
    passed_commit_id = passed_job['commit']['sha']
    lang = bug['lang']
    type_extension = 'java'
    if lang == 'Python':
        type_extension = 'py'

    bug_id = "%s-%s" % (bug['repo'].replace('/', '-'), failed_job['id'])

    is_duplicate = False
    duplicate_key = 'unique'

    if commit_id not in buggy_commit['dup'][lang]:
        buggy_commit['dup'][lang][commit_id] = 0
    else:
        is_duplicate = True
        duplicate_key = 'dup'
        count_duplicate_commit[duplicate_key][lang] += 1
    buggy_commit['dup'][lang][commit_id] += 1
    
    count[duplicate_key][lang] += 1

    diff_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id, 'patch.diff')

    if bug_id in categories and 'failure_category' in categories[bug_id]:
        if categories[bug_id]['failure_category'] not in count_categories:
            count_categories[categories[bug_id]['failure_category']] = {'dup': {'Java': 0,'Python': 0}, 'unique': {'Java': 0,'Python': 0}}
        count_categories[categories[bug_id]['failure_category']][duplicate_key][lang] += 1

    if passed_job['commit']['message'] is not None:
        message = passed_job['commit']['message'].lower()
        if message not in commit_messages:
            commit_messages[message] = 0
        else:
            count_duplicate_message[duplicate_key][lang]+= 1
        commit_messages[message] += 1
    
    if bug['failed_job']['committed_at'] is not None:
        failed_commit_date = datetime.datetime.strptime(bug['failed_job']['committed_at'], '%Y-%m-%dT%H:%M:%SZ')
        count_age[lang].append((datetime.datetime.now() - failed_commit_date).total_seconds())

    if passed_job['started_at'] is not None and passed_job['finished_at'] is not None:
        start_date = datetime.datetime.strptime(passed_job['started_at'], '%Y-%m-%dT%H:%M:%SZ')
        end_date =  datetime.datetime.strptime(passed_job['finished_at'], '%Y-%m-%dT%H:%M:%SZ')
        time_diff = (end_date - start_date).total_seconds()
        count_execution_time_passing[duplicate_key][lang].append(time_diff)
    if failed_job['started_at'] is not None and failed_job['finished_at'] is not None:
        start_date = datetime.datetime.strptime(failed_job['started_at'], '%Y-%m-%dT%H:%M:%SZ')
        end_date =  datetime.datetime.strptime(failed_job['finished_at'], '%Y-%m-%dT%H:%M:%SZ')
        time_diff = (end_date - start_date).total_seconds()
        count_execution_time_failing[duplicate_key][lang].append(time_diff)
    if bug['failed_job']['committed_at'] is not None and not is_duplicate:
        if passed_job['commit']['committed_at'] is not None:
            failed_commit_date = datetime.datetime.strptime(bug['failed_job']['committed_at'], '%Y-%m-%dT%H:%M:%SZ')
            passed_commit_date =  datetime.datetime.strptime(passed_job['commit']['committed_at'], '%Y-%m-%dT%H:%M:%SZ')
            time_diff = (passed_commit_date - failed_commit_date).total_seconds()
            count_fix_time_avg[duplicate_key][lang].append(time_diff)
            for (d, v) in sorted(count_fix_time['Java'].items(), key=operator.itemgetter(0)):
                if time_diff < d:
                    count_fix_time[lang][d] += 1
                    break
    image_manifest_path = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id, 'docker_manifest.json')
    if os.path.exists(image_manifest_path):
        with open(image_manifest_path, 'r') as fd:
            image_manifest = json.load(fd)
            for layer in image_manifest['layers']:
                total_size[lang] += layer['size']/1073741824
                if layer['digest'] not in layers[lang]:
                    layers[lang][layer['digest']] = layer['size']/1073741824
                    layer_size[lang] += layer['size']/1073741824
    else:
        count_missing[lang] += 1
        print("Missing docker image", bug_id)

    if not os.path.exists(diff_path):
        # empty[duplicate_key][lang] += 1
        continue
    diff = None
    with open(diff_path, encoding = "ISO-8859-1") as fd:
        diff = fd.read()
        diff = re.sub(r'index .{10}\.\..{10}', 'index a..b', diff)
    
    
    m = hashlib.md5()
    m.update(diff.encode('utf-8'))
    key = m.hexdigest()
    if key in md5_diff:
        if len(diff) > 0:
            count_duplicate_diff[duplicate_key][lang] += 1
    else:
        md5_diff[key] = []
    md5_diff[key].append(bug_id)


    all_files = get_changed_files(diff)
    files = list(set(all_files['modified']))

    #print('%s/%s' %(count, len(data)))
    root_buggy = os.path.join(ROOT, 'docs', 'BugSwarm', bug_id, 'buggy_files/')

    file_types = {}
    tests = []
    is_test_changed = False
    for f  in files:
        if 'tests/' in f or 'test/' in f or 'Test.java' in f or 'test_' in f or 'spec/' in f or 'checks.py' in f or '_test.py' in f:
            is_test_changed = True
            count_test_change[duplicate_key][lang] += 1
            tests.append(f)
        extension = get_file_extension(f)

        if extension not in file_types:
            file_types[extension] = 0    
        file_types[extension] += 1
    if is_test_changed:
        count_diff_test_change[duplicate_key][lang] += 1
    if not is_duplicate: 
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
            elif '+++' == line[:3] or line[0] == ' ':
                continue
            elif line[0] == '+' or line[0] == '-':
                count_patch_operations[action][line[0]][lang] += 1
        
        for action in global_file_types:
            for f in all_files[action]:
                count_file_actions[action][lang] += 1
                extension = get_file_extension(f)
                if extension not in global_file_types[action]:
                    global_file_types[action][extension] = 0   
                global_file_types[action][extension] += 1
    is_only_test = len(tests) == len(files) and len(files) > 0
    if len(file_types) == 1 and type_extension in file_types and len(tests) == 0:
        count_only_source_change[duplicate_key][lang] += 1
    if repo not in projects['dup'][lang]:
        projects['dup'][lang].append(repo)

    if len(files) == 0 and len(all_files['added']) == 0 and len(all_files['deleted']) == 0 and len(all_files['binary']['added']) == 0 and len(all_files['binary']['deleted']) == 0 and len(all_files['binary']['modified']) == 0:
        # to_clean.append(bug_id)
        empty[duplicate_key][lang] += 1 
        print(bug_id, "https://github.com/%s/compare/%s...%s" %(repo, commit_id, passed_commit_id))    
    else:
        if is_only_test:
            count_only_test_change[duplicate_key][lang] += 1
        if len(bug['failed_job']['failed_tests']) > 0:
            count_test_failings[duplicate_key][lang] += 1
        if type_extension not in file_types:
            count_no_source_change[duplicate_key][lang] += 1


def avg_time(l):
    time = str(datetime.timedelta(seconds=round(sum(l)/len(l))))
    if time[:2] == '0:':
        time = time[2:]
    return time
def median_time(l):
    time = str(datetime.timedelta(seconds=round(median(l))))
    if time[:2] == '0:':
        time = time[2:]
    return time

def print_latex_line_avg(label, data):
    if 'dup' in data:
        print("Avg. %s & {%s} & {%s} & {%s} & {%s} & {%s} & {%s} \\\\" % (
                label, 
                avg_time(data['dup']['Java'] + data['unique']['Java']), 
                avg_time(data['dup']['Python'] + data['unique']['Python']), 
                avg_time(data['dup']['Java']+ data['dup']['Python']+ data['unique']['Java'] + data['unique']['Python']),
                avg_time(data['unique']['Java']), 
                avg_time(data['unique']['Python']), 
                avg_time(data['unique']['Java']+ data['unique']['Python'])))
        print("Med. %s & {%s} & {%s} & {%s} & {%s} & {%s} & {%s} \\\\" % (
                label, 
                median_time(data['dup']['Java'] + data['unique']['Java']), 
                median_time(data['dup']['Python'] + data['unique']['Python']), 
                median_time(data['dup']['Java']+ data['dup']['Python']+ data['unique']['Java'] + data['unique']['Python']),
                median_time(data['unique']['Java']), 
                median_time(data['unique']['Python']), 
                median_time(data['unique']['Java']+ data['unique']['Python'])))

    else:
        print("Avg. %s & {%s} & {%s} & {%s} \\\\" % (label, avg_time(data['Java']), avg_time(data['Python']), avg_time(data['Java'] + data['Python'])))
        print("Med. %s & {%s} & {%s} & {%s} \\\\" % (label, median_time(data['Java']), median_time(data['Python']), median_time(data['Java'] + data['Python'])))

def print_latex_line(label, data):
    if 'dup' in data:
        if isinstance(data['unique']['Java'], list) or isinstance(data['unique']['Java'], dict):
            print("%s & \\numprint{%s} & \\numprint{%s} & \\numprint{%s} & \\numprint{%s} & \\numprint{%s} & \\numprint{%s} \\\\" % (
                label, 
                len(data['dup']['Java']) + len(data['unique']['Java']), 
                len(data['dup']['Python']) + len(data['unique']['Python']), 
                len(data['dup']['Java']) + len(data['dup']['Python']) + len(data['unique']['Java']) + len(data['unique']['Python']),
                len(data['unique']['Java']), 
                len(data['unique']['Python']), 
                len(data['unique']['Java']) + len(data['unique']['Python'])))
        else:
            print("%s & \\numprint{%s} & \\numprint{%s} & \\numprint{%s} & \\numprint{%s} & \\numprint{%s} & \\numprint{%s} \\\\" % (label, 
                (data['dup']['Java']) + (data['unique']['Java']), 
                (data['dup']['Python']) + (data['unique']['Python']), 
                (data['dup']['Java']) + (data['dup']['Python']) + (data['unique']['Java']) + (data['unique']['Python']),
                (data['unique']['Java']), 
                (data['unique']['Python']), 
                (data['unique']['Java']) + (data['unique']['Python'])))
    else:
        print("%s & \\numprint{%s} & \\numprint{%s} & \\numprint{%s} \\\\" % (label, (data['Java']), (data['Python']), (data['Java']) + (data['Python'])))

print("\n#### Table 2 ####\n")
print("\# Pair of builds presented in \\bugswarm & \\numprint{1827} & \\numprint{1264} & \\numprint{3091} \\\\")
print_latex_line('\# Pair of builds reproduced 5 times', {
    'Java': count['dup']['Java'] + count['unique']['Java'],
    'Python':count['dup']['Python'] + count['unique']['Python']})
print_latex_line('\# Pair of builds without duplicate commit', count['unique'])
print_latex_line('\# Docker image not available', count_missing)

print_latex_line('\# Duplicate commit', count_duplicate_commit)
print_latex_line_avg('age', count_age)
print_latex_line('\# project', projects)

print("\n#### Table 3 ####\n")
print_latex_line('\# Empty diff', empty['unique'])
print_latex_line('\# Duplicate diff', count_duplicate_diff['unique'])
print_latex_line('\# Duplicate message', count_duplicate_message['unique'])
print_latex_line('\# Diff that changes source', {
    'dup': {
        'Java': count['dup']['Java'] - count_no_source_change['dup']['Java'],
        'Python': count['dup']['Python'] - count_no_source_change['dup']['Python']
    },
    'unique': {
        'Java': count['unique']['Java'] - count_no_source_change['unique']['Java'],
        'Python': count['unique']['Python'] - count_no_source_change['unique']['Python']
    }
}['unique'])
print_latex_line('\# Diff that only changes source', count_only_source_change['unique'])

print("\n#### Table 4 ####\n")
print_latex_line_avg('fix time', count_fix_time_avg['unique'])
print_latex_line_avg('execution time passing', count_execution_time_passing['unique'])
print_latex_line_avg('execution time failing', count_execution_time_failing['unique'])

print("\n#### Table 5 ####\n")
print_latex_line('\# Modified file', count_file_actions['modified'])
print_latex_line('\# Added file', count_file_actions['added'])
print_latex_line('\# Removed file', count_file_actions['deleted'])

nb_changed_java_file = count_file_actions['modified']['Java'] + count_file_actions['added']['Java'] + count_file_actions['deleted']['Java']
nb_changed_python_file = count_file_actions['modified']['Python'] + count_file_actions['added']['Python'] + count_file_actions['deleted']['Python']
print('Avg. \# changed file', '&', round(nb_changed_java_file/count['unique']['Java']), '&', round(nb_changed_python_file/count['unique']['Python']), '&', round((nb_changed_java_file + nb_changed_python_file)/(count['unique']['Java']+count['unique']['Python'])), '\\\\')

print("\n#### Table 6 ####\n")
print_latex_line('\# Added lines in modified file', count_patch_operations['modified']['+'])
print_latex_line('\# Removed lines in modified file', count_patch_operations['modified']['-'])
print_latex_line('\# Added lines in added file', count_patch_operations['added']['+'])
print_latex_line('\# Removed lines in removed file', count_patch_operations['deleted']['-'])
nb_operation_java = count_patch_operations['modified']['+']['Java'] + count_patch_operations['modified']['-']['Java'] + count_patch_operations['added']['+']['Java'] + count_patch_operations['deleted']['-']['Java']
nb_operation_python = count_patch_operations['modified']['+']['Python'] + count_patch_operations['modified']['-']['Python'] + count_patch_operations['added']['+']['Python'] + count_patch_operations['deleted']['-']['Python']
print('Avg. patch size', '&', round(nb_operation_java/count['unique']['Java']), '&', round(nb_operation_python/count['unique']['Python']), '&', round((nb_operation_java + nb_operation_python)/(count['unique']['Java']+count['unique']['Python'])), '\\\\')

print("\n#### Table 7 ####\n")

labels = {
    'Test': 'Test failure',
    'Checkstyle': 'Checkstyle',
    'Compilation': 'Compilation error',
    'Documentation': 'Doc generation',
    'License': 'Missing license',
    'Missing library': 'Dependency error',
    'Compare  version': 'API Regression',
    'Unable to clone': 'Unable to clone',
    'Execution': 'Missing main file',
    'Unknown': 'Unknown'
}
for category in labels:
    print_latex_line(labels[category], count_categories[category])    

print("\n#### Table 8 ####\n")
print_latex_line('\\bugswarm Docker layer size in GB', total_size)
print_latex_line('\\bugswarm unique Docker layer size in GB', layer_size)
print('Avg. size in GB', '&', 
round(total_size['Java']/(count['unique']['Java'] + count['dup']['Java']), 2), '&',
round(total_size['Python']/(count['unique']['Python'] + count['dup']['Python']), 2), '&',
round((total_size['Java'] + total_size['Python'])/(count['unique']['Java'] + count['dup']['Java']+count['unique']['Python'] + count['dup']['Python']), 2), '\\\\')
print('Download all layers (\\numprint[Mbits/s]{80})', '&',
    datetime.timedelta(seconds=round( total_size['Java'] * 107)), '&',
    datetime.timedelta(seconds=round( total_size['Python'] * 107)), '&',
    datetime.timedelta(seconds=round( (total_size['Java'] + total_size['Python']) * 107)), '\\\\')
unique_download_time = datetime.timedelta(seconds=round( (layer_size['Java'] + layer_size['Python']) * 107))
print('Download unique layers (\\numprint[Mbits/s]{80})', '&',
    datetime.timedelta(seconds=round( layer_size['Java'] * 107)), '&',
    datetime.timedelta(seconds=round( layer_size['Python'] * 107)), '&',
    unique_download_time, '\\\\')

compression_rate = 0.41252209160908204
bugswarm_size_on_disk = (total_size['Java'] + total_size['Python']) * compression_rate # in GB
compute_cost = 0.1664 # per hour
storage_cost = 0.10 # per GB per month
storage_cost_hour = 0.10 / 30 / 24 # per GB

nb_hour_to_download = unique_download_time.total_seconds() / 3600


print("Renting machine cost", nb_hour_to_download * compute_cost)
print("Storage cost per hour", storage_cost_hour * bugswarm_size_on_disk)
print("Storage cost", nb_hour_to_download * storage_cost_hour * bugswarm_size_on_disk)

print("\n#### Others ####\n")
print_latex_line('\# test change', count_test_change)
print_latex_line('\# diff that changes tests', count_diff_test_change)
print_latex_line('\# test failings', count_test_failings)
print_latex_line('\# only test change', count_only_test_change)





print("\n#### Figures ####\n")

print('\n#### # file type impacted #### \n')

for action in global_file_types:
    header = []
    line = ''
    for (t, v) in sorted(global_file_types['modified'].items(), key=operator.itemgetter(1), reverse=True):
        header.append(t)
        v = 0
        if t in global_file_types[action]:
            v = global_file_types[action][t]
        line += '(%s, %s) ' % (t.upper(), v)
        if len(header) == 10:
            break
    print(action)
    print(','.join(header))
    print(line)

print('\n#### Time to fix #### \n')
fix_time_label = ['$<$1h','$<$6h','$<$12h','$<$24h','$<$36h','$<$48h','$<$1w','$<$1m','$>$1m']

line = ''
index = 0
for time in count_fix_time['Java']:
    line += '(%s, %s) ' % (fix_time_label[index], count_fix_time['Java'][time])
    index += 1
print(line)
line = ''
index = 0
for time in count_fix_time['Python']:
    line += '(%s, %s) ' % (fix_time_label[index], count_fix_time['Python'][time])
    index += 1
print(line)

print('\n#### Top 10 commit messages #### \n')

i = 0
for (message, value) in  sorted(commit_messages.items(), key=operator.itemgetter(1), reverse=True):
    if i == 10:
        break
    i += 1
    print(message, value)



for f in sorted(to_clean):
    if f in images:
        images.remove(f)
    diff_path = os.path.join(ROOT, 'docs', 'BugSwarm', f, 'patch.diff')
    os.remove(diff_path)
print("# to clean", len(to_clean))
with open(os.path.join(ROOT, 'docs', 'cloned_branches.txt'), 'w') as fd:
    fd.write('\n'.join(images))