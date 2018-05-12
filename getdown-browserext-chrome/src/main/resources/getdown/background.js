chrome.contextMenus.onClicked.addListener(function(info, tab) {
	var contentUrl=[info.linkUrl];

	fetch("http://192.168.1.105:7001/api/tasks", {
		method: "POST",
		headers: {
			'Accept': 'application/json, text/plain, */*',
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(contentUrl)
	})
});

chrome.contextMenus.create({
  id: 'open',
  title: chrome.i18n.getMessage('openContextMenuTitle'),
  contexts: ['link'],
});
