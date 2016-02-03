#!/usr/bin/python

import os
import sys

import expsystem as es

class RegionServers:
    def __init__(self, regionservers):
        self.servers = regionservers
    def save(self, config):
        with open(os.path.join(config, 'regionservers'), 'w') as outfile:
            for h in self.servers:
                outfile.write(h + "\n")

class HBaseConfig:
    def __init__(self, data):
        self.rootdir = "/hbase-test-" + es.getuser()
        self.param = data.get('param', {})
        self.master = data['master']
        self.namenode = data.get('namenode', self.master)
        self.regionservers = data['regionservers'].split(',')
        # 3 servers including master node
        self.zookeepers = [self.master] + [x for x in self.regionservers if x != self.master][:2]
    def save(self, dir):
        conf = os.path.join(dir, 'config')
        es.cleandir(conf)
        # save config/regionservers
        RegionServers(self.regionservers).save(conf)
        self.save_hbase_site(conf, dir)
    def save_hbase_site(self, conf, dir = None):
        with open(os.path.join(conf, 'hbase-site.xml'), 'w') as outfile:
            outfile.write("<configuration>\n")
            self.writeproperty(outfile, 'hbase.cluster.distributed', 'true')
            self.writeproperty(outfile, 'hbase.master', "{0}:60000".format(self.master))
            self.writeproperty(outfile, 'hbase.rootdir',
                               "hdfs://{0}:9000".format(self.namenode) + self.rootdir)
            self.writeproperty(outfile, 'hbase.zookeeper.property.clientPort', 2181)
            self.writeproperty(outfile, 'hbase.zookeeper.quorum', ",".join(self.zookeepers))
            
            if (dir is not None):
                self.writeproperty(outfile, 'hbase.zookeeper.property.dataDir',
                                        os.path.join(dir, 'zookeeper'))
                self.writeproperty(outfile, 'hbase.tmp.dir', dir)
            for (k, v) in self.param.items():
                self.writeproperty(outfile, k, v)
            outfile.write("</configuration>\n")

    def writeproperty(self, out, name, value):
        out.write(" <property>\n")
        out.write("  <name>{0}</name>\n".format(name))
        out.write("  <value>{0}</value>\n".format(value))
        out.write(" </property>\n")
    def isMaster(self, host):
        return self.master == host
