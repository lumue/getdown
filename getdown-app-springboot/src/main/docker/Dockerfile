FROM lumue/debjdk
MAINTAINER lm "mueller.lutz@gmail.com"

RUN apt-get -y update && \
apt-get -y install ffmpeg && \
apt-get -y install rtmpdump && \
apt-get -y install aria2 && \
apt-get -y install youtube-dl && \
ln -s `which youtube-dl` /usr/local/bin/youtube-dl && \
rm -rf /var/cache/*

ADD getdown-app-springboot-master-SNAPSHOT.jar /app.jar
CMD mkdir /getdown && mkdir /downloads

EXPOSE 8001

VOLUME ["/getdown"]
VOLUME ["/downloads"]

ENTRYPOINT ["java","-Dgetdown.path.home=/getdown","-Dspring.redis.host=redis", "-Dgetdown.path.download=/downloads","-jar","/app.jar"]