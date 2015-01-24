import 'package:polymer/polymer.dart';
import 'model.dart' show DownloadJob,TestData,DownloadRequest;
import 'controller.dart';

import 'dart:html' show Event, Node;
import 'dart:async';

@CustomTag('download-list')
class DownloadList extends PolymerElement {
 
  DownloadController downloadController=new DownloadController();
  
  @observable List<DownloadJob> downloads=new ObservableList.from(new List<DownloadJob>());
  
  Timer refreshListTimer;
  
  DownloadList.created() : super.created(){
    this.refreshListTimer=new Timer.periodic(new Duration(seconds: 1),(timer) => this.onRefreshDownloadList());
  }
  
  
  addDownload(Event e, var detail, Node sender) {
    
    e.preventDefault();
    DownloadRequest downloadRequest=detail['downloadRequest'];
    
    downloadController.addDownload(downloadRequest).then((downloadViewItem){
      downloads.add(downloadViewItem);
    });
    
  }
  
  refreshDownloadList(){
    downloadController.listDownloads().then((freshDownloadList){
      downloads.clear();
      downloads.addAll(freshDownloadList);
      });
  }
  
  
   onRefreshDownloadList() {
     refreshDownloadList();
  }
}