function formatBytes(bytes,decimals) {

	if(!bytes)
		return ""

	if(bytes == 0) return '0 Bytes';
	var k = 1024,
			dm = decimals || 2,
			sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
			i = Math.floor(Math.log(bytes) / Math.log(k));
	return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

function formatDateTime(datetimestring){
	var date = moment(datetimestring, "YYY-MM-DDTHH:mm:ssZ").toDate();
	var day = date.getDate();
	var monthIndex = date.getMonth()+1;
	var year = date.getFullYear();
	var hours=date.getHours();
	var minutes=date.getMinutes();
	return day+"."+monthIndex+"."+" "+hours+":"+minutes
}