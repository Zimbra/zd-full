/*
 * 
 */

/**
 * @overview
 */

/**
 * Creates the version assistant.
 * @class
 * This class represents a version assistant.
 * 
 * @extends		ZmAssistant
 */
ZmVersionAssistant = function() {
	ZmAssistant.call(this, "Client Version Information", ".version");
};

ZmVersionAssistant.prototype = new ZmAssistant();
ZmVersionAssistant.prototype.constructor = ZmVersionAssistant;

ZmVersionAssistant.prototype.handle =
function(dialog, verb, args) {
	dialog._setOkButton(AjxMsg.ok, true, true);
	this._setField("Version", appCtxt.get(ZmSetting.CLIENT_VERSION), false, true);
	this._setField("Release", appCtxt.get(ZmSetting.CLIENT_RELEASE), false, true);
	this._setField("Build Date", appCtxt.get(ZmSetting.CLIENT_DATETIME), false, true);	
};
