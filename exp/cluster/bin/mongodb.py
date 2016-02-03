#!/usr/bin/python
#
# python script that deploys and runs mongodb
# arg: hostname (start-configsvr|stop-configsvr
#|start-shard|stop-shard|start-mongos|stop-mongos)
import subprocess
import os
import sys
import json
import shutil
import re

import expsystem as es

mongos_port = 27017
shardsvr_port = 27018
configsvr_port = 27019

class ConfigServer:
    def __init__(self, param, host, script_home):
        self.param = param
        self.host = host
        self.dir = es.DataRoot(param).dir('mongo-configdb-' + es.getuser())
        self.mongobin = os.path.join(os.path.dirname(script_home),
                                   'mongodb', 'bin')
    def install(self):
        es.cleandir(self.dir)
    def start(self, install = True):
        if (install):
            self.install()
        slog = es.SysLog('mongoconfigsvr')
        slog.rotate(10)
        subprocess.check_call("{0} --fork --configsvr --dbpath {1} --logpath {2}".format(
                            os.path.join(self.mongobin, 'mongod'), self.dir, slog.getpath()),
                              shell=True)
    def stop(self):
        subprocess.call([os.path.join(self.mongobin, 'mongod'), '--shutdown',
                         '--dbpath', self.dir])

class ShardServer:
    def __init__(self, param, host, script_home):
        self.param = param
        self.host = host
        self.port = shardsvr_port
        self.dir = es.DataRoot(param).dir('mongodb-' + es.getuser())
        self.mongobin = os.path.join(os.path.dirname(script_home),
                                     'mongodb', 'bin')
        self.mongos = param['mongos'].split(',')[0]
        self.mongos_port = mongos_port
    def install(self):
        es.cleandir(self.dir)
    def start(self, install = True):
        if (install):
            self.install()
        slog = es.SysLog('mongoshard')
        slog.rotate(10)
        subprocess.check_call("{0} --fork --shardsvr --dbpath {1} --logpath {2}".format(
                            os.path.join(self.mongobin, 'mongod'), self.dir, slog.getpath()),
                              shell=True)
    def addto(self, mongos, port):
        subprocess.check_call([
                        os.path.join(self.mongobin, 'mongo'),
                        '--host', mongos, '--port', '{0}'.format(port),
                        'admin',
                        '--eval', 'sh.addShard("{0}:{1}")'.format(self.host,self.port)
                              ])
    def stop(self):
        subprocess.call([os.path.join(self.mongobin, 'mongod'), '--shutdown',
                         '--dbpath', self.dir])

class Mongos:
    def __init__(self, param, host, script_home):
        self.param = param
        self.host = host
        self.mongobin = os.path.join(os.path.dirname(script_home),
                                     'mongodb', 'bin')
        self.configsvrs = param['configsvr'].split(',')
        self.port = mongos_port
    def start(self):
        slog = es.SysLog('mongos')
        slog.rotate(10)
        subprocess.check_call("{0} --fork --configdb {1} --logpath {2} --port {3}".format(
                        os.path.join(self.mongobin, 'mongos'),
                        self.configdb(), slog.getpath(), self.port),
                              shell=True)
    def configdb(self):
        return ','.join(['{0}:{1}'.format(h, configsvr_port) for h in self.configsvrs])

    def add_shards(self):
        port = shardsvr_port
        for s in self.param['shardsvr'].split(','):
            subprocess.check_call([
                                   os.path.join(self.mongobin, 'mongo'),
                                   '--port', '{0}'.format(self.port),
                                   'admin',
                                   '--eval', 'sh.addShard("{0}:{1}")'.format(s,port)
                                   ])

    def stop(self):
        subprocess.call('{0} admin --port {1} --eval "db.shutdownServer()"'.format(
                        os.path.join(self.mongobin, 'mongo'), self.port), shell=True)


data = json.load(sys.stdin)

if len(sys.argv) > 2:
    cmd = sys.argv[2]
elif len(sys.argv) > 1:
    cmd = sys.argv[1]
else:
    cmd = 'start-mongos'

sd = es.SysDirs()

(do,what) = cmd.split('-')
if (what == 'configsvr' or what == 'configdb'):
    server = ConfigServer(data, host = sys.argv[1], script_home = sd.script_home)
elif (what == 'shardsvr' or what == 'shard'):
    server = ShardServer(data, host = sys.argv[1], script_home = sd.script_home)
else:
    server = Mongos(data, host = sys.argv[1], script_home = sd.script_home)

if (do == 'stop'):
    server.stop()
elif (do == 'install'):
    server.install()
elif (do == 'addshards'):
    server.add_shards()
else:
    server.start()
