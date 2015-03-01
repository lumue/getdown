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
		Getdown.downloadHttpClient.add(this.input,function(data){
			this.fireAsync('core-signal', {name: "download-added", data: data});
		});
	}

});