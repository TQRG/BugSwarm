########
# Copyright (c) 2017 GigaSpaces Technologies Ltd. All rights reserved
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
#    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    * See the License for the specific language governing permissions and
#    * limitations under the License.


import time
import os
from base64 import standard_b64encode


class HighAvailabilityHelper(object):
    @staticmethod
    def set_active(manager, cfy, logger):
        try:
            logger.info('Setting active manager %s',
                        manager.private_ip_address)
            cfy.cluster('set-active', manager.index)
        except Exception as e:
            logger.info('Setting active manager error message: %s', e.message)
        finally:
            time.sleep(120)

    @staticmethod
    def wait_leader_election(logger):
        logger.info('Waiting for a leader election...')
        time.sleep(120)

    @staticmethod
    def verify_nodes_status(manager, cfy, logger):
        logger.info('Verifying that manager %s is a master '
                    'and others are standby', manager.private_ip_address)
        cfy.cluster.nodes.list()
        nodes = manager.client.cluster.nodes.list()

        for node in nodes:
            if node.name == str(manager.index):
                assert node.master is True
            else:
                assert node.master is False

    @staticmethod
    def create_encription_key():
        return standard_b64encode(os.urandom(16))
