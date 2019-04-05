# ===============================================================================
# Copyright 2016 ross
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

from traits.api import Str, List, HasTraits, Dict, Any
from traitsui.api import View, Item, EnumEditor


# ============= standard library imports ========================
# ============= local library imports  ==========================


class AddAnalysisGroupView(HasTraits):
    name = Str
    project = Any
    projects = Dict

    def traits_view(self):
        v = View(Item('name'),
                 Item('project', editor=EnumEditor(name='projects')),
                 resizable=True,
                 buttons=['OK', 'Cancel'],
                 title='Add Analysis Group')
        return v

# ============= EOF =============================================
