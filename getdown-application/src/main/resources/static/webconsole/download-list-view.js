Polymer('download-list-view', {
	
	ready : function() {		
		this.reload();
	},

	reload : function(){
		Getdown.downloadHttpClient.list(function(newValue){
			this.downloads=newValue;
		})
	}
		
	
});