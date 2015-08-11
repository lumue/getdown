FROM jeanblanchard/busybox-java:8
MAINTAINER lm "lm@combase.de"

ADD getdown-app-springboot.jar app.jar
RUN touch /app.jar
CMD mkdir /getdown
ENTRYPOINT ["java","-Dgetdown.path.home=/getdown","-jar","/app.jar"]