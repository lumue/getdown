Polymer('download-list-view', {
	
	ready : function() {		
		this.reload();
		var thisView=this;
		document.addEventListener("wsevent-downloads-job-state-change", function(e) {
			 	thisView.onDownloadStateChange(e.detail);
		});
	},

	reload : function(){
		var thisView=this;
		Getdown.downloadHttpClient.list(function(newValue){
			thisView.downloads=newValue;
		})
	},
	
	onDownloadStateChange : function(download){
		for(var i = 0, size = this.downloads.length; i < size ; i++){
			if(this.downloads[i].handle==download.handle){
				this.downloads[i]=download;
			}
		}
	}
		
	
});