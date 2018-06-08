/*
 * 
 */

/**
 * @overview
 */

/**
 * Creates a folder picker control.
 * @class
 * This class represents a folder picker control.
 * 
 * @param		{DwtControl}	parent		the parent
 * 
 * @extends		ZmPicker
 * @see			ZmPicker.FOLDER
 */
ZmFolderPicker = function(parent) {

	ZmPicker.call(this, parent, ZmPicker.FOLDER);

    this._checkedItems = new AjxVector();
};

ZmFolderPicker._OVERVIEW_ID = "ZmFolderPicker";

ZmFolderPicker.prototype = new ZmPicker;
ZmFolderPicker.prototype.constructor = ZmFolderPicker;

ZmPicker.CTOR[ZmPicker.FOLDER] = ZmFolderPicker;

ZmFolderPicker.prototype.toString = 
function() {
	return "ZmFolderPicker";
};

ZmFolderPicker.prototype._setupPicker =
function(parent) {
	var overviewId = ZmFolderPicker._OVERVIEW_ID + "_" + Dwt.getNextId();
	this._setOverview(overviewId, parent, [ZmOrganizer.FOLDER]);
	this._twiddle();
};

ZmFolderPicker.prototype._updateQuery = 
function() {
	var folders = [];
	var num = this._checkedItems.size();
	for (var i = 0; i < num; i++) {
		var folder = this._checkedItems.get(i);
		folders.push(folder.createQuery(true));
	}
	var query = "";
	if (folders.length) {
		var folderStr = folders.join(" OR ");
		if (folders.length > 1) {
			folderStr = "(" + folderStr + ")";
		}
		query += "in:" + folderStr;
	}
	this.setQuery(query);
	this.execute();
};

ZmFolderPicker.prototype._treeListener =
function(ev) {
 	if (ev.detail == DwtTree.ITEM_CHECKED) {
 		var ti = ev.item;
 		var checked = ti.getChecked();
 		var folder = ti.getData(Dwt.KEY_OBJECT);
 		if (ti.getChecked()) {
			this._checkedItems.add(folder);
 		} else {
			this._checkedItems.remove(folder);
 		}
		this._updateQuery();
 	}
};

// Hide saved searches
ZmFolderPicker.prototype._twiddle =
function() {
	for (var i in this._treeView) {
		var treeView = this._treeView[i];
		for (var id in treeView._treeItemHash) {
			var ti = treeView._treeItemHash[id];
			var organizer = ti.getData(Dwt.KEY_OBJECT);
			if (organizer.type == ZmOrganizer.SEARCH) {
				ti.setVisible(false);
			}
		}
	}
};
