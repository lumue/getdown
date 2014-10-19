library getdown.web.models;

import 'package:polymer/polymer.dart';

class DownloadViewItem extends Observable {
  
  String handle;
  @observable String name;
  @observable String url;
  @observable int size;
  @observable int progress;
  /**
   * valid values are PENDING, RUNNING, PAUSED, CANCELLED, ERROR, FINISHED
   */
  @observable String state;

  DownloadViewItem([
            this.handle = "",
            this.name = "",
            this.url="",
            this.size=0,
            this.progress=0,
            this.state="PENDING"]);
}

class DownloadRequest extends Observable {
  
  @observable String url;  
  
  DownloadRequest([this.url = ""]);
}

class TestData{
  static final List<DownloadViewItem> TESTLIST=[new DownloadViewItem("X","file 1.zip","http://testhost/download/file1.zip",20000,5000),
                                                  new DownloadViewItem("X","file 2.zip","http://testhost/download/file1.zip",20000,3000),
                                                  new DownloadViewItem("X","file 3.zip","http://testhost/download/file1.zip",20000,8000),
                                                  new DownloadViewItem("X","file 4.zip","http://testhost/download/file1.zip",20000,10000),
                                                  new DownloadViewItem("X","file 5.zip","http://testhost/download/file1.zip",20000,9000),
                                                  new DownloadViewItem("X","file 6.zip","http://testhost/download/file1.zip",20000,5000),
                                                  new DownloadViewItem("X","file 7.zip","http://testhost/download/file1.zip",20000,1000)];
}
