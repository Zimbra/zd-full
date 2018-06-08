/*
 * 
 */

/**
 * Allows downloading a single email message.
 * 
 * @author Raja Rao DV
 */
function ZmMojibakeZimlet() {
}

ZmMojibakeZimlet.prototype = new ZmZimletBase();
ZmMojibakeZimlet.prototype.constructor = ZmMojibakeZimlet;


/**
 * Called by the framework on an droppedItem drop.
 * 
 * @param	{ZmConv|ZmMailMsg}	droppedItem		the dropped message object
 */
ZmMojibakeZimlet.prototype.doDrop =
function(droppedItem) {
	var msg;
	if(droppedItem instanceof Array) {
		droppedItem = droppedItem[0];
	} 
	var obj = droppedItem.srcObj ? droppedItem.srcObj : droppedItem;
	if (obj.type == "CONV"){
		msg = obj.getFirstHotMsg();
	} else if(obj.type == "MSG") {
		msg = obj;
	} else {
		return;
	}
	if(!msg._loaded) {
		msg.load({});
	}
	this._showEmailInNewWindow(msg);
};

ZmMojibakeZimlet.prototype._showEmailInNewWindow =
function(msg) {
	var brwsrOptn = this.getMessage("firefox");
	if(AjxEnv.isIE) {
		brwsrOptn = this.getMessage("internetExplorer");
	} else if(AjxEnv.isChrome) {
		brwsrOptn = this.getMessage("chrome");
	}
	var body = AjxStringUtil.nl2br(msg.getBodyContent());
	var win = window.open("_blank", "");
	var winContent = ["<div style='font-family:monospace;'><label style='color:red;'>", this.getMessage("changeCharSet"), "<br>", 
		brwsrOptn, "</label><br><br><h3>",msg.subject,"</h3>",body, "</div>"].join("");
	win.document.write(winContent);
};