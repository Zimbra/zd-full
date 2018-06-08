/*
 * 
 */

/**
 * @overview
 */

/**
 * Creates a rename folder dialog.
 * @class
 * This class represents a rename folder dialog.
 * 
 * @param	{DwtComposite}	parent		the parent
 * @param	{String}	className		the class name
 *  
 * @extends		ZmDialog
 */
ZmRenameFolderDialog = function(parent, className) {

	ZmDialog.call(this, {parent:parent, className:className, title:ZmMsg.renameFolder, id:"RenameFolderDialog"});

	this._setNameField(this._nameFieldId);
};

ZmRenameFolderDialog.prototype = new ZmDialog;
ZmRenameFolderDialog.prototype.constructor = ZmRenameFolderDialog;

ZmRenameFolderDialog.prototype.toString = 
function() {
	return "ZmRenameFolderDialog";
};

/**
 * Pops-up the dialog.
 * 
 * @param	{ZmFolder}		folder		the folder
 * @param	{Object}		[source]	(not used)
 */
ZmRenameFolderDialog.prototype.popup =
function(folder, source) {
	ZmDialog.prototype.popup.call(this);
	var title = (folder.type == ZmOrganizer.SEARCH) ? ZmMsg.renameSearch : ZmMsg.renameFolder;
	this.setTitle(title + ': ' + folder.getName(false, ZmOrganizer.MAX_DISPLAY_NAME_LENGTH));
	this._nameField.value = folder.getName(false, null, true);
	this._folder = folder;
};

ZmRenameFolderDialog.prototype._contentHtml = 
function() {
	this._nameFieldId = this._htmlElId + "_name";
	var subs = {id:this._htmlElId, newLabel:ZmMsg.newName};
	return AjxTemplate.expand("share.Dialogs#ZmRenameDialog", subs);
};

ZmRenameFolderDialog.prototype._okButtonListener =
function(ev) {
	var results = this._getFolderData();
	if (results) {
		DwtDialog.prototype._buttonListener.call(this, ev, results);
	}
};

ZmRenameFolderDialog.prototype._getFolderData =
function() {
	// check name for presence and validity
	var name = AjxStringUtil.trim(this._nameField.value);
	var msg = ZmFolder.checkName(name, this._folder.parent);

	// make sure another folder with this name doesn't already exist at this level
	if (!msg) {
		var folder = this._folder.parent.getByName(name);
		if (folder && (folder.id != this._folder.id)) {
			msg = ZmMsg.folderOrSearchNameExists;
		}
	}

	return (msg ? this._showError(msg) : [this._folder, name]);
};

ZmRenameFolderDialog.prototype._enterListener =
function(ev) {
	var results = this._getFolderData();
	if (results) {
		this._runEnterCallback(results);
	}
};
