/*
 * 
 */

AMZimletNavToolBar = function(params) {
	params.className = params.className || "AMZimletNavToolBar";
	var hasText = (params.hasText !== false);
	params.buttons = this._getButtons(hasText);
	params.toolbarType = ZmId.TB_NAV;
	params.posStyle = params.posStyle || DwtControl.STATIC_STYLE;
	ZmButtonToolBar.call(this, params);
	if (hasText) {
		this._textButton = this.getButton(ZmOperation.TEXT);
	}
};

AMZimletNavToolBar.prototype = new ZmButtonToolBar;
AMZimletNavToolBar.prototype.constructor = AMZimletNavToolBar;

AMZimletNavToolBar.prototype.toString = 
function() {
	return "AMZimletNavToolBar";
};

AMZimletNavToolBar.prototype._getButtons = 
function(hasText) {

	var buttons = [];
	buttons.push(ZmOperation.PAGE_BACK);
	if (hasText) {
		buttons.push(ZmOperation.TEXT);
	}
	buttons.push(ZmOperation.PAGE_FORWARD);

	return buttons;
};

AMZimletNavToolBar.prototype.createOp =
function(id, params) {
	params.textClassName = "ZWidgetTitle AMZimletNavToolBarTitle";
	return ZmButtonToolBar.prototype.createOp.apply(this, arguments);
};

AMZimletNavToolBar.prototype.setText =
function(text) {
	if (!this._textButton) return;
	this._textButton.setText(text);
};
