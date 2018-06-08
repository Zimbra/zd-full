/*
 * 
 */

/**
 * @overview
 */

/**
 * Creates the call assistant.
 * @class
 * This class represents a call assistant.
 * 
 * @extends		ZmAssistant
 */
ZmCallAssistant = function() {
	ZmAssistant.call(this, ZmMsg.call, ZmMsg.ASST_CMD_CALL);
};

ZmCallAssistant.prototype = new ZmAssistant();
ZmCallAssistant.prototype.constructor = ZmCallAssistant;

ZmCallAssistant.prototype.okHandler =
function(dialog) {
	return true;	//override
};

ZmCallAssistant.prototype.handle =
function(dialog, verb, args) {
	dialog._setOkButton(AjxMsg.ok, true, true);
};

ZmCallAssistant.prototype.initialize =
function(dialog) {
	var html = new AjxBuffer();
	html.append("<div>HelloWorld</div>");
	dialog.setAssistantContent(html.toString());
};

// called when dialog switches away from this assistant
ZmCallAssistant.prototype.finish =
function(dialog) {

};
