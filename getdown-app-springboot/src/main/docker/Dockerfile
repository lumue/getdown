FROM lumue/jdkydl:alpine
MAINTAINER lm "mueller.lutz@gmail.com"

RUN ln -s `which youtube-dl` /usr/local/bin/youtube-dl
RUN ln -s `which ffmpeg` /usr/local/bin/ffmpeg
RUN ln -s `which ffprobe` /usr/local/bin/ffprobe

ADD *.jar /app.jar
CMD mkdir /getdown && mkdir /downloads

EXPOSE 8001

VOLUME ["/getdown"]
VOLUME ["/downloads"]

ENTRYPOINT ["java","-Xms20M","-Xmx100M","-Dgetdown.path.home=/getdown","-Dgetdown.path.media=/downloads", "-Xdebug", "-Xrunjdwp:transport=dt_socket,address=7002,server=y,suspend=n","-Dcom.sun.management.jmxremote.port=7003","-Djava.rmi.server.hostname=0.0.0.0","-Dcom.sun.management.jmxremote.local.only=false","-Dcom.sun.management.jmxremote.rmi.port=7003","-Dcom.sun.management.jmxremote.authenticate=false","-Dcom.sun.management.jmxremote.ssl=false" ,"-jar","/app.jar"]