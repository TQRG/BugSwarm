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
# ============= standard library imports ========================
import json
import os
# ============= local library imports  ==========================


__version__ = '0.1'


def dvc_dump(obj, path):
    with open(path, 'w') as wfile:
        try:
            json.dump(obj, wfile, indent=4, sort_keys=True)
        except TypeError:
            print 'dvc dump exception {}'.format(obj)


def dvc_load(path):
    if os.path.isfile(path):
        with open(path, 'r') as rfile:
            return json.load(rfile)
    else:
        return {}

# ============= EOF =============================================



