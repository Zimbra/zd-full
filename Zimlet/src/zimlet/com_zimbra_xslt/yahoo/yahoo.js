/*
 * 
 */

function _initYahoo() {
	var yhoo = new Object();
	yhoo.label = "Yahoo";
	yhoo.id = "yahoosearch";
	yhoo.icon = "Yahoo-panelIcon";
	yhoo.xsl = "yahoo/yahoo.xsl";
	yhoo.getRequest = 
		function (ctxt, q) {
			var args = {};
			args.appid = ctxt.getConfig("ywsAppId");
			args.results = ctxt.getConfig("numResults");
			args.query = AjxStringUtil.urlEncode(q);

			var q_url;
			q_url = ctxt.getConfig("yhooSearchUrl");
			var sep = "?";
			for (var arg in args) {
				q_url = q_url + sep + arg + "=" + args[arg];
				sep = "&";
			}
			return {"url":q_url, "req":null}
		};
		
	Com_Zimbra_Xslt.registerService(yhoo);
};

_initYahoo();
