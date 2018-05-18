FROM docker.today36524.com.cn:5000/base/tomcat:8-jdk8

COPY ./catalina.sh /usr/local/tomcat/bin/
RUN mkdir /gateway-conf && rm -rf /usr/local/tomcat/webapps && mkdir -p /usr/local/tomcat/webapps && chmod 744 /usr/local/tomcat/bin/catalina.sh

COPY  ./apps/ROOT.war /usr/local/tomc/webapps/

COPY  ./apps/service-whitelist.xml /gateway-conf/

CMD sh /opt/fluent-bit/fluent-bit.sh && catalina.sh run
