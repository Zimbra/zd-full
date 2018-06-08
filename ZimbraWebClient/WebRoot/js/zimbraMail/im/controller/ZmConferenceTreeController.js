/*
 * 
 */

ZmConferenceTreeController = function() {

	ZmTreeController.call(this, ZmOrganizer.CONFERENCE_ITEM);
};

ZmConferenceTreeController.prototype = new ZmTreeController;
ZmConferenceTreeController.prototype.constructor = ZmConferenceTreeController;


// Public Methods
ZmConferenceTreeController.prototype.toString =
function() {
	return "ZmConferenceTreeController";
};

ZmConferenceTreeController.prototype.getDataTree =
function() {
	var result = ZmImApp.INSTANCE.getRoster().getConferenceTree();
	if (!this._dataChangeListener) {
		result.addChangeListener(this._getTreeChangeListener());
	}
	return result;
};

ZmConferenceTreeController.prototype.resetOperations =
function(parent, type, id) {
	var folder = appCtxt.getById(id);
	parent.enable(ZmOperation.EXPAND_ALL, (folder.size() > 0));
};

ZmConferenceTreeController.prototype._treeListener =
function(ev) {
	ZmTreeController.prototype._treeListener.apply(this, arguments);

	if (ev.detail == DwtTree.ITEM_EXPANDED) {
		var organizer = ev && ev.item && ev.item.getData(Dwt.KEY_OBJECT);
		if ((organizer instanceof ZmConferenceService) && !organizer.roomsLoaded) {
			organizer.getRooms();
		}
	}
};

// Returns a list of desired header action menu operations
ZmConferenceTreeController.prototype._getHeaderActionMenuOps =
function() {
	return null;
};

ZmConferenceTreeController.prototype._getActionMenu =
function(ev) {
	return null;
};
