/**
 * getdown rest controller clients
 */
library getdown.web.controller;

import 'model.dart';
import 'dart:async';
import 'dart:html' show Event, Node,HttpRequest;
import 'dart:convert';


class DownloadController
{
  
  static final String ADD_DOWNLOAD_SERVICE_PATH = '../download/add';  
  static final String LIST_DOWNLOAD_SERVICE_PATH = '../download/list';
  
  Future<DownloadJob> addDownload(DownloadRequest downloadRequest){
      
      return HttpRequest.request(ADD_DOWNLOAD_SERVICE_PATH+"?url="+Uri.encodeQueryComponent(downloadRequest.url),method: 'PUT')
               .then((HttpRequest response) {
                   DownloadJob download=new DownloadJob.fromMap(JSON.decode(response.responseText));
                   return download;
    }
    );
  }
  
  Future<List<DownloadJob>> listDownloads(){
    
      return HttpRequest.getString(LIST_DOWNLOAD_SERVICE_PATH)
               .then((response) {
        
                   List jsonMaps=JSON.decode(response);
                   
                   List<DownloadJob> result=new List<DownloadJob>();
                   jsonMaps.forEach((jsonMap){
                     result.add(new DownloadJob.fromMap(jsonMap));
                   });
                   
                   return result;
    }
    );
  }
}
