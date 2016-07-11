FROM lumue/jdkydl
MAINTAINER lm "mueller.lutz@gmail.com"

RUN ln -s `which youtube-dl` /usr/local/bin/youtube-dl


ADD *.jar /app.jar
CMD mkdir /getdown && mkdir /downloads

EXPOSE 8001

VOLUME ["/getdown"]
VOLUME ["/downloads"]

ENTRYPOINT ["java","-Dgetdown.path.home=/getdown", "-Dgetdown.path.download=/downloads","-jar","/app.jar"]