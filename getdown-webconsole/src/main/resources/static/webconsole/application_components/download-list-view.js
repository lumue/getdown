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
		
		//lookup download item and update if found
		var downloadItemFound=false;
		for(var i = 0, size = this.downloads.length; i < size && !downloadItemFound ; i++){
			if(this.downloads[i].handle==download.handle){
				this.downloads[i]=download;
				downloadItemFound=true;
			}
		}
		
		//otherwise append to list
		if(!downloadItemFound){
			this.downloads.push(download);
		}
		
	}
		
	
});