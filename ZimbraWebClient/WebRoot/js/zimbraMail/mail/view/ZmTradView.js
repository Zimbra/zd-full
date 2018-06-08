/*
 * 
 */

ZmTradView = function(params) {

	params.className = params.className || "ZmTradView";
	params.mode = ZmId.VIEW_TRAD;
	ZmDoublePaneView.call(this, params);
}

ZmTradView.prototype = new ZmDoublePaneView;
ZmTradView.prototype.constructor = ZmTradView;

ZmTradView.prototype.toString = 
function() {
	return "ZmTradView";
};

ZmTradView.prototype._createMailListView =
function(params) {
	return ZmDoublePaneView.prototype._createMailListView.apply(this, arguments);
};
