/*
 * 
 */

/**
* @constructor
* @class ZaServerListView
* @param parent
* @author Greg Solovyev
**/

ZaServerListView = function(parent) {
	if (arguments.length == 0) return;
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;	
	var headerList = ZaServerListView._getHeaderList();
	
	ZaListView.call(this, {
		parent:parent, 
		className:className, 
		posStyle:posStyle, 
		headerList:headerList,
		id:ZaId.TAB_SERVER_MANAGE
	});

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);	
}  

ZaServerListView.prototype = new ZaListView;
ZaServerListView.prototype.constructor = ZaServerListView;

ZaServerListView.prototype.toString = 
function() {
	return "ZaServerListView";
}

ZaServerListView.prototype.getTitle = 
function () {
	return ZaMsg.Servers_view_title;
}

ZaServerListView.prototype.getTabIcon =
function () {
	return "Server";
}

/**
* Renders a single item as a DIV element.
*/
ZaServerListView.prototype._createItemHtml =
function(server, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(server, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='0' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;
		if(field == ZaServer.A_ServiceHostname) {	
			// name
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(server.attrs[ZaServer.A_ServiceHostname]);
			html[idx++] = "</nobr></td>";
		} else if(field == ZaServer.A_description) {	
			// description
			html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(
                   ZaItem.getDescriptionValue(server.attrs[ZaServer.A_description]));
			html[idx++] = "</nobr></td>";
		}
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaServerListView._getHeaderList =
function() {

	var headerList = new Array();
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	var sortable=1;
	headerList[0] = new ZaListHeaderItem(ZaServer.A_ServiceHostname, ZaMsg.SLV_ServiceHName_col, null, 200, sortable++, ZaServer.A_ServiceHostname, true, true);

	headerList[1] = new ZaListHeaderItem(ZaServer.A_description, ZaMsg.DLV_Description_col, null, "auto", null, ZaServer.A_description, true, true);
		
	return headerList;
}


