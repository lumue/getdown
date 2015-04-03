FROM jeanblanchard/busybox-java:8
MAINTAINER lm "lm@combase.de"

ADD getdown-app-springboot.jar app.jar
RUN touch /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]