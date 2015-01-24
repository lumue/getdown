library getdown.web.models;


import 'package:polymer/polymer.dart';


class DownloadJob extends Observable{
  
  @observable String handle;
  
  @observable String name;
  
  @observable String url;
  
  @observable int size;
  
  @observable int progress;
  
  /**
    * valid values are PENDING,RESOLVING,RUNNING, PAUSED, CANCELLED, ERROR, FINISHED
    */
  @observable String state;
   
   DownloadJob(String handle,String name,String url, int size, int progress,String state){
      this.handle=handle;
      this.name=name;
      this.url=url;
      this.size=size;
      this.progress=progress;
      this.state=state;
   }
   
   factory DownloadJob.fromMap(Map map) {
         return new DownloadJob(map['handle'],map['name'],map['url'],map['size'],map['progress'],map['state']);
       }

}




class DownloadRequest extends Observable {
  
  @observable String url;  
  
  DownloadRequest([this.url = ""]);
}


