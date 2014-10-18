library getdown.web.models;

import 'package:polymer/polymer.dart';

class Download extends Observable {
  
  @observable String name;
  @observable String url;
  @observable int size;
  @observable int progress;

  Download([this.name = "",
            this.url="",
            this.size=0,
            this.progress=0]);
}

class DownloadRequest extends Observable {
  
  @observable String url;  
  
  DownloadRequest([this.url = ""]);
}

class TestData{
  static final List<Download> TESTLIST=[new Download("file 1.zip","http://testhost/download/file1.zip",20000,5000),
                                                  new Download("file 2.zip","http://testhost/download/file1.zip",20000,3000),
                                                  new Download("file 3.zip","http://testhost/download/file1.zip",20000,8000),
                                                  new Download("file 4.zip","http://testhost/download/file1.zip",20000,10000),
                                                  new Download("file 5.zip","http://testhost/download/file1.zip",20000,9000),
                                                  new Download("file 6.zip","http://testhost/download/file1.zip",20000,5000),
                                                  new Download("file 7.zip","http://testhost/download/file1.zip",20000,1000)];
}
