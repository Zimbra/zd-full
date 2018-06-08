/*
 * 
 */

/**
 * @overview
 * This file contains the address book dialog class.
 */

/**
 * Creates an address book dialog.
 * @class
 * This class represents the address book dialog.
 * 
 * @param	{DwtControl}	parent		the parent
 * @param	{String}	className		the class name
 * @param	{String}	title		the dialog title
 * @param	{constant}	type		the type
 * 
 * @extends		ZmNewOrganizerDialog
 */
ZmNewAddrBookDialog = function(parent, className) {
	var title = ZmMsg.createNewAddrBook;
	var type = ZmOrganizer.ADDRBOOK;
	ZmNewOrganizerDialog.call(this, parent, className, title, type);
}

ZmNewAddrBookDialog.prototype = new ZmNewOrganizerDialog;
ZmNewAddrBookDialog.prototype.constructor = ZmNewAddrBookDialog;


// Public methods

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmNewAddrBookDialog.prototype.toString =
function() {
	return "ZmNewAddrBookDialog";
};


// Protected methods

/**
 * overload since we always want to init the color to grey.
 * 
 * @private
 */
ZmNewAddrBookDialog.prototype._initColorSelect =
function() {
	var option = this._colorSelect.getOptionWithValue(ZmOrganizer.DEFAULT_COLOR[this._organizerType]);
	this._colorSelect.setSelectedOption(option);
};

/**
 * overload so we dont show this.
 * 
 * @private
 */
ZmNewAddrBookDialog.prototype._createRemoteContentHtml =
function(html, idx) {
	return idx;
};

/**
 * @private
 */
ZmNewAddrBookDialog.prototype._setupFolderControl =
function(){
    ZmNewOrganizerDialog.prototype._setupFolderControl.call(this);
    if(this._omit) this._omit[ZmFolder.ID_TRASH] = true;
};