#!/usr/bin/python
#
# python script that deploys and runs tokumx
# arg: hostname (start|stop)
#
# NOTE
# TokuMX will not run with transparent huge pages enabled.
# To disable:
# (echo never > /sys/kernel/mm/transparent_hugepage/enabled)
import subprocess
import os
import sys
import json
import shutil
import re

import expsystem as es

mongos_port = 27017

class TokumxServer:
    def __init__(self, param, host, script_home):
        self.param = param
        self.host = host
        self.dir = es.DataRoot(param).dir('tokumx-' + es.getuser())
	self.mongobin = param.get('tokumx_bin')
	if (self.mongobin is None or self.mongobin == ''):
        	self.mongobin = os.path.join(os.path.dirname(script_home),
                                     'tokumx', 'bin')
    def install(self):
        es.cleandir(self.dir)
    def start(self, install = True):
        if (install):
            self.install()
        slog = es.SysLog('tokumx')
        slog.rotate(10)
        subprocess.check_call("{0} --fork --dbpath {1} --logpath {2}".format(
                            os.path.join(self.mongobin, 'mongod'), self.dir, slog.getpath()),
                              shell=True)
    def stop(self):
        subprocess.call([os.path.join(self.mongobin, 'mongod'), '--shutdown',
                     '--dbpath', self.dir])


data = json.load(sys.stdin)

if len(sys.argv) > 2:
    cmd = sys.argv[2]
elif len(sys.argv) > 1:
    cmd = sys.argv[1]
else:
    cmd = 'start'

sd = es.SysDirs()
server = TokumxServer(data, host = sys.argv[1], script_home = sd.script_home)

if (cmd == 'stop'):
    server.stop()
else:
    server.start()

