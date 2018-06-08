/*
 * 
 */

function _initWiki() {
	var wiki = new Object();
	wiki.label = "Wikipedia";
	wiki.id = "wikipedia";
	wiki.icon = "Wiki-panelIcon";
	wiki.xsl = "wiki/mediawiki.xsl";
	wiki.getRequest = 
		function (ctxt, q) {
			return {"url":ctxt.getConfig("wikipediaUrl") + AjxStringUtil.urlEncode(q), "req":null}
		};
		
	Com_Zimbra_Xslt.registerService(wiki);

	wiki = new Object();
	wiki.label = "Wiktionary";
	wiki.id = "wiktionary";
	wiki.icon = "Wiki-panelIcon";
	wiki.xsl = "wiki/mediawiki.xsl";
	wiki.getRequest = 
		function (ctxt, q) {
			return {"url":ctxt.getConfig("wiktionaryUrl") + AjxStringUtil.urlEncode(q), "req":null}
		};
		
	Com_Zimbra_Xslt.registerService(wiki);
};

_initWiki();
