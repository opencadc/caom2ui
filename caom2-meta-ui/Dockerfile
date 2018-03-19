FROM opencadc/tomcat:alpine

# The SOURCE (first argument) is relative to this directory.  The
# TARGET (second argument) is relative to the WORKDIR above.
# Absolute paths are not allowed from the SOURCE.
#
# For development purposes, pass the -v ${PATH_TO_LOCAL_BUILD_WAR_DIR}:/usr/local/tomcat/webapps to the run command
#
COPY *.war webapps/
COPY domains.properties /root/config/
