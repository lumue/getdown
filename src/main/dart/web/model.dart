library getdown.web.models;

import 'package:polymer/polymer.dart';

class Download extends Observable {
  
  @observable String name;
  @observable int size;
  @observable int progress;

  // Constructor.
  Download([this.name = "",
            this.size=0,
            this.progress=0]);
}