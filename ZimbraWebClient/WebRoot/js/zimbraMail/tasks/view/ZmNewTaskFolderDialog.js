/*
 * 
 */

/**
 * @overview
 * This file contains the new task folder dialog.
 * 
 */

/**
 * Creates the new task folder dialog.
 * @class
 * This class represents the new task folder dialog.
 * 
 * @param	{DwtControl}	parent		the parent
 * @param	{String}	className		the class name
 * 
 * @extends		ZmNewOrganizerDialog
 */
ZmNewTaskFolderDialog = function(parent, className) {
	ZmNewOrganizerDialog.call(this, parent, className, ZmMsg.createNewTaskFolder, ZmOrganizer.TASKS);
};

ZmNewTaskFolderDialog.prototype = new ZmNewOrganizerDialog;
ZmNewTaskFolderDialog.prototype.constructor = ZmNewTaskFolderDialog;


// Public methods

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmNewTaskFolderDialog.prototype.toString =
function() {
	return "ZmNewTaskFolderDialog";
};


// Protected methods

// overload since we always want to init the color to grey
ZmNewTaskFolderDialog.prototype._initColorSelect =
function() {
	var option = this._colorSelect.getOptionWithValue(ZmOrganizer.C_ORANGE);
	this._colorSelect.setSelectedOption(option);
};

ZmNewTaskFolderDialog.prototype._getRemoteLabel =
function() {
	return ZmMsg.addRemoteTasks;
};

// overload so we dont show this
ZmNewTaskFolderDialog.prototype._createFolderContentHtml =
function(html, idx) {
	return idx;
};
