/*
 * 
 */

/**
 * @overview
 */

/**
 * Creates a tag picker control.
 * @class
 * This class represents a tag picker control.
 * 
 * @param		{DwtControl}	parent		the parent
 * 
 * @extends		ZmPicker
 * @see			ZmPicker.TAG
 */
ZmTagPicker = function(parent) {

	ZmPicker.call(this, parent, ZmPicker.TAG);

    this._checkedItems = {};
}

ZmTagPicker._OVERVIEW_ID = "ZmTagPicker";

ZmTagPicker.prototype = new ZmPicker;
ZmTagPicker.prototype.constructor = ZmTagPicker;

ZmPicker.CTOR[ZmPicker.TAG] = ZmTagPicker;

ZmTagPicker.prototype.toString = 
function() {
	return "ZmTagPicker";
}

ZmTagPicker.prototype._setupPicker =
function(parent) {
	var overviewId = ZmTagPicker._OVERVIEW_ID + "_" + Dwt.getNextId();
	this._setOverview(overviewId, parent, [ZmOrganizer.TAG]);
}

ZmTagPicker.prototype._updateQuery = 
function() {
	var tags = [];
	for (var tagId in this._checkedItems) {
		var tag = this._checkedItems[tagId];
		tags.push('"' + tag.name + '"');
	}
		
	var num = tags.length;
	if (num) {
		var query = tags.join(" OR ");
		if (num > 1) {
			query = "(" + query + ")";
		}
		this.setQuery("tag:" + query);
	} else {
		this.setQuery("");
	}
	this.execute();
}

ZmTagPicker.prototype._treeListener =
function(ev) {
 	if (ev.detail == DwtTree.ITEM_CHECKED) {
 		var ti = ev.item;
 		var checked = ti.getChecked();
 		var tagId = ti.getData(Dwt.KEY_ID);
 		var tag = ti.getData(Dwt.KEY_OBJECT);
 		if (ti.getChecked()) {
			this._checkedItems[tagId] = tag;
 		} else {
			delete this._checkedItems[tagId];
 		}
		this._updateQuery();
 	}
}
