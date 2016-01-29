#!/bin/bash

#
# [r] ... a trick to avoid the grep process itself is caught by grep.
#
pids=`ps xwww | grep workload.server.rest.WorkerServe[r] | awk '{print $1}'`

if [ "$pids" != "" ]
then
	echo "Stopping worker ($pids at `hostname`)..."
	kill $pids
	exit 0
fi
echo "worker not running."
exit 1
