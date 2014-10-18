library getdown.web.models;

import 'package:polymer/polymer.dart';

class Download extends Observable {
  
  @observable String name;
  @observable int size;
  @observable int progress;

  Download([this.name = "",
            this.size=0,
            this.progress=0]);
}

class DownloadRequest extends Observable {
  
  @observable String url;  
  
  DownloadRequest([this.url = ""]);
}