Polymer('add-download-dialog', {
	
	ready : function() {
		this.input="";
	},
	
	toggle : function(){
		this.$.adddownloaddialog.toggle();
	},
	
	onCancelClicked : function(e) {
		this.toggle();
	},

	onAcceptClicked : function(e){
		this.toggle();
		Getdown.downloadHttpClient.add(this,this.input,this.onDownloadAdded);
	},
	
	onDownloadAdded : function(caller,data){
		caller.fire('core-signal',  {name: 'download-added', data: data});
	}

});