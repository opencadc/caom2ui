# Base Tomcat 8 JRE 8 image from CANFAR running on Alpine.
FROM tomcat:8.5-alpine

ENV CATALINA_OPTS "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5555 -Djava.security.egd=file:/dev/./urandom -Djsse.enableSNIExtension=false"

# Clear out the webapps directory.
RUN rm -rf webapps \
  && mkdir webapps

# The SOURCE (first argument) is relative to this directory.  The
# TARGET (second argument) is relative to the WORKDIR above.
# Absolute paths are not allowed from the SOURCE.
#
# For development purposes, pass the -v ${PATH_TO_LOCAL_BUILD_WAR_DIR}:/usr/local/tomcat/webapps to the run command
#
COPY *.war webapps/

VOLUME /root/config

COPY org.opencadc.caom2ui.properties /root/config/
