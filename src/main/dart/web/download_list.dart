import 'dart:html';

void main() {
  querySelector("#sample_text_id")
      ..text = "Reverse"
      ..onClick.listen(reverseText);
}

void reverseText(MouseEvent event) {
  var text = querySelector("#sample_text_id").text;
  String url="../api/strings/revert/"+text;
      HttpRequest.getString(url).then((String result){
        querySelector("#sample_text_id").text = result.toString();
      });
 
  
}