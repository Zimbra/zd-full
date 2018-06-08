/*
 * 
 */

ZmZimbraImServiceController = function(roster) {
	var capabilities = [
		ZmImServiceController.ACCOUNTS,
		ZmImServiceController.CONFERENCES,
		ZmImServiceController.AUTO_LOGIN_PREF,
		ZmImServiceController.INSTANT_NOTIFY
	];
	ZmImServiceController.call(this, roster, capabilities);

	this.service = new ZmZimbraImService(roster);
};

ZmZimbraImServiceController.prototype = new ZmImServiceController;
ZmZimbraImServiceController.prototype.constructor = ZmZimbraImServiceController;


// Public methods

ZmZimbraImServiceController.prototype.toString =
function() {
	return "ZmZimbraImServiceController";
};

ZmZimbraImServiceController.prototype.login =
function(params) {
	if (!this._showedJive) {
		this._chatListChangeListenerObj = new AjxListener(this, this._chatListChangeListener);
		ZmImApp.INSTANCE.getRoster().getChatList().addChangeListener(this._chatListChangeListenerObj);
	}
	this.service.login(params);
};

ZmZimbraImServiceController.prototype.getMyPresenceTooltip =
function(showText) {
	this._presenceTooltipFormat = this._presenceTooltipFormat || new AjxMessageFormat(ZmMsg.presenceTooltip);
	return this._presenceTooltipFormat.format(showText);
};

ZmZimbraImServiceController.prototype.defineStatusMenu =
function() {
	return {
		statuses: [
			ZmOperation.IM_PRESENCE_OFFLINE,
			ZmOperation.IM_PRESENCE_ONLINE,
			ZmOperation.IM_PRESENCE_CHAT,
			ZmOperation.IM_PRESENCE_DND,
			ZmOperation.IM_PRESENCE_AWAY,
			ZmOperation.IM_PRESENCE_XA
		]
	};
};

ZmZimbraImServiceController.prototype._chatListChangeListener =
function(ev) {
	if (!this._showedJive && (ev.event == ZmEvent.E_CREATE)) {
		this._showedJive = true;
		ZmImApp.INSTANCE.getRoster().getChatList().removeChangeListener(this._chatListChangeListenerObj);
		delete this._chatListChangeListenerObj;
		var statusArgs = {
			msg: AjxTemplate.expand("im.Chat#JiveNotification"),
			transitions: [ { type: "fade-in", duration: 500 }, { type: "pause", duration: 5000 }, { type: "fade-out", duration: 500 } ]
		};
		appCtxt.setStatusMsg(statusArgs);
	}
};


