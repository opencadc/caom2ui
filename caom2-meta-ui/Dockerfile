FROM images.opencadc.org/library/cadc-tomcat:1.3

# Web resources are in the caom2-search-server JAR file, so enable scanning of it.
RUN sed -i 's/tomcat.util.scan.StandardJarScanFilter.jarsToScan=\\/tomcat.util.scan.StandardJarScanFilter.jarsToScan=\\\ncaom2-ui-server*\.jar,\\/' /etc/tomcat/catalina.properties

COPY build/libs/caom2ui.war /usr/share/tomcat/webapps/
