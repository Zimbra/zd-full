/*
 * 
 */

/**
 * Creates a two factor code update dialog.
 * @class
 * This class represents a two factor code update dialog.
 *
 * @param	{DwtComposite}	parent		the parent
 * @param	{String}	className		the class name
 *
 * @extends		ZmDialog
 */
ZmTwoFactorCodeUpdateDialog = function(parent) {

	ZmDialog.call(this, {parent:parent, title:ZmMsg.changeTwoFactorCode, id:"TwoFactorCodeUpdateDialog"});
	this._setNameField(this._nameFieldId);
    this._createControls();
};

ZmTwoFactorCodeUpdateDialog.prototype = new ZmDialog;
ZmTwoFactorCodeUpdateDialog.prototype.constructor = ZmTwoFactorCodeUpdateDialog;

ZmTwoFactorCodeUpdateDialog.prototype.toString =
function() {
	return "ZmTwoFactorCodeUpdateDialog";
};

/**
 * Sets the message String
 */
ZmTwoFactorCodeUpdateDialog.prototype.setMsgString =
function(msgString) {
	 this._msgText = msgString;
};


/**
 * Pops-up the dialog.
 */
ZmTwoFactorCodeUpdateDialog.prototype.popup =
function(acct) {
    ZmDialog.prototype.popup.call(this);
    this.acct = acct;
    var desc  = document.getElementById(this._htmlElId + "_desc");
    this._toggleOKButton(false);
    desc.innerHTML = AjxMessageFormat.format(this._msgText, this.acct.name);
    var acctTd  = document.getElementById(this._htmlElId + "_acct");
    acctTd.innerHTML = this.acct.name;
	this._nameField.value = "";
};

ZmTwoFactorCodeUpdateDialog.prototype._createControls =
function() {
    this.setTitle(ZmMsg.offlineAccountAuth);
    this._toggleOKButton(false);
    var cancelBtn = this.getButton(DwtDialog.CANCEL_BUTTON);
    cancelBtn.setText(ZmMsg.dismiss);
    var okBtn = this.getButton(DwtDialog.OK_BUTTON);
	okBtn.setText(ZmMsg.save);
    this._nameField._dlgEl = this._htmlElId;
    Dwt.setHandler(this._nameField, DwtEvent.ONKEYUP, this._handleKeyUp);
	Dwt.setHandler(this._nameField, DwtEvent.ONPASTE, function() {
		setTimeout(function() {
			this._handleContextMenuPaste();
		}.bind(this), 0)
	}.bind(this));
};

ZmTwoFactorCodeUpdateDialog.prototype._contentHtml =
function() {
	this._nameFieldId = this._htmlElId + "_name";
	var subs = {id:this._htmlElId, labelAcct:ZmMsg.account, labelTwoFactorCode:ZmMsg.code};
	return AjxTemplate.expand("share.Dialogs#ZmTwoFactorCodeUpdateDialog", subs);
};


ZmTwoFactorCodeUpdateDialog.prototype._okButtonListener =
function(ev) {
	var twoFactorCode = AjxStringUtil.trim(this._nameField.value);
	if (twoFactorCode && twoFactorCode.length > 0 ) {
		var soapDoc = AjxSoapDoc.create("ResetTwoFactorCodeRequest", "urn:zimbraOffline");
		soapDoc.setMethodAttribute("id", this.acct.id);
		soapDoc.set("twoFactorCode", twoFactorCode);

		appCtxt.getAppController().sendRequest({
			soapDoc:soapDoc,
			asyncMode:true,
			noBusyOverlay:true,
			callback: new AjxCallback(this, this._handleTwoFactorCodeUpdateResult),
			accountName:this.name
		});
	}
};

/**
 *  Updates twofactor code for specified account
 *
 */
ZmTwoFactorCodeUpdateDialog.prototype._handleTwoFactorCodeUpdateResult =
function(result) {
    var resp = result.getResponse();
    resp = resp.ResetTwoFactorCodeResponse;
    if (resp && resp.status == "success") {
        this.popdown();
        var msg = AjxMessageFormat.format(ZmMsg.offlineTwoFactorCodeUpdateSuccess, this.acct.name);
        appCtxt.setStatusMsg(msg, ZmStatusView.LEVEL_INFO);
    } else {
        appCtxt.setStatusMsg(ZmMsg.offlineTwoFactorCodeUpdateFailure, ZmStatusView.LEVEL_WARNING);
        this._nameField.value = "";
        this._toggleOKButton(false);
    }
};

ZmTwoFactorCodeUpdateDialog.prototype._enterListener =
function(ev) {
	var twoFactorCode = AjxStringUtil.trim(this._nameField.value);
    if (twoFactorCode && twoFactorCode.length > 0 ) {
	    this._okButtonListener();
	}
};


ZmTwoFactorCodeUpdateDialog.prototype._handleKeyUp =
function(ev) {
	var key = DwtKeyEvent.getCharCode(ev);
	if (key == 9) {
		return;
	}
	var el = DwtUiEvent.getTarget(ev);
	var val = el && el.value;
	var dlgEl  = el && el._dlgEl && DwtControl.ALL_BY_ID[el._dlgEl];
	dlgEl._toggleOKButton(val.length > 0);
};


ZmTwoFactorCodeUpdateDialog.prototype._toggleOKButton =
function(enable) {
	var okBtn = this.getButton(DwtDialog.OK_BUTTON);
	okBtn.setEnabled(enable);
};

ZmTwoFactorCodeUpdateDialog.prototype._handleContextMenuPaste =
function() {
	var inputEl = this._nameField;
	var val = inputEl.value;
	var dlgEl = inputEl && inputEl._dlgEl && DwtControl.ALL_BY_ID[inputEl._dlgEl];
	dlgEl._toggleOKButton(val.length > 0);
};