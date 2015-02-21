Polymer('download-list-view', {
	
	ready : function() {		
		
		this.downloads = [ {
			name : 'The Big Bang Theory S01E01.mp4',
			state : 'running',
			completed_percentage: '80'
		}, {
			name : 'The Big Bang Theory S01E02.mp4',
			state : 'waiting',
			completed_percentage: '0'
		},
		{
			name : 'The Big Bang Theory S01E03.mp4',
			state : 'waiting',
			completed_percentage: '0'
		}];
	
	}
});