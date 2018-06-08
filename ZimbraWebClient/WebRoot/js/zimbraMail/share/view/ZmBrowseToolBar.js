/*
 * 
 */

/**
 * @overview
 */

/**
 * Creates the browser tool bar.
 * @class
 * This class represents the browse tool bar.
 * 
 * @param	{DwtControl}	parent		the parent
 * @param	{Array}		pickers			an array of {ZmPicker} objects
 * @extends		ZmToolBar
 */
ZmBrowseToolBar = function(parent, pickers) {

	ZmToolBar.call(this, {parent:parent, className:"ZmBrowseToolBar"});
	
	for (var i = 0; i < pickers.length; i++) {
		var id = pickers[i];
		var b = this.createButton(id, {image:ZmPicker.IMAGE[id], text:ZmMsg[ZmPicker.MSG_KEY[id]],
									   tooltip:ZmMsg[ZmPicker.TT_MSG_KEY[id]]});
		b.setData(ZmPicker.KEY_ID, id);
		b.setData(ZmPicker.KEY_CTOR, ZmPicker.CTOR[id]);
	}

	this.addSeparator();

	var id = ZmPicker.RESET;
	var b = this.createButton(id, {image:ZmPicker.IMAGE[id], text:ZmMsg[ZmPicker.MSG_KEY[id]],
								   tooltip:ZmMsg[ZmPicker.TT_MSG_KEY[id]]});
	b.setData(ZmPicker.KEY_ID, id);

	this.addFiller();

	var id = ZmPicker.CLOSE;
	var text = ZmMsg[ZmPicker.MSG_KEY[id]];
	var b = this.createButton(id, {image:ZmPicker.IMAGE[id], text:text,
								   tooltip:ZmMsg[ZmPicker.TT_MSG_KEY[id]]});
	b.setData(ZmPicker.KEY_ID, id);
};

ZmBrowseToolBar.prototype = new ZmToolBar;
ZmBrowseToolBar.prototype.constructor = ZmBrowseToolBar;

ZmBrowseToolBar.prototype.toString = 
function() {
	return "ZmBrowseToolBar";
};
