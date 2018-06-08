/*
 * 
 */

ACZimletNavToolBar = function(params) {
	params.className = params.className || "ACZimletNavToolBar";
	var hasText = (params.hasText !== false);
	params.buttons = this._getButtons(hasText);
	params.toolbarType = ZmId.TB_NAV;
	params.posStyle = params.posStyle || DwtControl.STATIC_STYLE;
	ZmButtonToolBar.call(this, params);
	if (hasText) {
		this._textButton = this.getButton(ZmOperation.TEXT);
	}
};

ACZimletNavToolBar.prototype = new ZmButtonToolBar;
ACZimletNavToolBar.prototype.constructor = ACZimletNavToolBar;

ACZimletNavToolBar.prototype.toString = 
function() {
	return "ACZimletNavToolBar";
};

ACZimletNavToolBar.prototype._getButtons = 
function(hasText) {

	var buttons = [];
	buttons.push(ZmOperation.PAGE_BACK);
	if (hasText) {
		buttons.push(ZmOperation.TEXT);
	}
	buttons.push(ZmOperation.PAGE_FORWARD);

	return buttons;
};

ACZimletNavToolBar.prototype.createOp =
function(id, params) {
	params.textClassName = "ZWidgetTitle ACZimletNavToolBarTitle";
	return ZmButtonToolBar.prototype.createOp.apply(this, arguments);
};

ACZimletNavToolBar.prototype.setText =
function(text) {
	if (!this._textButton) return;
	this._textButton.setText(text);
};
