import 'package:polymer/polymer.dart';
import 'model.dart' show Download,TestData,DownloadRequest;
import 'dart:html' show Event, Node;
import 'dart:convert';
import 'package:http/http.dart' as http;
/*
 * Class to represent a collection of Codelab objects.
 */
@CustomTag('download-list')
class DownloadList extends PolymerElement {
 
  static final String ADD_DOWNLOAD_SERVICE_PATH = '../download/add';
  
  
  @observable List<Download> downloads = toObservable([]);
  
  addDownload(Event e, var detail, Node sender) {
    e.preventDefault();
    DownloadRequest downloadRequest=detail['downloadRequest'];
    
    
    
    http.get(Uri.encodeQueryComponent(ADD_DOWNLOAD_SERVICE_PATH+"?url="+downloadRequest.url))
          .then((response) {
              Download download=JSON.decode(response.body);
              downloads.add(download);
    });
    
    
    
  }
  
  DownloadList.created() : super.created();

}
