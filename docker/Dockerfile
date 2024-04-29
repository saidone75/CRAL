ARG ACS_IMAGE
ARG ACS_VERSION
FROM ${ACS_IMAGE}:${ACS_VERSION}
ARG TOMCAT_DIR=/usr/local/tomcat
USER root
COPY alfresco-global.properties $TOMCAT_DIR/shared/classes/alfresco-global.properties