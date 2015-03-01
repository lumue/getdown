var Getdown={
		
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
				var url = "../download/list";
				var params="url="+encodeURIComponent(url);

				xmlhttp.onreadystatechange = function() {
				    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				       ondata(JSON.parse(xmlhttp.responseText));
				    }
				}
				xmlhttp.open("GET", url, true);
				xmlhttp.send(params);
			}

		}
}



Polymer('getdown-webconsole-application', {
	
	ready : function() {	
	},

	onAddDownloadButtonClicked : function(e){
		this.$.add_download_dialog.toggle();
	},
	
	

});