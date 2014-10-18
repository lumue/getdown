import 'package:polymer/polymer.dart';
import 'model.dart' show Download;
/*
 * Class to represent a collection of Codelab objects.
 */
@CustomTag('download-list')
class DownloadList extends PolymerElement {
 
  @observable List<Download> downloads = toObservable([]);

  DownloadList.created() : super.created()
  {
    downloads.add(new Download("file 1.zip",20000,5000));
    downloads.add(new Download("file 2.zip",20000,3000));
    downloads.add(new Download("file 3.zip",20000,8000));
    downloads.add(new Download("file 4.zip",20000,10000));
    downloads.add(new Download("file 5.zip",20000,9000));
    downloads.add(new Download("file 6.zip",20000,5000));
    downloads.add(new Download("file 7.zip",20000,1000));
  }

 
  
}
