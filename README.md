# getdown

[![](https://badge.imagelayers.io/lumue/getdown:latest.svg)](https://imagelayers.io/?images=lumue/getdown-app-springboot:latest 'Get your own badge on imagelayers.io')
[![Build Status](https://travis-ci.org/lumue/getdown.svg?branch=master)](https://travis-ci.org/lumue/getdown)

## what is it ?

Getdown is a webapplication to simplify downloads from various filehosting sites.

Since [youtube-dl]() is used to perform the actual downloading, a large number of downloadsites is supported. you can find a list of supported filehosters [on the youtube-dl site](https://rg3.github.io/youtube-dl/supportedsites.html)

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

