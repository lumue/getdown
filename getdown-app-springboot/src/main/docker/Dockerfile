FROM lumue/debjdk
MAINTAINER lm "mueller.lutz@gmail.com"

RUN apt-get -y update && \
apt-get -y install ffmpeg && \
apt-get -y install youtube-dl && \
ln -s `which youtube-dl` /usr/local/bin/youtube-dl && \
rm -rf /var/cache/*

ADD getdown-app-springboot-stable-SNAPSHOT.jar app.jar
RUN touch /app.jar
CMD mkdir /getdown && mkdir /downloads

EXPOSE 8001

VOLUME ["/getdown"]
VOLUME ["/downloads"]

ENTRYPOINT ["java","-Dgetdown.path.home=/getdown", "-Dgetdown.path.download=/downloads","-jar","/app.jar"]