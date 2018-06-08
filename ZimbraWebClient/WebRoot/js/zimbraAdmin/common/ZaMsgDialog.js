/*
 * 
 */

/**
* Creates a new message dialog.
* @constructor
* @class
* This class represents a reusable message dialog box. Messages can be informational, warning, or
* critical.
*/
ZaMsgDialog = function(parent, className, buttons, extraButtons, contextId) {
	this._app = ZaApp.getInstance();
    	var id = contextId? ZaId.getDialogId(ZaId.DLG_MSG, contextId):ZaId.getDialogId(ZaId.DLG_MSG);
 	DwtMessageDialog.call(this, parent, className, buttons, extraButtons, id);
}


ZaMsgDialog.prototype = new DwtMessageDialog;
ZaMsgDialog.prototype.constructor = ZaMsgDialog;

ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON = "close tab and delete";
ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON_DESC = 
	new DwtDialog_ButtonDescriptor (ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON, ZaMsg.bt_close_tab_delete, DwtDialog.ALIGN_RIGHT);
ZaMsgDialog.NO_DELETE_BUTTON = "no delete" ;
ZaMsgDialog.NO_DELETE_BUTTON_DESC = 
	new DwtDialog_ButtonDescriptor (ZaMsgDialog.NO_DELETE_BUTTON, ZaMsg.bt_no_delete, DwtDialog.ALIGN_RIGHT);
