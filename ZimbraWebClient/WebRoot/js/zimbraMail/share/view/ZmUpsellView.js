/*
 * 
 */

/**
 * 
 * @extends		DwtControl
 * @private
 */
ZmUpsellView = function(params) {
	DwtControl.call(this, params);
}
ZmUpsellView.prototype = new DwtControl;
ZmUpsellView.prototype.constructor = ZmUpsellView;

ZmUpsellView.prototype.toString = function() {
	return "ZmUpsellView";
};

ZmUpsellView.prototype.setBounds =
function(x, y, width, height, showToolbar) {
    var deltaHeight = 0;
    if(!showToolbar) {
        deltaHeight = this._getToolbarHeight();
    }
	DwtControl.prototype.setBounds.call(this, x, y - deltaHeight, width, height + deltaHeight);
	var id = "iframe_" + this.getHTMLElId();
	var iframe = document.getElementById(id);
	if(iframe) {
    	iframe.width = width;
    	iframe.height = height + deltaHeight;
	}
};

ZmUpsellView.prototype._getToolbarHeight =
function() {
    var topToolbar = appCtxt.getAppViewMgr().getCurrentViewComponent(ZmAppViewMgr.C_TOOLBAR_TOP);
	if (topToolbar) {
		var sz = topToolbar.getSize();
		var height = sz.y ? sz.y : topToolbar.getHtmlElement().clientHeight;
		return height;
	}
	return 0;
};
