library getdown.web.models;

import 'package:polymer/polymer.dart';

class Download extends Observable {
  
  @observable String name;
  @observable long size;
  @observable long progress;

  // Constructor.
  Download([this.name = "",
            this.size=0,
            this.progress=0]);
}