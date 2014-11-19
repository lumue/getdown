library getdown.web.models;

import 'package:json_object/json_object.dart';
import 'package:polymer/polymer.dart';

abstract class DownloadViewItem extends JsonObject {
  
  String handle;
  
  String name;
  
  String url;
  
  int size;
  
  int progress;
  
  /**
    * valid values are PENDING, RUNNING, PAUSED, CANCELLED, ERROR, FINISHED
    */
   String state;
}


class JsonDownloadViewItem  extends JsonObject implements DownloadViewItem
{
  
  JsonDownloadViewItem();
  
  factory JsonDownloadViewItem.fromJsonString(string) {
      return new JsonObject.fromJsonString(string, new JsonDownloadViewItem());
    }

 
}


class DownloadRequest extends Observable {
  
  @observable String url;  
  
  DownloadRequest([this.url = ""]);
}


