var Getdown={
		
		downloadHttpClient:{
			
			list : function(caller,ondata){
				var xmlhttp = new XMLHttpRequest();
				var url = "../download/list";

				xmlhttp.onreadystatechange = function() {
				    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				       ondata(caller,JSON.parse(xmlhttp.responseText));
				    }
				}
				xmlhttp.open("GET", url, true);
				xmlhttp.send();
			},

			add : function(caller,url,ondata){
				var xmlhttp = new XMLHttpRequest();
				var url = "../download/add?url="+encodeURIComponent(url);

				xmlhttp.onreadystatechange = function() {
				    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				       ondata(caller,JSON.parse(xmlhttp.responseText));
				    }
				}
				xmlhttp.open("PUT", url, true);
				xmlhttp.send();
			}

		}
}



Polymer('getdown-webconsole-application', {
	
	ready : function() {	
		
		var socket = new SockJS('/ws');
		stompClient = Stomp.over(socket);
		
		stompClient.connect({}, function(frame) {
			
			console.log('Connected: ' + frame);
			
			stompClient.subscribe('/downloads/jobStateChange', function(message){
				console.log(message.body);
			});
			
		});
	},

	onAddDownloadButtonClicked : function(e){
		this.$.add_download_dialog.toggle();
	},
	
	

});