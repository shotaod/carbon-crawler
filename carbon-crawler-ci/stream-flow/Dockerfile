FROM 8-jre-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
# To reduce Tomcat startup time
# we added a system property pointing to "/dev/urandom" as a source of entropy.
# This is not necessary with more recent versions of Spring Boot,
# if you use the "standard" version of Tomcat (or any other web server).
# ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
ENTRYPOINT ["java","-Dcarbon.profile=prd","-jar","/app.jar"]