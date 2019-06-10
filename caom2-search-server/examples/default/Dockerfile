# Base Tomcat 8 JRE 8 image from CANFAR running on Alpine.
FROM opencadc/tomcat:8.5-alpine

# Clear out the webapps directory.
RUN rm -rf webapps \
  && mkdir webapps

# The SOURCE (first argument) is relative to this directory.  The
# TARGET (second argument) is relative to the WORKDIR above.
# Absolute paths are not allowed from the SOURCE.
#
# For development purposes, pass the -v ${PATH_TO_LOCAL_BUILD_WAR_DIR}:/usr/local/tomcat/webapps to the run command
#
COPY build/libs/*.war webapps/

VOLUME /root/config

COPY org.opencadc.search.properties /root/config/
