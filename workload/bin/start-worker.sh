#!/bin/bash
script_home=`dirname "$0"`
worker=$script_home/worker

LOG_FILE="/dev/null"
#
# command options
#
while getopts ":p:l:m:c:" opt; do
  case $opt in
  p)
  	PORT_NUMBER=$OPTARG
  	;;
  l)
    LOG_FILE=$OPTARG
    ;;
  m)
    JMX_PORT=$OPTARG
    ;;
  c)
    CPATH_BASE=$OPTARG
    ;;
  \?)
  	echo "Invalid option: -$OPTARG" >&2
  	echo "Usage: `basename $0` [-p PORT_NUMBER] [-l LOG_FILE] [-m JMX_PORT] [-c CLASSPATH_BASE]" >&2
  	exit 1
  	;;
  :)
    echo "Option -$OPTARG requires an argument." >&2
    exit 1
    ;;
  esac
done
# remove options from $@
shift $((OPTIND-1))

if [ -z "$WKLD_OPTS" ]; then
    WKLD_OPTS="-Xmx1G"
fi
WKLD_OPTS="-server $WKLD_OPTS"

if [ "$PORT_NUMBER" != "" ]; then
	WKLD_OPTS="$WKLD_OPTS -Dworker.port=$PORT_NUMBER"
fi
if [ "$JMX_PORT" != "" ]; then
    WKLD_OPTS="$WKLD_OPTS -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=$JMX_PORT"
fi
if [ "$CPATH_BASE" != "" ]; then
    WKLD_OPTS="$WKLD_OPTS -Dstrudel.classpath.base=$CPATH_BASE"
fi

export WKLD_OPTS
echo "Starting worker"
nohup $worker 0<&- &> $LOG_FILE &
