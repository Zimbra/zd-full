/*
 * 
 */

ZmNewNotebookDialog = function(parent, className) {
	var title = ZmMsg.createNewNotebook;
	var type = ZmOrganizer.NOTEBOOK;
	ZmNewOrganizerDialog.call(this, parent, className, title, type);
}

ZmNewNotebookDialog.prototype = new ZmNewOrganizerDialog;
ZmNewNotebookDialog.prototype.constructor = ZmNewNotebookDialog;

ZmNewNotebookDialog.prototype.toString = 
function() {
	return "ZmNewNotebookDialog";
}

// Protected methods

// NOTE: don't show remote checkbox
ZmNewNotebookDialog.prototype._createRemoteContentHtml =
function(html, idx) {
	return idx;
};
