var Getdown={
		
		downloadWsClient:{
			
			connect: function(){
				
				var socket = new SockJS('/ws');
				var stompClient = Stomp.over(socket);
				
				stompClient.connect({}, function(frame) {
					
					stompClient.subscribe('/downloads/jobStateChange', function(message){
						var parsedMessage=JSON.parse(message.body);
						var event = new CustomEvent("wsevent-downloads-job-state-change", { "detail": parsedMessage });
						document.dispatchEvent(event);
					});
					
				});
			}
		},
		
		downloadHttpClient:{
			
			list : function(ondata){
				var xmlhttp = new XMLHttpRequest();
				var url = "../download/list";

				xmlhttp.onreadystatechange = function() {
				    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				       ondata(JSON.parse(xmlhttp.responseText));
				    }
				}
				xmlhttp.open("GET", url, true);
				xmlhttp.send();
			},

			add : function(url,ondata){
				var xmlhttp = new XMLHttpRequest();
				var url = "../download/add?url="+encodeURIComponent(url);

				xmlhttp.onreadystatechange = function() {
				    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				       ondata(JSON.parse(xmlhttp.responseText));
				    }
				}
				xmlhttp.open("PUT", url, true);
				xmlhttp.send();
			},
			
			

		}
}



Polymer('getdown-webconsole-application', {
	
	ready : function() {	
		Getdown.downloadWsClient.connect();
	}
	
	

});