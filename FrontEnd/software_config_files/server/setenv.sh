export CATALINA_OPTS="$CATALINA_OPTS -DDB_DRIVER=$DB_DRIVER -DDB_URL_APPZILLONCB=$DB_URL_APPZILLONCB  -DDB_URL_GRAMEENKOOTA=$DB_URL_GRAMEENKOOTA -DDB_USERNAME=$DB_USERNAME -DDB_PWD=$DB_PWD"
export CATALINA_OPTS="$CATALINA_OPTS -Xms750m"
export CATALINA_OPTS="$CATALINA_OPTS -Xmx2048m"
#export CATALINA_OPTS="$CATALINA_OPTS -XX:MaxPermSize=1024m"
# JAVA_OPTS='-Djavax.net.ssl.trustStore=postgresql -Djavax.net.ssl.trustStorePassword=changeit -Djavax.net.debug=ssl'


export CATALINA_OPTS="$CATALINA_OPTS -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=8686 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
export CATALINA_OPTS="$CATALINA_OPTS -javaagent:/opt/prometheus/jmx_prometheus_javaagent-0.20.0.jar=8871:/opt/prometheus/config.yaml"
export CATALINA_OPTS="$CATALINA_OPTS -Djavax.servlet.request.encoding=UTF-8 -Dfile.encoding=UTF-8"
