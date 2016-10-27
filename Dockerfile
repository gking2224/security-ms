# base
FROM me.gking2224/ms-base:v2

# details
MAINTAINER Graham King <gking2224@gmail.com>

# labels

# build args
ARG version
	
# environment variables
ENV SERVICE       securityms
ENV DEBUG_OPTS    -Xdebug \
                  -Xrunjdwp:server=y,transport=dt_socket,suspend=y,address=8081
ENV JAVA_OPTS     ${DEBUG:+${DEBUG_OPTS}}
ENV JAVA_PROPS    -Dlogging.config=$WORK_DIR/logback.xml
ENV JAR           $WORK_DIR/service.jar

# mount points

# create directories

# run as user
USER $user

# fetch code
COPY build/libs/${SERVICE}-${version}-boot.jar $WORK_DIR/service.jar
COPY logback.xml $WORK_DIR/logback.xml

# executable
WORKDIR ${WORK_DIR}
CMD PROPS_DIR=${PROPS_DIR} \
    java $JAVA_OPTS $JVM_ARGS $JAVA_PROPS \
         -jar $JAR \
         -a $APP \
         -e $ENV \
         $JAVA_ARGS



# cp /Users/gk/Documents/Projects/STS-workspace2/security-service/build/libs/securityms-0.0.1-SNAPSHOT-boot.jar  .
# docker run --name activemq -it --rm -P webcenter/activemq:latest
# docker build --build-arg service=securityms --build-arg version=0.0.1-SNAPSHOT -t me.gking2224/securityms:0.0.1-SNAPSHOT .
# docker run -e APP="web" -e ENV="local" -p 8080:80 -p 10003:8081 --name sec-local-web1 --link ms_dbserver:mysql --link activemq:activemq -v /Users/gk/dev/properties:/work/properties -v /Users/gk/dev/logs:/logs  me.gking2224/securityms:0.0.1-SNAPSHOT