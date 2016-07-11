FROM lumue/jdkydl
MAINTAINER lm "mueller.lutz@gmail.com"

ADD getdown-app-springboot.jar /app.jar
CMD mkdir /getdown && mkdir /downloads

EXPOSE 8001

VOLUME ["/getdown"]
VOLUME ["/downloads"]

ENTRYPOINT ["java","-Dgetdown.path.home=/getdown", "-Dgetdown.path.download=/downloads","-jar","/app.jar"]