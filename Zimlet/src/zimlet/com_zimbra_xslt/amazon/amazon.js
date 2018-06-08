/*
 * 
 */

function _initAmazon() {
	function _queryAmazon(ctxt, q, domain) {
		var searchIndex;

		if (domain == "awsmusic") {
			searchIndex = "Music";
		} else if (domain == "awsbooks") {
			searchIndex = "Books";
		}

		var q_url = ctxt.getConfig("amznUrl");
		var args = { Service: "AWSECommerceService", 
					 Operation: "ItemSearch", 
					 SearchIndex: searchIndex, 
					 ResponseGroup: "Request,Small", 
					 Version: "2004-11-10" };
		args.SubscriptionId = ctxt.getConfig("amazonKey");
		args.Keywords = AjxStringUtil.urlEncode(q);
		var sep = "?";
		for (var arg in args) {
			q_url = q_url + sep + arg + "=" + args[arg];
			sep = "&";
		}
		return {"url":q_url, "req":null}
	};

	var amzn = new Object();
	amzn.label = "Amazon Music";
	amzn.id = "awsmusic";
	amzn.icon = "Amazon-panelIcon";
	amzn.xsl = "amazon/amazon.xsl";
	amzn.queryAmazon = _queryAmazon;
	amzn.getRequest = 
		function (ctxt, q) { return this.queryAmazon(ctxt, q, this.id) };
		
	Com_Zimbra_Xslt.registerService(amzn);

	amzn = new Object();
	amzn.label = "Amazon Books";
	amzn.id = "awsbooks";
	amzn.icon = "Amazon-panelIcon";
	amzn.xsl = "amazon/amazon.xsl";
	amzn.queryAmazon = _queryAmazon;
	amzn.getRequest = 
		function (ctxt, q) { return this.queryAmazon(ctxt, q, this.id) };
		
	Com_Zimbra_Xslt.registerService(amzn);
};

_initAmazon();
