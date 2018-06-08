/*
 * 
 */

/**
 * @overview
 */

/**
 * Creates a screen-covering background with a password lock dialog in the middle.
 * @class
 * This class represents a password lock dialog.
 * 
 * @param	{DwtControl}	parent		the parent
 * @param	{String}	className		the class name
 * 
 * @extends		ZmDialog
 */
ZmPasswordLockDialog = function(parent, className) {
	className = className || "ZmPasswordLockDialogContainer";
	var submitBtnDesc = new DwtDialog_ButtonDescriptor(Dwt.getNextId(), ZmMsg.submit, DwtDialog.ALIGN_RIGHT, new AjxCallback(this, this._handleSubmit));
	ZmDialog.call(this, {
		parent:         parent,
		className:      className,
		title:          ZmMsg.passwordLock,
		id:             "PasswordLockDialog",
		noDrag:         true,
		zIndex:         10000000,
		standardButtons:DwtDialog.NO_BUTTONS,
		extraButtons:   [submitBtnDesc]});

	this._setNameField(this._nameFieldId);
	this._createControls();
};

ZmPasswordLockDialog.prototype = new ZmDialog;
ZmPasswordLockDialog.prototype.constructor = ZmPasswordLockDialog;

ZmPasswordLockDialog.prototype.toString =
function() {
	return "ZmPasswordLockDialog";
};

/**
 * Pops-up the dialog.
 */
ZmPasswordLockDialog.prototype.popup =
function() {
	/* We will add account info each time the dialog is popped up
	 * reason being the account used last time might have got deleted.
	 */
	var visibleAccounts = appCtxt.accountList.visibleAccounts;
	for (var i = 0; i < visibleAccounts.length; i++) {
		var acct = visibleAccounts[i];
		if (acct.type == ZmAccount.TYPE_ZIMBRA && !acct.isMain) {
			this._acct = acct;
			break;
		}
	}
	if (this._acct) {
		this._nameField.value = "";
		Dwt.setVisible(this._errorField, false);
		this._toggleSubmitButton(false);

		var desc  = Dwt.byId(this._htmlElId + "_desc");
		if (desc) {
			//The account name info in the dialog should be bold.
			desc.innerHTML =  AjxMessageFormat.format(ZmMsg.passwordLockAccountMsg, this._acct.name);
		}

		this._mode = DwtBaseDialog.MODELESS;
		ZmDialog.prototype.popup.call(this);
	}
};

ZmPasswordLockDialog.prototype._createControls =
function() {
	//Remove the "move" cursor as we are disabling dnd operation on the dialog
	var title  = Dwt.byId(this._htmlElId + "_title");
	if (title) {
		title.style.cursor = "default";
	}

	//Add the css class for aligning dialog to the center.
	var dlgOuterContainer  = Dwt.byId(this._htmlElId + "_outerContainer");
	if (dlgOuterContainer) {
		dlgOuterContainer.className += " ZmPasswordLockDialog";
	}

	this._nameField._dlgEl = this._htmlElId;
	Dwt.setHandler(this._nameField, DwtEvent.ONKEYUP, this._handleKeyUp);
	this._errorField = Dwt.byId(this._htmlElId + "_error");
};


ZmPasswordLockDialog.prototype._contentHtml =
function() {
	this._nameFieldId = this._htmlElId + "_name";
	return AjxTemplate.expand("share.Dialogs#ZmPasswordLockDialog", {id:this._htmlElId});
};


ZmPasswordLockDialog.prototype._handleSubmit =
function() {
	var pwd = AjxStringUtil.trim(this._nameField.value);
	if (pwd && pwd.length > 0) {
		appCtxt.getAppController().sendRequest({
			jsonObj: {
				OfflineVerifyPasswordRequest: {
					_jsns: "urn:zimbraOffline",
					id: this._acct.id,
					password: pwd
				}
			},
			asyncMode:true,
			noBusyOverlay:true,
			callback: new AjxCallback(this, this._handleVerifyPasswordResult),
			accountName:this.name
		});
	}
};


/**
 *  Updates password for specified account
 *
 */

ZmPasswordLockDialog.prototype._handleVerifyPasswordResult =
function(result) {
	var resp = result.getResponse();
	if (resp) {
		resp = resp.OfflineVerifyPasswordResponse;
		if (resp && resp.status == "success") {
			this.popdown();
			appCtxt.setStatusMsg(ZmMsg.passwordLockVerificationSuccess, ZmStatusView.LEVEL_INFO);
			return;
		}
	}

	this._nameField.value = "";
	Dwt.setVisible(this._errorField, "block");
	this._toggleSubmitButton(false);
};


ZmPasswordLockDialog.prototype.handleKeyAction =
function(actionCode, ev) {
	switch (actionCode) {
		case DwtKeyMap.ENTER:
			var pwd = AjxStringUtil.trim(this._nameField.value);
			if (pwd && pwd.length > 0 ) {
				this._handleSubmit();
			}
			return true;

		case DwtKeyMap.CANCEL:
			return true;

		default:
			return false;
	}
};

ZmPasswordLockDialog.prototype._handleKeyUp =
function(ev) {
	var key = DwtKeyEvent.getCharCode(ev);
	if (key == DwtKeyEvent.KEY_TAB) {
		return;
	}
	var el = DwtUiEvent.getTarget(ev);
	var val = el && el.value;
	var dlgEl  = el && el._dlgEl && DwtControl.ALL_BY_ID[el._dlgEl];
	dlgEl._toggleSubmitButton(val.length > 0);
};


ZmPasswordLockDialog.prototype._toggleSubmitButton =
function(enable) {
	var submitBtn = this.getButton(this._buttonList[0]);
	submitBtn.setEnabled(enable);
	if (enable) {
		Dwt.setVisible(this._errorField, false);
	}
};
