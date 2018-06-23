FROM docker.today36524.com.cn:5000/base/openjdk:server-jre8

RUN mkdir /gateway-conf &&  mkdir -p /opt/gateway

COPY  ./apps/dapeng-api-gateway.jar /opt/gateway/
COPY  ./startup.sh /opt/gateway/
COPY  ./apps/service-whitelist.xml /gateway-conf/

ENTRYPOINT EXEC  /opt/gateway/startup.sh

