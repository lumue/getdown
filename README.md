# getdown

[![](https://images.microbadger.com/badges/image/lumue/getdown.svg)](https://microbadger.com/images/lumue/getdown "Get your own image badge on microbadger.com")
[![Build Status](https://travis-ci.org/lumue/getdown.svg?branch=master)](https://travis-ci.org/lumue/getdown)

## what is it ?

Getdown is a webapplication to simplify downloads from various filehosting sites.
It is basically a http frontend to run youtube-dl remotely.

Since [youtube-dl](https://rg3.github.io/youtube-dl) is used to perform the actual downloading, a large number of downloadsites is supported. you can find a list of supported filehosters [on the youtube-dl site](https://rg3.github.io/youtube-dl/supportedsites.html)

## how do i use it?

### installing and running with docker

easy, via docker:
  
```docker run -p 8001:8001 -v ~/mydownloads:/downloads  lumue/getdown```

### accessing the ui via browser

navigate your browser to ```http://<hostname>:8001/webconsole/index.html```


#### exposed ports

 * 8001/tcp - access to web api and web ui

#### exposed volumes

 * `/download` location for downloaded files
 * `/getdown` place *.properties files here for custom configuration

## links

 * [Docker](https://hub.docker.com/r/lumue/getdown/)

