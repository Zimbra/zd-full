/*
 * 
 */

ZmShareTreeController = function() {
    ZmTreeController.call(this, ZmOrganizer.SHARE);
};
ZmShareTreeController.prototype = new ZmTreeController;
ZmShareTreeController.prototype.constructor = ZmShareTreeController;

ZmShareTreeController.prototype.toString = function() {
    return "ZmShareTreeController";
};

//
// ZmTreeController methods
//

ZmShareTreeController.prototype.getDataTree = function(account) {
    var tree = new ZmFolderTree(ZmOrganizer.SHARE);
    var obj = { id: ZmOrganizer.ID_ROOT, name: ZmMsg.sharedFoldersHeader };
    tree.root = ZmFolderTree.createFolder(ZmOrganizer.SHARE, null, obj, tree);
    return tree;
};

ZmShareTreeController.prototype._createTreeView = function(params) {
	return new ZmShareTreeView(params);
};
