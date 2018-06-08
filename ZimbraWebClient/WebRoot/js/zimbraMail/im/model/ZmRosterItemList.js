/*
 * 
 */

ZmRosterItemList = function() {
	ZmList.call(this, ZmItem.ROSTER_ITEM);
};

ZmRosterItemList.prototype = new ZmList;
ZmRosterItemList.prototype.constructor = ZmRosterItemList;

ZmRosterItemList.prototype.toString =
function() {
	return "ZmRosterItemList";
};

ZmRosterItemList.prototype.setLoaded =
function() {
	this._notify(ZmEvent.E_LOAD);
};

ZmRosterItemList.prototype.addItem = function(item, skipNotify, index) {
	this.add(item, index);
	if (!skipNotify) {
		this._notify(ZmEvent.E_CREATE, {items: [item]});
	}
};

ZmRosterItemList.prototype.addItems = function(items) {
	for (var i = 0, count = items.length; i < count; i++) {
		this.add(items[i]);
	}
	this._notify(ZmEvent.E_CREATE, {items: items});
};

ZmRosterItemList.prototype.removeItem = function(item, skipNotify) {
	if (!item.isDefaultBuddy()) {
		this.remove(item);
		if (!skipNotify) {
			this._notify(ZmEvent.E_REMOVE, {items: [item]});
		}
	}
};

ZmRosterItemList.prototype.getByAddr =
function(addr) {
    return this.getById(addr.toLowerCase());
};

/**
 * return an array of all groups (uniqified)
 */

ZmRosterItemList.prototype.getGroupsArray = function() {
	// TODO: cache. not currently used.
	var hash = {};
	var result = [];
	var listArray = this.getArray();
	for (var i=0; i < listArray.length; i++) {
		var groups = listArray[i].getGroups();
		for (var g in groups) {
			var name = groups[g];
			if (!(name in hash)) {
				hash[name] = true;
				result.push(name);
			}
		}
	}
	return result;
};

ZmRosterItemList.prototype.removeAllItems = function() {
	// get a clone, since we are removing while iterating...
	var listArray = this.getVector().clone().getArray();
	for (var i = 0; i < listArray.length; i++) {
		this.removeItem(listArray[i]);
	}
};
