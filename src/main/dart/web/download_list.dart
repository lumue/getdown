import 'package:polymer/polymer.dart';
import 'model.dart' show Download;
/*
 * Class to represent a collection of Codelab objects.
 */
@CustomTag('download-list')
class DownloadList extends PolymerElement {
 
  @observable List<Download> downloads = toObservable([]);

  DownloadList.created() : super.created();

 
  
}
