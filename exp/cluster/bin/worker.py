#!/usr/bin/python
#
#
import subprocess
import os
import sys
import json

import expsystem as es

class WorkerServer:
    def __init__(self, param, lib_dir, worker_bin):
        self.param = param
        self.lib_dir = lib_dir
        self.worker_bin = worker_bin
 
    def stop(self):
        subprocess.call([os.path.join(self.worker_bin, 'stop-worker.sh')])
    def start(self):
        slog = es.SysLog('worker')
        slog.rotate(10)
        es.waitforport(self.jmxport())
        es.Server(os.path.join(self.worker_bin, 'worker'),
                  syslog = slog.getpath(),
                  env = self.getenv()).start()
    def jmxport(self):
        try:
            return int(self.param['jmx_port'])
        except (ValueError, KeyError):
            return 9099
    def port(self):
        try:
            return int(self.param['port'])
        except (ValueError, KeyError):
            return -1
    def getenv(self):
        env = os.environ.copy()
        opts = "-server "
        port = self.port()
        if (port > 0):
            opts += "-Dworker.port={0} ".format(port)
        jmxport = self.jmxport()
        if (jmxport > 0):
            opts += "-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port={0} ".format(jmxport)
        opts += "-Dstrudel.classpath.base={0} ".format(self.lib_dir)
        env['STRUDEL_WKLD_OPTS'] = opts + self.param.get('jvm_opt','-Xmx1G')
        return env



if len(sys.argv) > 2:
    cmd = sys.argv[2]
else:
    cmd = 'restart'

data = json.load(sys.stdin)

sys_dirs = es.SysDirs()

server = WorkerServer(data, sys_dirs.home,
                      os.path.join(sys_dirs.home, 'strudel', 'bin'));

server.stop()
if (cmd == 'restart'):
    server.start()

