/*
 * 
 */

/**
 * Creates the new briefcase dialog.
 * @class
 * This class represents the new briefcase dialog.
 * 
 * @param	{ZmControl}	parent		the parent
 * @param	{String}	className		the class name
 * 
 * @extends		ZmNewOrganizerDialog
 */
ZmNewBriefcaseDialog = function(parent, className) {
	var title = ZmMsg.createNewBriefcaseItem;
	var type = ZmOrganizer.BRIEFCASE;
	ZmNewOrganizerDialog.call(this, parent, className, title, type);
}

ZmNewBriefcaseDialog.prototype = new ZmNewOrganizerDialog;
ZmNewBriefcaseDialog.prototype.constructor = ZmNewBriefcaseDialog;

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmNewBriefcaseDialog.prototype.toString = 
function() {
	return "ZmNewBriefcaseDialog";
}

// Protected methods

// NOTE: don't show remote checkbox
ZmNewBriefcaseDialog.prototype._createRemoteContentHtml =
function(html, idx) {
	return idx;
};
