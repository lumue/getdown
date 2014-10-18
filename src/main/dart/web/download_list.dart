import 'package:polymer/polymer.dart';
import 'model.dart' show Download,TestData,DownloadRequest;
import 'dart:html' show Event, Node;
/*
 * Class to represent a collection of Codelab objects.
 */
@CustomTag('download-list')
class DownloadList extends PolymerElement {
 
  @observable List<Download> downloads = toObservable(TestData.TESTLIST);
  
  addDownload(Event e, var detail, Node sender) {
    e.preventDefault();
    DownloadRequest downloadRequest=detail['downloadRequest'];
    downloads.add(new Download(downloadRequest.url,downloadRequest.url,10000,399));
  }
  
  DownloadList.created() : super.created();

}
