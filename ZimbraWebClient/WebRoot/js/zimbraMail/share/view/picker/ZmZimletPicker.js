/*
 * 
 */

/**
 * @overview
 */

/**
 * Creates a zimlet picker control.
 * @class
 * This class represents a zimlet picker control.
 * 
 * @param		{DwtControl}	parent		the parent
 * 
 * @extends		ZmPicker
 * @see			ZmPicker.ZIMLET
 */
ZmZimletPicker = function(parent) {

	ZmPicker.call(this, parent, ZmPicker.ZIMLET);
}

ZmZimletPicker.prototype = new ZmPicker();
ZmZimletPicker.prototype.constructor = ZmZimletPicker;

ZmPicker.CTOR[ZmPicker.ZIMLET] = ZmZimletPicker;

ZmZimletPicker.prototype.toString = 
function() {
	return "ZmZimletPicker";
};

ZmZimletPicker.prototype._addZimlet =
function(tree, text, imageInfo, type) {
	var ti = this._zimlets[type] = new DwtTreeItem({parent:tree});
	ti.setText(text);
	ti.setImage(imageInfo);
};

ZmZimletPicker.prototype._setupPicker =
function(picker) {
    this._zimlets = {};
    var idxZimlets = appCtxt.getZimletMgr().getIndexedZimlets()
    if (idxZimlets.length) {
        var tree = this._tree = new DwtTree({parent:picker, style:DwtTree.CHECKEDITEM_STYLE});
        tree.addSelectionListener(new AjxListener(this, ZmZimletPicker.prototype._treeListener));
        for (var i = 0; i < idxZimlets.length; i += 1) {
            this._addZimlet(tree, idxZimlets[i].description, idxZimlets[i].icon, idxZimlets[i].keyword);
        }
    }
};

ZmZimletPicker.prototype._updateQuery = 
function() {
	var types = new Array();
	for (var type in this._zimlets)
		if (this._zimlets[type].getChecked())
			types.push(type);
	
	if (types.length) {
		var query = types.join(" OR ");
		if (types.length > 1)
			query = "(" + query + ")";
		this.setQuery("has:" + query);
	} else {
		this.setQuery("");
	}
	this.execute();
}

ZmZimletPicker.prototype._treeListener =
function(ev) {
 	if (ev.detail == DwtTree.ITEM_CHECKED) {
 		this._updateQuery();
 	}
}
