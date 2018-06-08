/*
 * 
 */

ZmCustomStatusDlg = function(params) {
	ZmDialog.call(this, params);
	this._setNameField(this._messageFieldId);
};

ZmCustomStatusDlg.prototype = new ZmDialog;
ZmCustomStatusDlg.prototype.constructor = ZmCustomStatusDlg;

ZmCustomStatusDlg.prototype.toString =
function() {
	return "ZmCustomStatusDlg";
};

ZmCustomStatusDlg.prototype.popup =
function () {
	ZmDialog.prototype.popup.call(this);
	Dwt.byId(this._messageFieldId).focus();
};

ZmCustomStatusDlg.prototype.getValue =
function() {
	return Dwt.byId(this._messageFieldId).value;
};

ZmCustomStatusDlg.prototype._contentHtml =
function() {
	this._messageFieldId = Dwt.getNextId();
	return AjxTemplate.expand("im.Chat#ZmCustomStatusDlg", { id: this._messageFieldId });
};

ZmCustomStatusDlg.prototype._enterListener =
function() {
	this._runEnterCallback();
};

ZmCustomStatusDlg.prototype._okButtonListener =
function(ev) {
	ZmDialog.prototype._buttonListener.call(this, ev);
};
