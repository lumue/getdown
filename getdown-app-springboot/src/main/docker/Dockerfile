FROM lumue/jdkydl:alpine
MAINTAINER lm "mueller.lutz@gmail.com"

RUN ln -s `which youtube-dl` /usr/local/bin/youtube-dl


ADD *.jar /app.jar
CMD mkdir /getdown && mkdir /downloads

EXPOSE 8001

VOLUME ["/getdown"]
VOLUME ["/downloads"]

ENTRYPOINT ["java","-Xms20M","-Xmx100m","-Dgetdown.path.home=/getdown", "-Dgetdown.path.media=/downloads","-jar","/app.jar"]