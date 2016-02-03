#!/usr/bin/python
#
# python script that deploys and runs omid
# arg: hostname (start|stop)
import subprocess
import os
import sys
import json
import shutil
import re

import expsystem as es
import hbaseconfig as hb


class OmidServer:
    def __init__(self, param, host, script_home):
        self.param = param
        self.host = host
        self.src = os.path.join(os.path.dirname(script_home),
                                'omid', 'tso-server');
        self.dir = es.DataRoot(param).dir('omid-' + es.getuser())
        self.omid = os.path.join(self.dir,
                                 'bin', 'omid.sh')

    def start(self, syslog_file = ""):
        # create tables (TODO only when this is the first host)
        numSplits = self.param.get('commitTableSplit', 16)
        subprocess.call("{0} create-hbase-commit-table -numSplits {1}".format(
                                                            self.omid, numSplits),
                               shell=True)
        subprocess.call("{0} create-hbase-timestamp-table".format(self.omid),
                                                      shell=True)
        slog = es.SysLog('omid')
        slog.rotate(10)
        if (syslog_file == ""):
            syslog_file = slog.getpath()
        es.Server("{0} tso".format(self.omid),
                  env = self.env(), syslog = syslog_file).start()
    def install(self):
        es.cleandir(self.dir)
        for d in ['lib', 'target']:
            os.symlink(os.path.join(self.src, d), os.path.join(self.dir, d))
        for d in ['bin', 'conf']:
            shutil.copytree(os.path.join(self.src, d),
                            os.path.join(self.dir, d))
        # create hbase-site.xml in conf
        hb.HBaseConfig(self.param['hbase']).save_hbase_site(
                        os.path.join(self.dir, 'conf'))
        # create omid.conf from param['opt']
        self.save_conf(os.path.join(self.dir, 'conf'))
    def save_conf(self, dir):
        opt = {
            'port': 54758,
            'maxItems': 100000000,
            'timestampStore': 'HBASE',
            'commitTableStore': 'HBASE',
            'metricsProvider': 'CODAHALE',
            'metricsConfigs': 'console:_:60:SECONDS'
        }
        opt.update(self.param.get('opt', {}))
        with open(os.path.join(dir, 'omid.conf'), 'w') as outfile:
            for (k,v) in opt.iteritems():
                if v is not None:
                    outfile.write("-{0}\n{1}\n".format(k,v))


    def env(self):
        env = os.environ.copy()
        env['JVM_FLAGS'] = self.param.get('jvm_opt', '-server')
        env.update(self.param.get('env', {}))
        return env
    def stop(self):
        subprocess.call("kill -9 $(jps | grep 'TSOServer' | awk '{print $1}')", shell=True)

data = json.load(sys.stdin)

if len(sys.argv) > 2:
    cmd = sys.argv[2]
elif len(sys.argv) > 1:
    cmd = sys.argv[1]
else:
    cmd = 'start'

sd = es.SysDirs()
server = OmidServer(data, host = sys.argv[1], script_home = sd.script_home)

if (cmd == 'stop'):
    server.stop()
elif (cmd == 'install'):
    server.install()
else:
    server.start()
