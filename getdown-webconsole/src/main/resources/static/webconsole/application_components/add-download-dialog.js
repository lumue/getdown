Polymer('add-download-dialog', {
	
	ready : function() {
		this.input="";
	},
	
	toggle : function(){
		this.$.dialog.toggle();
	},
	
	onCancelClicked : function(e) {
		this.toggle();
	},

	onAcceptClicked : function(e){
		this.toggle();
		var thisView=this;
		Getdown.downloadHttpClient.add(this.input,function(data){
			thisView.fire('core-signal',  {name: 'download-added', data: data});
		});
	}

});