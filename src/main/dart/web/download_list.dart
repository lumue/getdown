import 'package:polymer/polymer.dart';
import 'model.dart' show DownloadViewItem,TestData,DownloadRequest;
import 'dart:html' show Event, Node,HttpRequest;
import 'dart:convert';
/*
 * Class to represent a collection of Codelab objects.
 */
@CustomTag('download-list')
class DownloadList extends PolymerElement {
 
  static final String ADD_DOWNLOAD_SERVICE_PATH = '../download/add';
  
  
  @observable List<DownloadViewItem> downloads = toObservable(TestData.TESTLIST);
  
  addDownload(Event e, var detail, Node sender) {
    e.preventDefault();
    DownloadRequest downloadRequest=detail['downloadRequest'];
    
    
    
    HttpRequest.getString(ADD_DOWNLOAD_SERVICE_PATH+"?url="+Uri.encodeQueryComponent(downloadRequest.url))
          .then((response) {
              var decodedResponse=JSON.decode(response);
              
              DownloadViewItem download=new DownloadViewItem(
                  decodedResponse["handle"],
                  decodedResponse["name"],
                  decodedResponse["url"],
                  decodedResponse["size"],
                  decodedResponse["progress"],
                  decodedResponse["state"]);
              
              downloads.add(download);
    });
    
    
    
  }
  
  DownloadList.created() : super.created();

}
