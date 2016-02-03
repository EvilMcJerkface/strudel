import os
import sys
import subprocess
import time
import json
import copy
import expsystem as es

class HBaseClient:
    def __init__(self, script_home, conf):
        self.script_home = script_home
        self.conf = conf
        self.master = es.Client(script_home, 'bin/hbase.py',
                                hosts = [conf['master']],
                                input = conf)
        slavehosts = [h for h in conf['regionservers'].split(',') if h != conf['master']]
        self.slave = es.Client(script_home, 'bin/hbase.py',
                                                       hosts = slavehosts,
                                                       input = conf)
    def stop(self):
        # stop master -> regionservers will stop
        self.master.stop()
    def restart(self):
        self.master.stop()
        time.sleep(1)
        print "installing master"
        self.master.command('install')
        print "installing regison servers"
        self.slave.command('install')
        time.sleep(1)
        print "starting master"
        # start master -> regionservers will start
        self.master.command('start')
    def command(self, cmd):
        if (cmd == 'install'):
            self.master.command('install')
            self.slave.command('install')
        elif (cmd == 'start'):
            self.master.command('start')
    def prn(self):
        print "hbase.master=" + self.conf['master']
        print "hbase.regionservers=" + self.conf['regionservers']

class OmidClient:
    def __init__(self, script_home, conf, hbconf):
        self.script_home = script_home
        self.conf = conf
        self.conf['hbase'] = hbconf
        self.server = es.Client(script_home, 'bin/omid.py',
                                hosts = self.conf['hosts'].split(','),
                                input = self.conf)
    def stop(self):
        self.server.stop()
    def restart(self):
        self.server.stop()
        time.sleep(1)
        self.server.command('install')
        time.sleep(1)
        self.server.command('start')
    def prn(self):
        print "omid.hosts=" + self.conf['hosts']

class MongodbClient:
    def __init__(self, script_home, conf):
        self.script_home = script_home
        self.conf = conf
        self.configsvr = es.Client(script_home, 'bin/mongodb.py',
                                   hosts = self.conf['configsvr'].split(','),
                                   input = self.conf)
        self.shardsvr = es.Client(script_home, 'bin/mongodb.py',
                                hosts = self.conf['shardsvr'].split(','),
                                input = self.conf)
        self.mongos = es.Client(script_home, 'bin/mongodb.py',
                                hosts = self.conf['mongos'].split(','),
                                input = self.conf)
    def stop(self):
        self.mongos.command('stop-mongos')
        self.shardsvr.command('stop-shardsvr')
        self.configsvr.command('stop-configsvr')
    def restart(self):
        self.stop()
        time.sleep(1)
        self.configsvr.command('start-configsvr')
        self.shardsvr.command('start-shardsvr')
        self.mongos.command('start-mongos')
        es.Client(self.script_home, 'bin/mongodb.py',
                  hosts = self.conf['mongos'].split(',')[:1],
                  input = self.conf).command('addshards-mongos')
    def prn(self):
        print "mongo.configsvr=" + self.conf['configsvr']
        print "mongo.shardsvr=" + self.conf['shardsvr']
        print "mongo.mongos=" + self.conf['mongos']

class TokumxClient:
    def __init__(self, script_home, conf):
        self.script_home = script_home
        self.conf = conf
        self.server = es.Client(script_home, 'bin/tokumx.py',
                                hosts= self.conf['hosts'].split(','),
                                input = self.conf)
    def stop(self):
        self.server.stop()
    def restart(self):
        self.server.stop()
        self.server.command('start')
    def prn(self):
        print "tokumx=" + self.conf['hosts']

class WorkerClient:
    def __init__(self, script_home, conf):
        self.script_home = script_home
        self.conf = conf
        self.hosts = conf['hosts'].split(',')
        self.server = es.Client(script_home, 'bin/worker.py',
                                hosts= self.hosts,
                                input = self.conf)
    def stop(self):
        self.server.stop()
    def restart(self):
        self.server.restart()
    def prn(self):
        print "workers =", ", ".join(self.hosts)


def hbase_client(data, servers, script_home):
    if ('hbase' in data):
        servers.append(HBaseClient(script_home, data['hbase']))
def omid_client(data, servers, script_home):
    if ('omid' in data):
        servers.append(OmidClient(script_home, data['omid'], data['hbase']))

def mongo_client(data, servers, script_home):
    if ('mongodb' in data):
        servers.append(MongodbClient(script_home, data['mongodb']))
def tokumx_client(data, servers, script_home):
    if ('tokumx' in data):
        servers.append(TokumxClient(script_home, data['tokumx']))
clients = [
           lambda data, servers, script_home: hbase_client(data, servers, script_home),
           lambda data, servers, script_home: omid_client(data, servers, script_home),
           lambda data, servers, script_home: mongo_client(data, servers, script_home),
           lambda data, servers, script_home: tokumx_client(data, servers, script_home)
           ]

def register_client(c):
    clients.append(c)

class ExpSys:
    def __init__(self, data):
        self.data = data
        self.script_home = data.get('home_dir', "ExpTKVS")
        
        self.servers = []
        for c in clients:
            c(data, self.servers, self.script_home)
        
        if ('worker' in data):
            self.servers.append(WorkerClient(self.script_home, data['worker']))
        elif ('workers' in data):
            workerparam = {}
            workerparam['hosts'] = data['workers']
            self.servers.append(WorkerClient(self.script_home, workerparam))
    def restart(self):
        for s in reversed(self.servers):
            s.stop()
        time.sleep(1)
        for s in self.servers:
            s.restart()

    def stop(self):
        for s in reversed(self.servers):
            s.stop()
    def command(self, cmd):
        for s in self.servers:
            s.command(cmd)

    def prn(self):
        print "home_dir =", self.script_home
        for s in self.servers:
            s.prn()
                
    def save(self, file):
        with open(file, "w") as outfile:
            json.dump(self.data, outfile)
