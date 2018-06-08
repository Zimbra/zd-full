/*
 * 
 */
/**
 * Creates the auto archive preferences page.
 * @class
 * This class represents the auto archiving page.
 * 
 * @param {DwtControl}	parent			the containing widget
 * @param {Object}	section			the page
 * @param {ZmPrefController}	controller		the prefs controller
 * 
 * @extends	ZmPreferencesPage
 * 
 * @private
 */
ZmAutoArchivePage = function(parent, section, controller) {
	ZmPreferencesPage.apply(this, arguments);
};

ZmAutoArchivePage.prototype = new ZmPreferencesPage;
ZmAutoArchivePage.prototype.constructor = ZmAutoArchivePage;

ZmAutoArchivePage.prototype.toString =
	function () {
	return "ZmAutoArchivePage";
};

ZmAutoArchivePage.prototype.showMe =
	function(){
	ZmPreferencesPage.prototype.showMe.call(this);
	this._getLastAutoArchiveInfo();
};

ZmAutoArchivePage.prototype._createControls =
	function() {
	ZmPreferencesPage.prototype._createControls.apply(this, arguments);
	var cbox = this.getFormObject(ZmSetting.AUTO_ARCHIVE_ENABLED);
	if (cbox) {
		this._handleEnableAutoArchivePref(cbox);
	}
};

ZmAutoArchivePage.prototype.reset =
	function(useDefaults) {
	ZmPreferencesPage.prototype.reset.apply(this, arguments);

	var cbox = this.getFormObject(ZmSetting.AUTO_ARCHIVE_ENABLED);
	if (cbox) {
		this._handleEnableAutoArchivePref(cbox);
	}
};

ZmAutoArchivePage.prototype._setupCheckbox =
	function(id, setup, value) {
	var cbox = ZmPreferencesPage.prototype._setupCheckbox.apply(this, arguments);
	if (id == ZmSetting.AUTO_ARCHIVE_ENABLED)
	{
		cbox.addSelectionListener(new AjxListener(this, this._handleEnableAutoArchivePref, [cbox, id]));
	}
	return cbox;
};

ZmAutoArchivePage.prototype._handleEnableAutoArchivePref =
	function(cbox, id, evt) {
	var enabled = cbox.isSelected();

	var days = this.getFormObject(ZmSetting.AUTO_ARCHIVE_AGE);
	var freq = this.getFormObject(ZmSetting.AUTO_ARCHIVE_FREQ);

	days.setEnabled(enabled);
	if (enabled) {
		appCtxt.getKeyboardMgr().grabFocus(days);
	}
	freq.setEnabled(enabled);
};

ZmAutoArchivePage.prototype._getLastAutoArchiveInfo =
	function() {
	var jsonObj = { GetLastAutoArchiveInfoRequest : { _jsns:"urn:zimbraOffline"}};
	var params = {
			jsonObj: {
				GetLastAutoArchiveInfoRequest: {
					_jsns: "urn:zimbraOffline"
				}
			},
			asyncMode: true,
			callback: new AjxCallback(this, this._handleGetLastAutoArchiveInfoResponse)
	};
	appCtxt.getAppController().sendRequest(params);
};

ZmAutoArchivePage.prototype._handleGetLastAutoArchiveInfoResponse =
	function(response) {
	var getLastArchiveResponse = response._data.GetLastAutoArchiveInfoResponse;
	var timeInMillis = getLastArchiveResponse.offlineLastAutoArchive;
	if (timeInMillis == "0") {
		return;
	}
	var timestamp = AjxDateUtil.computeDateTimeString(new Date(timeInMillis));
	var lastAutoArchiveSection = document.getElementById("lastAutoArchiveSection");
	lastAutoArchiveSection.style.display = "inline-block";
	var lastAutoArchiveObj = document.getElementById('lastAutoArchiveInfo');
	lastAutoArchiveObj.innerHTML = AjxMessageFormat.format(ZmMsg.autoArchiveLastRun, timestamp);
};

ZmAutoArchivePage.prototype.getPreSaveCallback = function() {
	return new AjxCallback(this, this._preSaveAction, []);
};

ZmAutoArchivePage.prototype._preSaveAction =
	function(continueCallback, batchCommand) {
	var success = true;

	var chbox = this.getFormObject(ZmSetting.AUTO_ARCHIVE_ENABLED);
	var daysObj = this.getFormObject(ZmSetting.AUTO_ARCHIVE_AGE);

	var isAutoArchivingEnabled = chbox.isSelected();
	var days = daysObj.getValue();

	if (isAutoArchivingEnabled) {
		if (days == "" || days == 0 || !days.match(/^[\d]+$/)) {
			appCtxt.setStatusMsg(ZmMsg.invalidPrefValue, ZmStatusView.LEVEL_CRITICAL);
			continueCallback.run(false);
			return;
		}
	}

	if (isAutoArchivingEnabled && appCtxt.accountList.getZcsAccountCount() > 1) {
		success = false;

		var msgDialog = appCtxt.getMsgDialog();
		msgDialog.setMessage(ZmMsg.autoArchiveMultipleAccountsFailure, DwtMessageDialog.WARNING_STYLE);
		msgDialog.popup();
	}
	continueCallback.run(success);
};
