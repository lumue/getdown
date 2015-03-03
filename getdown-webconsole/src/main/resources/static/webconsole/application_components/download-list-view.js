Polymer('download-list-view', {
	
	ready : function() {		
		this.reload();
	},

	reload : function(){
		Getdown.downloadHttpClient.list(this,function(caller,newValue){
			caller.downloads=newValue;
		})
	}
		
	
});