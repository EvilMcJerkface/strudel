#!/usr/bin/python
#
# python script that deploys and runs hbase
# arg: hostname (start|stop|install)
# input:
# {
#   master:
#   namenode:
#   regionservers:
#   java_home?
#   hbase_home?
#   hadoop_home?
# }
import subprocess
import os
import sys
import json
import shutil
import re

import expsystem as es
import hbaseconfig as hb

class HBaseServer:
    def __init__(self, data, host, sys_dirs):
        self.data = data
        self.hconf = hb.HBaseConfig(data)
	self.home = data.get('hbase_home')
	if (self.home is None or self.home == ''):
        	self.home = os.path.join(sys_dirs.home, 'hbase')
	self.hadoop = data.get('hadoop_home')
	if (self.hadoop is None or self.hadoop == ''):
        	self.hadoop = os.path.join(sys_dirs.home, 'hadoop')
        self.data_dir = es.DataRoot(data).dir('hbase-' + es.getuser())
        self.host = host
        self.master = self.hconf.master
        self.java_home = data.get('java_home','')
    
    def install(self):
        es.cleandir(self.data_dir)
        self.hconf.save(self.data_dir)
        #TODO refactor: copying log4j.properties to config dir
        conf = os.path.join(self.data_dir, 'config')
        shutil.copyfile(os.path.join(self.home, 'conf', 'log4j.properties'),
                        os.path.join(conf, 'log4j.properties'))

        # remove hbase home (needs only once - let master do it)
        if (self.host == self.master):
            self.call([os.path.join(self.hadoop, 'bin', 'hadoop'), 'fs',
                   '-rm', '-f', '-r', self.hconf.rootdir])
    def start(self):
        # start at master
        if self.hconf.isMaster(self.host):
            cmd = [os.path.join(self.home, 'bin', 'start-hbase.sh')]
            self.call(cmd, self.env())

    def stop(self):
        # stop at master
        if self.hconf.isMaster(self.host):
            pidfile = os.path.join('/tmp',
                                   'hbase-' + es.getuser() + '-master.pid')
            if (os.path.exists(pidfile)):
                # HMaster running
                cmd = [os.path.join(self.home, 'bin', 'stop-hbase.sh')]
                self.call(cmd, self.env())
            else:
                # do not run stop-hbase.sh, which is not idempotent.
                # it can end up with starting HMaster process...
                print pidfile + ' not found: ignore stopping'
    def env(self):
        env = os.environ.copy()
        env['HBASE_CONF_DIR'] = os.path.join(self.data_dir, 'config')
        env['JAVA_HOME'] = self.java_home
        return env
    def call(self, cmd, env = None):
        p = subprocess.Popen(cmd, env = env)
        ret_code = p.wait()
        if (ret_code != 0):
            raise RuntimeError("command failed with {0} at {1}: {2}".format(
                               ret_code, es.gethostname(), ' '.join(cmd)
                               ))
data = json.load(sys.stdin)

if len(sys.argv) > 2:
    cmd = sys.argv[2]
elif len(sys.argv) > 1:
    cmd = sys.argv[1]
else:
    cmd = 'start'

server = HBaseServer(data, host = sys.argv[1],
                     sys_dirs = es.SysDirs())

if (cmd == 'stop'):
    server.stop()
elif (cmd == 'install'):
    server.install()
else:
    server.start()

