#!/usr/bin/python
import subprocess
import os
import sys
import json
import time
import copy
import clients as cs


c = cs.ExpSys(json.load(sys.stdin))

c.prn()

for cmd in sys.argv[1:]:
    if cmd == 'restart':
        print "config: ", json.dumps(c.data)
        c.restart()
    elif cmd == 'stop':
        c.stop()
    elif cmd == 'wait':
        time.sleep(1)
    elif cmd == 'wait5':
        time.sleep(5)
    elif cmd.startswith('wait='):
        sec = cmd[len('wait='):]
        time.sleep(float(sec))
    elif cmd.startswith('save='):
        file = cmd[len('save='):]
        if len(file) > 0:
            c.save(file)
    else:
        c.command(cmd)
