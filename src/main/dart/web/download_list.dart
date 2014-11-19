import 'package:polymer/polymer.dart';
import 'model.dart' show DownloadViewItem,TestData,DownloadRequest;
import 'controller.dart';
import 'dart:html' show Event, Node;


@CustomTag('download-list')
class DownloadList extends PolymerElement {
 
   DownloadController downloadController=new DownloadController();
  
  @observable List<DownloadViewItem> downloads=new List<DownloadViewItem>();
  

  DownloadList.created() : super.created();
  
  addDownload(Event e, var detail, Node sender) {
    
    e.preventDefault();
    DownloadRequest downloadRequest=detail['downloadRequest'];
    
    downloadController.addDownload(downloadRequest).then((downloadViewItem){
      refreshDownloadList();
    });
    
  }
  
  refreshDownloadList(){
    downloadController.listDownloads().then((freshDownloadList){downloads=freshDownloadList;});
  }
  
}