#!/bin/sh
echo "begin"
export JVM_HOME='opt/oracle-server-jre'
export PATH=$JVM_HOME/bin:$PATH

PRGNAME=api-gate-way
ADATE=`date +%Y%m%d%H%M%S`
PRGDIR=`pwd`
dirname $0|grep "^/" >/dev/null
if [ $? -eq 0 ];then
   PRGDIR=`dirname $0`
else
    dirname $0|grep "^\." >/dev/null
    retval=$?
    if [ $retval -eq 0 ];then
        PRGDIR=`dirname $0|sed "s#^.#$PRGDIR#"`
    else
        PRGDIR=`dirname $0|sed "s#^#$PRGDIR/#"`
    fi
fi

LOGDIR=$PRGDIR/logs
if [ ! -d "$LOGDIR" ]; then
        mkdir "$LOGDIR"
fi


#DEBUG="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9997"
JMX="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=1091 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
JVM_OPTS="-Dfile.encoding=UTF-8 -Dsun.jun.encoding=UTF-8 -Dname=$PRGNAME -Dio.netty.leakDetectionLevel=advanced -Xms512M -Xmx1024M -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDateStamps -Xloggc:$LOGDIR/gc-$PRGNAME-$ADATE.log -XX:+PrintGCDetails -XX:NewRatio=1 -XX:SurvivorRatio=30 -XX:+UseParallelGC -XX:+UseParallelOldGC -Dlog.dir=$PRGDIR/.."
SOA_BASE="-Dsoa.base=$PRGDIR/../ -Dsoa.run.mode=native"

# SIGTERM  graceful-shutdown
pid=0
process_exit() {
 if [ $pid -ne 0 ]; then
  echo "graceful shutdown pid: $pid" > $LOGDIR/pid.txt
  kill -SIGTERM "$pid"
  wait "$pid"
 fi
 exit 143; # 128 + 15 -- SIGTERM
}


trap 'kill ${!};process_exit' SIGTERM

nohup java -server $JVM_OPTS $SOA_BASE $DEBUG_OPTS $USER_OPTS  $E_JAVA_OPTS -jar $PRGDIR/dapeng-api-gateway.jar >> $LOGDIR/console.log 2>&1 &
#nohup java  -jar dapeng-api-gateway.jar >> $LOGDIR/console.log 2>&1 &
#java  -jar $PRGDIR/dapeng-api-gateway.jar
pid="$!"
echo "start pid: $pid" > $LOGDIR/pid.txt

nohup sh /opt/fluent-bit/fluent-bit.sh >> $LOGDIR/fluent-bit.log 2>&1 &

wait $pid
