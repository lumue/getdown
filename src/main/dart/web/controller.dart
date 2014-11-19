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
  
  Future<DownloadViewItem> addDownload(DownloadRequest downloadRequest){
      
      return HttpRequest.getString(ADD_DOWNLOAD_SERVICE_PATH+"?url="+Uri.encodeQueryComponent(downloadRequest.url))
               .then((response) {
                   DownloadViewItem download=new JsonDownloadViewItem.fromJsonString(response);
                   return download;
    }
    );
  }
  
  Future<List<DownloadViewItem>> listDownloads(){
    
      return HttpRequest.getString(LIST_DOWNLOAD_SERVICE_PATH)
               .then((response) {
        
                   List jsonStrings=JSON.decode(response);
                   
                   List<DownloadViewItem> result=new List<DownloadViewItem>(jsonStrings.length);
                   jsonStrings.forEach((jsonString){
                     result.add(new JsonDownloadViewItem.fromJsonString(response));
                   });
                   
                   return result;
    }
    );
  }
}
