library getdown.web.models;

import 'package:polymer/polymer.dart';

class DownloadViewItem extends Observable {
  
  String handle;
  @observable String name;
  @observable String url;
  @observable int size;
  @observable int progress;

  DownloadViewItem([
            this.handle = "",
            this.name = "",
            this.url="",
            this.size=0,
            this.progress=0]);
}

class DownloadRequest extends Observable {
  
  @observable String url;  
  
  DownloadRequest([this.url = ""]);
}

class TestData{
  static final List<DownloadViewItem> TESTLIST=[new DownloadViewItem("file 1.zip","http://testhost/download/file1.zip",20000,5000),
                                                  new DownloadViewItem("file 2.zip","http://testhost/download/file1.zip",20000,3000),
                                                  new DownloadViewItem("file 3.zip","http://testhost/download/file1.zip",20000,8000),
                                                  new DownloadViewItem("file 4.zip","http://testhost/download/file1.zip",20000,10000),
                                                  new DownloadViewItem("file 5.zip","http://testhost/download/file1.zip",20000,9000),
                                                  new DownloadViewItem("file 6.zip","http://testhost/download/file1.zip",20000,5000),
                                                  new DownloadViewItem("file 7.zip","http://testhost/download/file1.zip",20000,1000)];
}
