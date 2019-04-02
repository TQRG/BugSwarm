# ===============================================================================
# Copyright 2016 Jake Ross
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
# ============= local library imports  ==========================
from traits.has_traits import provides

from pychron.furnace.furnace_controller import IFurnaceController
from pychron.hardware.core.core_device import CoreDevice


@provides(IFurnaceController)
class NMGRLFurnaceEurotherm(CoreDevice):
    def set_setpoint(self, v, **kw):
        d = json.dumps({'command': 'SetSetpoint', 'setpoint': v})
        self.ask(d)

    def get_setpoint(self, **kw):
        resp = self.ask('GetSetpoint')
        try:
            return float(resp)
        except (TypeError, ValueError):
            pass

    read_setpoint = get_setpoint

    def get_temperature(self, **kw):
        resp = self.ask('GetTemperature', **kw)
        try:
            return float(resp)
        except (TypeError, ValueError):
            pass

    read_temperature = get_temperature

# ============= EOF =============================================
