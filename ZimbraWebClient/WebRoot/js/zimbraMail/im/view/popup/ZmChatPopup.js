/*
 * 
 */

ZmChatPopup = function(params) {
	ZmTaskbarPopup.call(this, params);
	this._chat = params.data.chat;

	var args = {
		parent: this,
		posStyle: Dwt.STATIC_STYLE
	};
	this.chatWidget = new ZmChatWidget(args);
	this.chatWidget.addCloseListener(params.data.closeListener);
	this.chatWidget.addMinimizeListener(params.data.minimizeListener);
	this.chatWidget.addStatusListener(params.data.statusListener);
	this.chatWidget._setChat(this._chat);
};

ZmChatPopup.prototype = new ZmTaskbarPopup;
ZmChatPopup.prototype.constructor = ZmChatPopup;

ZmChatPopup.prototype.toString =
function() {
	return "ZmChatPopup";
};

ZmChatPopup.prototype.popup =
function(background) {
	ZmTaskbarPopup.prototype.popup.apply(this, arguments);
	if (!background) {
		this.chatWidget.focus();
	}
};

