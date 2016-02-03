import os
import sys
import subprocess
import shutil
import getpass # for getting usernameu
import socket # for getting hostname for syslog file name
import time
import json

class SysDirs:
    def __init__(self, home = None):
        self.script_home = os.path.dirname(os.path.abspath(sys.argv[0]))
        if (home == None):
            self.home = os.path.dirname(self.script_home)
        else:
            self.home = home
        self.config_home = os.path.join(self.home, 'conf')


class DataRoot:
    def __init__(self, data = {}):
        if ('data_root' in data):
            self.root = data['data_root']
        elif (os.path.exists('/data/tmp')):
            self.root = '/data/tmp'
        else:
            self.root = '/tmp'
    def dir(self, name):
        return os.path.join(self.root, name)

class Server:
    def __init__(self, cmd, syslog = '/dev/null', env = None):
        self.cmd = cmd;
        self.syslog = syslog
        self.env = env
        if (env != None):
            self.env = os.environ.copy()
            self.env.update(env)
    def start(self):
        p = subprocess.Popen("nohup {0} 0<&- &> {1} &".format(self.cmd, self.syslog),
                             shell=True, env = self.env)
        ret_code = p.wait()
        if (ret_code != 0):
            raise RuntimeError("server start exited with {0} at {1}: {2}".format(ret_code,
                                                            gethostname(), self.cmd))

class Client:
    def __init__(self, home = 'ExpTKVS', cmd = None, input = "", hosts = []):
        self.script_home = home
        self.input = input
        if isinstance(input, dict):
            self.input = json.dumps(input)
        self.cmd = cmd
        self.hosts = hosts
    def stop(self, hosts = None):
        if (hosts == None):
            hosts = self.hosts
        for h in hosts:
            self.call(h, self.cmd, [h, 'stop'], self.input)
    def restart(self, hosts = None):
        if (hosts == None):
            hosts = self.hosts
        for h in hosts:
            self.call(h, self.cmd, [h, 'restart'], self.input)
    def command(self, c, hosts = None):
        if (hosts == None):
            hosts = self.hosts
        for h in hosts:
            self.call(h, self.cmd, [h, c], self.input)
    def call(self, host, cmd = None, args = [], input = None, check = True):
        if (cmd == None):
            cmd = self.cmd
        if (input == None):
            input = self.input
        if (input == ""):
            ret_code = subprocess.call(self.tocmd(host, cmd, args))
        else:
            pipe = subprocess.Popen(self.tocmd(host, cmd, args),
                                    stdin= subprocess.PIPE)
            pipe.communicate(input=input)
            ret_code = pipe.wait()
        if (ret_code != 0):
            if (check):
                raise RuntimeError("command exited with {0} at {1}: {2}".format(ret_code, host, cmd))
            else:
                print "warn: command exited with {0} at {1}: {2}".format(ret_code, host, cmd)

    def tocmd(self, host, cmd, args = []):
        if host == 'localhost':
            local_home = os.path.join(os.path.expanduser('~'), self.script_home)
            return [os.path.join(local_home, cmd)] + args
        else:
            return ["ssh", host, os.path.join(self.script_home, cmd)] + args


class SysLog:
    def __init__(self, type, dir = None):
        self.type = type
        if (dir == None):
            self.dir = os.path.join(os.path.expanduser('~'), 'tmp')
        else:
            self.dir = dir
        self.basename = os.path.join(self.dir,
                                     type + '-' + gethostname())
    def rotate(self, num):
        while (num > 0):
            num0 = num - 1
            dst = self.basename + '-{0}.log'.format(num)
            if (num0 == 0):
                src = self.basename + '.log'
            else:
                src = self.basename + '-{0}.log'.format(num0)
            try:
                os.rename(src, dst)
            except (IOError, OSError):
                pass
            num = num0
    def getpath(self):
        return self.basename + '.log'

def cleandir(dir_path):
    if (os.path.exists(dir_path)):
        shutil.rmtree(dir_path)
    os.makedirs(dir_path)

def getuser():
    return getpass.getuser()
def gethostname():
    fqfn = socket.getfqdn()
    hn = socket.gethostname()
    if (len(fqfn) > len(hn)):
        return fqfn
    else:
        return hn


def isportlistened(port):
    if (os.access('/usr/sbin/lsof', os.X_OK)):
        cmd = '/usr/sbin/lsof'
    elif (os.access('/usr/bin/lsof', os.X_OK)):
        cmd = '/usr/bin/lsof'
    else:
        cmd = 'lsof'
    p = subprocess.Popen([cmd, '-i', ":{0}".format(port)],
                         stdout=subprocess.PIPE)
    output = p.communicate()[0]
    return 'LISTEN' in output

def waitforport(port):
    count = 0;
    while (isportlistened(port) and count < 100):
        time.sleep(1)
        count = count + 1
    if count >= 100:
        raise Exception("port is not available after waiting: {0}".format(port))

os.umask(0002)
