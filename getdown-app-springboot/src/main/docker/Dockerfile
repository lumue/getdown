FROM jeanblanchard/busybox-java:8
MAINTAINER lm "lm@combase.de"

ADD getdown-app-springboot.jar app.jar
RUN touch /app.jar
CMD mkdir /getdown && mkdir /downloads

EXPOSE 8001

VOLUME ["/getdown"]
VOLUME ["/downloads"]

ENTRYPOINT ["java","-Dgetdown.path.home=/getdown", "-Dgetdown.path.download=/downloads","-jar","/app.jar"]