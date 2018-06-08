/*
 * 
 */

function Com_Zimbra_Search_Yahoo(zimlet) {
	this.zimlet = zimlet;
	this.icon = "Yahoo-Icon";
	this.label = this.zimlet.getMessage("com_zimbra_search_yahoo");
};

Com_Zimbra_Search_Yahoo.prototype.getSearchFormHTML =
function(query) {
	var zimlet = this.zimlet;
	var props = {
		query : query
	};
	var code = zimlet.getConfig("yahoo-search-code");
	code = zimlet.xmlObj().replaceObj(ZmZimletContext.RE_SCAN_PROP, code, props);
	return code;
};

Com_Zimbra_Search.registerHandler(Com_Zimbra_Search_Yahoo);
