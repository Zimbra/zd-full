/*
 * 
 */

ZmNotebookFileView = function(parent, controller) {
	ZmListView.call(this, {parent:parent, controller:controller});
	this._controller = controller;
}

ZmNotebookFileView.prototype = new ZmListView;
ZmNotebookFileView.prototype.constructor = ZmNotebookFileView;

ZmNotebookFileView.prototype.toString = function() {
	return "ZmNotebookFileView";
};

//
// Data
//

ZmNotebookFileView.prototype._controller;

ZmNotebookFileView.prototype._fileListView;

//
// Public methods
//

ZmNotebookFileView.prototype.handleActionPopdown = function(ev) { /*TODO*/ };
