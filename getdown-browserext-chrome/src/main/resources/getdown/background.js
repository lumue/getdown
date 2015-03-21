// Copyright (c) 2013 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.


chrome.contextMenus.onClicked.addListener(function(info, tab) {
	var contentUrl=info.linkUrl;
	var xmlhttp = new XMLHttpRequest();
	var url = "http://localhost:8001/download/add?url="+encodeURIComponent(contentUrl);

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
