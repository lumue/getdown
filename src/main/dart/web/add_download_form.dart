import 'package:polymer/polymer.dart';
import 'model.dart' show DownloadRequest;
import 'dart:html' show CustomEvent, Event, Node;


@CustomTag('add-download-form')
class AddDownloadFormElement extends PolymerElement {
  
  @observable DownloadRequest downloadRequest;
  
  AddDownloadFormElement.created() : super.created() {
    downloadRequest=new DownloadRequest();
  }
  
  /*
     * Dispatches a custom event if a codelab passes validation. Otherwise, sets
     * the form error message. It is up to the form's parent element to listen
     * for the dispatch and handle the validated codelab object.
     */
  sendDownloadRequest(Event event, Object detail, Node sender) {
      event.preventDefault();
       
        dispatchEvent(new CustomEvent('download_requested',
            detail: {'downloadRequest': downloadRequest}));
      
    }
  
}
