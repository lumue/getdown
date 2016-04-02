chrome.contextMenus.onClicked.addListener(function(info, tab) {
	var contentUrl=info.linkUrl;
	var xmlhttp = new XMLHttpRequest();
	var url = "http://192.168.1.105:7001/download/add?url="+encodeURIComponent(contentUrl);

	xmlhttp.onreadystatechange = function() {
	    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
	       ondata(JSON.parse(xmlhttp.responseText));
	    }
	}
	xmlhttp.open("PUT", url, true);
	xmlhttp.send();
});

chrome.contextMenus.create({
  id: 'open',
  title: chrome.i18n.getMessage('openContextMenuTitle'),
  contexts: ['link'],
});
