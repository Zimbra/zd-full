/*
 * 
 */

/**
 * @overview
 * This file defines the settings class.
 */

/**
 * Creates a collection of settings with default values. If no app context is given,
 * then this is a skeletal, non-live version of settings which can provide default
 * settings and parse SOAP settings.
 * @class
 * This class is a collection of various sorts of settings: config values, preferences,
 * and COS features. Each setting has an ID which can be used to retrieve it.
 *
 * @author Conrad Damon
 *
 * @param {Boolean}	noInit	if <code>true</code>, skip initialization
 *
 * @extends		ZmModel
 */
ZmSettings = function(noInit) {

	ZmModel.call(this, ZmEvent.S_SETTINGS);

	this._settings = {};	// settings by ID
	this._nameToId = {};	// map to get from server setting name to setting ID

	this.getInfoResponse = null; // Cached GetInfoResponse for lazy creation of identities, etc.
	this._handleImplicitChange = new AjxListener(this, this._implicitChangeListener);

	if (!noInit) {
        this.initialize();
	}
};

ZmSettings.prototype = new ZmModel;
ZmSettings.prototype.constructor = ZmSettings;

// Fix for bug: 53347
ZmSettings.BASE64_TO_NORMAL_RATIO = 1.34;

/**
 * Creates a new setting and adds it to the settings.
 *
 * @param {String}		id			the unique ID of the setting
 * @param {Hash}	params		a hash of parameters
 * @param {String}	params.name			the name of the pref or attr on the server
 * @param {constant}	params.type		config, pref, or COS
 * @param {constant}	params.dataType	string, int, or boolean (defaults to string)
 * @param {Object}	params.defaultValue	the default value
 */
ZmSettings.prototype.registerSetting =
function(id, params) {
	ZmSetting[id] = id;
	var setting = this._settings[id] = new ZmSetting(id, params);
	if (params.name) {
		this._nameToId[params.name] = id;
	}
	if (params.isImplicit) {
		setting.addChangeListener(this._handleImplicitChange);
	}
	return setting;
};

/**
 * Returns a string representation of the object.
 *
 * @return		{String}		a string representation of the object
 */
ZmSettings.prototype.toString =
function() {
	return "ZmSettings";
};

/**
 * Initializes the settings.
 *
 */
ZmSettings.prototype.initialize =
function() {
	this._initialize();
	this._setDefaults();
	this.userSettingsLoaded = false;

	// set listeners for settings
	var listener = new AjxListener(this, this._changeListener);
	if (!appCtxt.multiAccounts) {
		this.getSetting(ZmSetting.QUOTA_USED).addChangeListener(listener);
	}
	this.getSetting(ZmSetting.POLLING_INTERVAL).addChangeListener(listener);
	this.getSetting(ZmSetting.SKIN_NAME).addChangeListener(listener);
	this.getSetting(ZmSetting.LOCALE_NAME).addChangeListener(listener);
	this.getSetting(ZmSetting.SHORTCUTS).addChangeListener(listener);
	this.getSetting(ZmSetting.CHILD_ACCTS_VISIBLE).addChangeListener(listener);
	this.getSetting(ZmSetting.ATTACHMENTS_BLOCKED).addChangeListener(listener);
	this.getSetting(ZmSetting.PASSWORD_LOCK_ENABLED).addChangeListener(listener);


	if (appCtxt.isOffline) {
		this.getSetting(ZmSetting.OFFLINE_NOTEBOOK_SYNC_ENABLED).addChangeListener(listener);
		this.getSetting(ZmSetting.OFFLINE_IS_MAILTO_HANDLER).addChangeListener(listener);
        this.getSetting(ZmSetting.OFFLINE_BACKUP_ACCOUNT_ID).addChangeListener(listener);
        this.getSetting(ZmSetting.OFFLINE_BACKUP_INTERVAL).addChangeListener(listener);
        this.getSetting(ZmSetting.OFFLINE_BACKUP_PATH).addChangeListener(listener);
        this.getSetting(ZmSetting.OFFLINE_BACKUP_KEEP).addChangeListener(listener);
        this.getSetting(ZmSetting.OFFLINE_UPDATE_NOTIFY).addChangeListener(listener);
	}
};

/**
 * Gets the value of the given setting.
 *
 * @param {String}	id		the ID of the setting
 * @param	{String}	key		the key
 * @return	{Object}	the value or <code>null</code> for none
 */
ZmSettings.prototype.get =
function(id, key) {
	return (id && this._settings[id]) ? this._settings[id].getValue(key) : null;
};

/**
 * Gets the setting.
 *
 * @param {String}	id		the ID of the setting
 * @return	{ZmSetting}	the setting
 */
ZmSettings.prototype.getSetting =
function(id) {
	return this._settings[id];
};

/**
 * Populates settings values.
 *
 * @param {Hash}	list		a hash of preference or attribute values
 */
ZmSettings.prototype.createFromJs =
function(list, setDefault, skipNotify, skipImplicit) {
    // default skipImplicit value is true
    skipImplicit = skipImplicit == null || skipImplicit; 

	for (var i in list) {
		var val = list[i];
		var setting = this._settings[this._nameToId[i]];
		if (setting) {
			if (setting.dataType == ZmSetting.D_HASH) {
				var pairs = val.split(",");
				var value = {};
				for (var j = 0; j < pairs.length; j++) {
					var fields = pairs[j].split(":");
					value[fields[0]] = fields[1];
				}
				val = value;
			}
			setting.setValue(val, null, setDefault, skipNotify, skipImplicit);
			if (ZmSetting.IS_IMPLICIT[setting.id]) {
				setting.origValue = setting.copyValue();
			}
		} else {
			DBG.println(AjxDebug.DBG3, "*** Unrecognized setting: " + i);
		}
	}
};

/**
 * Gets the setting that is associated with the given server-side setting, if any.
 *
 * @param {String}	name	the server-side setting name (for example, "zimbraFeatureContactsEnabled")
 * @return	{String}	the setting id
 */
ZmSettings.prototype.getSettingByName =
function(name) {
	return this._nameToId[name];
};

/**
 * Checks if the given ID was received from the server. Use this method
 * to determine whether this ID is supported by a ZCS server. Currently used by
 * ZDesktop since it can "talk" to both v5 and v6 ZCS.
 *
 * @param {String}	id	the setting ID
 * @return	{Boolean}	<code>true</code> if the attribute is supported
 */
ZmSettings.prototype.attrExists =
function(id) {
	var name = this.getSetting(id).name;
	return (this.getInfoResponse.prefs._attrs[name] ||
			this.getInfoResponse.attrs._attrs[name]);
};

/**
 * Retrieves the preferences, COS settings, and metadata for the current user.
 * All the data gets stored into the settings collection.
 *
 * @param {AjxCallback}	callback 			the callback to run after response is received
 * @param {AjxCallback}	errorCallback 	the callback to run error is received
 * @param {String}	accountName		the name of account to load settings for
 * @param {Object}	response			the pre-determined JSON response object
 * @param {ZmBatchCommand}	batchCommand		set if part of a batch request
 */
ZmSettings.prototype.loadUserSettings =
function(callback, errorCallback, accountName, response, batchCommand) {
	var args = [callback, accountName];

	if (batchCommand) {
		var soapDoc = AjxSoapDoc.create("GetInfoRequest", "urn:zimbraAccount");
		var respCallback = new AjxCallback(this, this._handleResponseLoadUserSettings, args);
		batchCommand.addNewRequestParams(soapDoc, respCallback);
	} else {
		var params = {
			soapDoc: (response ? null : AjxSoapDoc.create("GetInfoRequest", "urn:zimbraAccount")),
			accountName: accountName,
			asyncMode: true,
			callback: (new AjxCallback(this, this._handleResponseLoadUserSettings, args)),
			errorCallback: errorCallback,
			response: response
		};
		appCtxt.getAppController().sendRequest(params);
	}
};

/**
 * @private
 */
ZmSettings.prototype._handleResponseLoadUserSettings =
function(callback, accountName, result) {
    this.setUserSettings(result.getResponse().GetInfoResponse, accountName);
    this.userSettingsLoaded = true;
    if (callback) {
        callback.run(result);
    }
};

/**
 * Sets the user settings.
 *
 * @param {hash}    params
 * @param {object}  params.info             The GetInfoResponse object.
 * @param {string}  [params.accountName]    The name of the account.
 * @param {boolean} [params.setDefault]     Set default value
 * @param {boolean} [params.skipNotify]     Skip change notification
 * @param {boolean} [params.skipImplicit]   Skip implicit changes
 * @param {boolean} [params.preInit]        Only init base settings for startup
 */
ZmSettings.prototype.setUserSettings = function(params) {
    params = Dwt.getParams(arguments, ["info", "accountName", "setDefault", "skipNotify", "skipImplicit", "preInit"]);

    var info = this.getInfoResponse = params.info;
    var accountName = params.accountName;
    var setDefault = params.preInit ? false : params.setDefault;
    var skipNotify = params.preInit ? true : params.skipNotify;
    var skipImplicit = params.preInit ? true : params.skipImplicit;

    var settings = [
        ZmSetting.ADMIN_DELEGATED,          info.adminDelegated,
        ZmSetting.ATTACHMENT_SIZE_LIMIT,    this._base64toNormalSize(info.attrs._attrs.zimbraMtaMaxMessageSize), // Fix for bug: 53347
        ZmSetting.CHANGE_PASSWORD_URL,      info.changePasswordURL,
        ZmSetting.DOCUMENT_SIZE_LIMIT,       this._base64toNormalSize(info.attrs._attrs.zimbraFileUploadMaxSize), // Fix for bug: 53347
        ZmSetting.LAST_ACCESS,              info.accessed,
        ZmSetting.LICENSE_STATUS,           info.license && info.license.status,
        ZmSetting.PREVIOUS_SESSION,         info.prevSession,
        ZmSetting.PUBLIC_URL,               info.publicURL,
        ZmSetting.QUOTA_USED,               info.used,
        ZmSetting.RECENT_MESSAGES,          info.recent,
        ZmSetting.REST_URL,                 info.rest,
        ZmSetting.USERNAME,                 info.name,
		ZmSetting.EMAIL_VALIDATION_REGEX, 	info.zimbraMailAddressValidationRegex,
		ZmSetting.DISABLE_SENSITIVE_ZIMLETS_IN_MIXED_MODE, 	(info.domainSettings && info.domainSettings.zimbraZimletDataSensitiveInMixedModeDisabled ? info.domainSettings.zimbraZimletDataSensitiveInMixedModeDisabled : "FALSE"),
		ZmSetting.FORCE_DISABLE_PASSWORD_LOCK, info.disablePasswordLock
    ];
    for (var i = 0; i < settings.length; i += 2) {
        var value = settings[i+1];
        if (value != null) {
            this._settings[settings[i]].setValue(value, null, setDefault, skipNotify, skipImplicit);
        }
    }
    // features and other settings
    if (info.attrs && info.attrs._attrs) {
        this.createFromJs(info.attrs._attrs, setDefault, skipNotify, skipImplicit);
    }

    // admin mail enabled setting takes precedence if admin delegated
    if (this.get(ZmSetting.ADMIN_DELEGATED) && !this.get(ZmSetting.ADMIN_MAIL_ENABLED)) {
        this.getSetting(ZmSetting.MAIL_ENABLED).setValue(false);
    }

    if (params.preInit) return;

    // preferences
    if (info.prefs && info.prefs._attrs) {
        this.createFromJs(info.prefs._attrs, setDefault, skipNotify, skipImplicit);
    }

    // accounts
	if (!accountName) {
		// NOTE: only the main account can have children
		appCtxt.accountList.createAccounts(this, info);

        // ZD_Next: isWebkit will be true if launched in Node-webkit chromium shell
		if (appCtxt.isOffline) {
			if (window.isNodeWebkit) {
				var setting = this._settings[ZmSetting.OFFLINE_SUPPORTS_MAILTO];
				if (setting) {
					setting.setValue(true, null, setDefault, skipNotify, skipImplicit);
				}
				setting = this._settings[ZmSetting.OFFLINE_SUPPORTS_DOCK_UPDATE];
				if (setting) {
					setting.setValue(true, null, setDefault, skipNotify, skipImplicit);
				}
			}

			// bug #45804 - sharing always enabled for offline
			appCtxt.set(ZmSetting.SHARING_ENABLED, true, null, setDefault, skipNotify);
		}
	}

	if (appCtxt.isOffline && window.isNodeWebkit) {
		// for node webkit, we need to get language code from local pref, instead of getting from zimbra server
		// to make sure it is available in offline mode also
		var localeId = NodeWebkitPrefs.getPreference('LOCALE_NAME');

		if(localeId) {
            NodeWebkitUtils.updateSpellCheck(localeId);
			appCtxt.set(ZmSetting.LOCALE_NAME, localeId, null, setDefault, skipNotify);
		}

        // for node webkit, check ZD is set as default mail app
        var isDefault = NodeWebkitMailto.isRegistered();
        appCtxt.set(ZmSetting.OFFLINE_IS_MAILTO_HANDLER, isDefault, null, setDefault, skipNotify);
	}

	// handle settings whose values may depend on other settings
	var setting = this._settings[ZmSetting.REPLY_TO_ADDRESS];
	if (setting) {
		setting.defaultValue = this.get(ZmSetting.USERNAME);
	}
	if (!this.get(ZmSetting.SEARCH_ENABLED)) {
		setting = this._settings[ZmSetting.BROWSE_ENABLED];
		if (setting) {
			setting.setValue(false, null, setDefault, skipNotify, skipImplicit);
		}
	}
	if (this.get(ZmSetting.FORCE_CAL_OFF)) {
		setting = this._settings[ZmSetting.CALENDAR_ENABLED];
		if (setting) {
			setting.setValue(false, null, setDefault, skipNotify, skipImplicit);
		}
	}
	if (!this.get(ZmSetting.OPTIONS_ENABLED)) {
		setting = this._settings[ZmSetting.FILTERS_ENABLED];
		if (setting) {
			setting.setValue(false, null, setDefault, skipNotify, skipImplicit);
		}
	}

	// load zimlets *only* for the main account
	if (!accountName) {
		if (info.zimlets && info.zimlets.zimlet) {
            if (this.get(ZmSetting.ZIMLETS_SYNCHRONOUS)) {
                var action = new AjxTimedAction(this, this._beginLoadZimlets, [info.zimlets.zimlet, info.props.prop, true]);
                AjxTimedAction.scheduleAction(action, 0);
            } else {
                var listener = new AjxListener(this, this._beginLoadZimlets, [info.zimlets.zimlet, info.props.prop, false]);
                appCtxt.getAppController().addListener(ZmAppEvent.POST_STARTUP, listener);
            }
		} else {
			appCtxt.allZimletsLoaded();
		}
	}

	var value = appCtxt.get(ZmSetting.REPLY_INCLUDE_ORIG);
	if (value) {
		var list = ZmMailApp.INC_MAP[value];
		appCtxt.set(ZmSetting.REPLY_INCLUDE_WHAT, list[0], null, setDefault, skipNotify);
		appCtxt.set(ZmSetting.REPLY_USE_PREFIX, list[1], null, setDefault, skipNotify);
		appCtxt.set(ZmSetting.REPLY_INCLUDE_HEADERS, list[2], null, setDefault, skipNotify);
	}

	var value = appCtxt.get(ZmSetting.FORWARD_INCLUDE_ORIG);
	if (value) {
		var list = ZmMailApp.INC_MAP[value];
		appCtxt.set(ZmSetting.FORWARD_INCLUDE_WHAT, list[0], null, setDefault, skipNotify);
		appCtxt.set(ZmSetting.FORWARD_USE_PREFIX, list[1], null, setDefault, skipNotify);
		appCtxt.set(ZmSetting.FORWARD_INCLUDE_HEADERS, list[2], null, setDefault, skipNotify);
	}

    //Populate Sort Order Defaults
    var sortPref =  ZmSettings.DEFAULT_SORT_PREF;
    sortPref[ZmId.VIEW_CONVLIST]            = ZmSearch.DATE_DESC;
    sortPref[ZmId.VIEW_CONV]                = ZmSearch.DATE_DESC;
    sortPref[ZmId.VIEW_TRAD]                = ZmSearch.DATE_DESC;
    sortPref[ZmId.VIEW_CONTACT_SRC]         = ZmSearch.NAME_ASC;
    sortPref[ZmId.VIEW_CONTACT_TGT]         = ZmSearch.NAME_ASC;
    sortPref[ZmId.VIEW_CONTACT_SIMPLE]      = ZmSearch.NAME_ASC;
    sortPref[ZmId.VIEW_CAL]                 = ZmSearch.DATE_ASC;
    sortPref[ZmId.VIEW_TASKLIST]            = ZmSearch.DUE_DATE_ASC;
    sortPref[ZmId.VIEW_BRIEFCASE_DETAIL]    = ZmSearch.SUBJ_ASC;

    var sortOrderSetting = this._settings[ZmSetting.SORTING_PREF];
    if (sortOrderSetting) {

        //Populate empty sort pref's with defaultValues
        for(var pref in sortPref){
            if(!sortOrderSetting.getValue(pref)){
                sortOrderSetting.setValue(sortPref[pref], pref, false, true);
            }
        }

        // Disable SORTING PREF for MAIL, Overwrite Mail Sort Settings to Defaults
        // We do this becoz we fetch initial mail response inline, have no control over sortBy
        sortOrderSetting.setValue(ZmSearch.DATE_DESC, ZmId.VIEW_CONVLIST, false, true);
        sortOrderSetting.setValue(ZmSearch.DATE_DESC, ZmId.VIEW_CONV, false, true);
        sortOrderSetting.setValue(ZmSearch.DATE_DESC, ZmId.VIEW_TRAD, false, true);

        //Explicitly Set defaultValue
        sortOrderSetting.defaultValue = AjxUtil.hashCopy(sortPref);
    }

};

ZmSettings.prototype._beginLoadZimlets =
function(zimlet, prop, sync) {
    var zimletsCallback = new AjxCallback(this, this._loadZimletPackage, [zimlet, prop, sync]);
    AjxDispatcher.require(["Startup2"], false, zimletsCallback);
};

ZmSettings.prototype._loadZimletPackage = 
function(zimlet, prop, sync) {
	var zimletsCallback = new AjxCallback(this, this._loadZimlets, [zimlet, prop, sync]);
	AjxDispatcher.require("Zimlet", false, zimletsCallback);
}
/**
 * @private
 */
ZmSettings.prototype._loadZimlets =
function(allZimlets, props, sync) {

	allZimlets = allZimlets || [];
	this.registerSetting("ZIMLETS",		{type:ZmSetting.T_CONFIG, defaultValue:allZimlets, isGlobal:true});
	this.registerSetting("USER_PROPS",	{type:ZmSetting.T_CONFIG, defaultValue:props});

	var zimlets = this._getCheckedZimlets(allZimlets);

	DBG.println(AjxDebug.DBG1, "Zimlets - Loading " + zimlets.length + " Zimlets");
	var zimletMgr = appCtxt.getZimletMgr();
	zimletMgr.loadZimlets(zimlets, props, null, null, sync);

	if (zimlets && zimlets.length) {
		var activeApp = appCtxt.getCurrentApp();
		if (activeApp) {
			var overview;
			if (appCtxt.multiAccounts) {
				var containerId = activeApp.getOverviewContainer().containerId;
				var zimletLabel = ZmOrganizer.LABEL[ZmOrganizer.ZIMLET];
				var overviewId = [containerId, zimletLabel].join("_");
				overview = appCtxt.getOverviewController().getOverview(overviewId);
			} else {
				overview = activeApp.getOverview();
			}
		}

		// update overview tree
		if (overview) {
			overview.setTreeView(ZmOrganizer.ZIMLET);

			// HACK: for multi-account, hide the zimlet section if no panel zimlets
			if (appCtxt.multiAccounts && zimletMgr.getPanelZimlets().length == 0) {
				activeApp.getOverviewContainer().removeZimletSection();
			}
		}

		// create global portlets
		if (appCtxt.get(ZmSetting.PORTAL_ENABLED)) {
			var portletMgr = appCtxt.getApp(ZmApp.PORTAL).getPortletMgr();
			var portletIds = portletMgr.createPortlets(true);
		}
	}
};

/**
 * Filters a list of zimlets, returned ones that are checked.
 *
 * @param zimlets			[array]		list of zimlet objects
 *
 * @private
 */
ZmSettings.prototype._getCheckedZimlets =
function(allZimlets) {

	var zimlets = [];
	for (var i = 0; i < allZimlets.length; i++) {
		var zimletObj = allZimlets[i];
		if (zimletObj.zimletContext[0].presence != "disabled") {
			zimlets.push(zimletObj);
		}
	}

	return zimlets;
};

/**
 * Loads the preference data.
 *
 * @param	{AjxCallback}	callback		the callback
 */
ZmSettings.prototype.loadPreferenceData =
function(callback) {
	// force main account (in case multi-account) since locale/skins are global
	var command = new ZmBatchCommand(null, appCtxt.accountList.mainAccount.name);

	var skinDoc = AjxSoapDoc.create("GetAvailableSkinsRequest", "urn:zimbraAccount");
	var skinCallback = new AjxCallback(this, this._handleResponseLoadAvailableSkins);
	command.addNewRequestParams(skinDoc, skinCallback);

	var localeDoc = AjxSoapDoc.create("GetAvailableLocalesRequest", "urn:zimbraAccount");
	var localeCallback = new AjxCallback(this, this._handleResponseGetAllLocales);
	command.addNewRequestParams(localeDoc, localeCallback);

	var csvFormatsDoc = AjxSoapDoc.create("GetAvailableCsvFormatsRequest", "urn:zimbraAccount");
	var csvFormatsCallback = new AjxCallback(this, this._handleResponseGetAvailableCsvFormats);
	command.addNewRequestParams(csvFormatsDoc, csvFormatsCallback);

	command.run(callback);
};

/**
 * @private
 */
ZmSettings.prototype._handleResponseLoadAvailableSkins =
function(result) {
	var resp = result.getResponse().GetAvailableSkinsResponse;
	var skins = resp.skin;
	if (skins && skins.length) {
		var setting = appCtxt.accountList.mainAccount.settings.getSetting(ZmSetting.AVAILABLE_SKINS);
		for (var i = 0; i < skins.length; i++) {
			// always save available skins on the main account (in case multi-account)
			setting.setValue(skins[i].name);
		}
	}
};

/**
 * @private
 */
ZmSettings.prototype._handleResponseGetAllLocales =
function(response) {
	var locales = response._data.GetAvailableLocalesResponse.locale;
	if (locales && locales.length) {
		for (var i = 0, count = locales.length; i < count; i++) {
			var locale = locales[i];
			// bug: 38038
			locale.id = locale.id.replace(/^in/,"id");
			ZmLocale.create(locale.id, locale.name);
		}
		this.getSetting(ZmSetting.LOCALE_CHANGE_ENABLED).setValue(ZmLocale.hasChoices());
	}
};

/**
 * @private
 */
ZmSettings.prototype._handleResponseGetAvailableCsvFormats =
function(result){
	var formats = result.getResponse().GetAvailableCsvFormatsResponse.csv;
	if (formats && formats.length) {
		var setting = appCtxt.accountList.mainAccount.settings.getSetting(ZmSetting.AVAILABLE_CSVFORMATS);
		for (var i = 0; i < formats.length; i++) {
			setting.setValue(formats[i].name);
		}
	}
};

/**
 * Saves one or more settings.
 *
 * @param {Array}		list			a list of {ZmSetting} objects
 * @param {AjxCallback}	callback		the callback to run after response is received
 * @param {ZmBatchCommand}	batchCommand	the batch command
 * @param {ZmZimbraAccount}	account		the account to save under
 * @param {boolean}			isImplicit	if true, we are saving implicit settings
 */
ZmSettings.prototype.save =
function(list, callback, batchCommand, account, isImplicit) {
	if (!(list && list.length)) { return; }

	var acct = account || appCtxt.getActiveAccount();
	var soapDoc = AjxSoapDoc.create("ModifyPrefsRequest", "urn:zimbraAccount");
	var gotOne = false;
	var metaData = [], done = {}, setting;
	for (var i = 0; i < list.length; i++) {
		setting = list[i];
        if (done[setting.id]) { continue; }
		if (setting.type == ZmSetting.T_METADATA) {
			metaData.push(setting);
			// update the local meta data
			acct.metaData.update(setting.section, setting.name, setting.getValue());
			continue;
		} else if (setting.type != ZmSetting.T_PREF) {
			DBG.println(AjxDebug.DBG1, "*** Attempt to modify non-pref: " + setting.id + " / " + setting.name);
			continue;
		}
		if (!setting.name) {
			DBG.println(AjxDebug.DBG2, "Modify internal pref: " + setting.id);
			continue;
		}
		if (setting.dataType == ZmSetting.D_LIST) {
			// LDAP supports multi-valued attrs, so don't serialize list
			var value = setting.getValue();
			if (value && value.length) {
				for (var j = 0; j < value.length; j++) {
					var node = soapDoc.set("pref", value[j]);
					node.setAttribute("name", setting.name);
				}
			} else {
				var node = soapDoc.set("pref", "");
				node.setAttribute("name", setting.name);
			}
		} else {
			var value = setting.getValue(null, true);
			var node = soapDoc.set("pref", value);
			node.setAttribute("name", setting.name);
		}

        done[setting.id] = true;
		gotOne = true;
	}

    // bug: 50668 if the setting is implicit and global, use main Account
    if(appCtxt.isOffline && ZmSetting.IS_IMPLICIT[setting.id] && ZmSetting.IS_GLOBAL[setting.id]) {
        acct = appCtxt.accountList.mainAccount;
    }

	if (metaData.length > 0) {
		var metaDataCallback = new AjxCallback(this, this._handleResponseSaveMetaData, [metaData]);
		var sections = [ZmSetting.M_IMPLICIT, ZmSetting.M_OFFLINE];
		acct.metaData.save(sections, metaDataCallback);
	}

	if (gotOne) {
		var respCallback;
		if (callback || batchCommand) {
			respCallback = new AjxCallback(this, this._handleResponseSave, [list, callback]);
		}
		if (batchCommand) {
			batchCommand.addNewRequestParams(soapDoc, respCallback);
		} else {
			appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:respCallback,
			 										accountName:acct.name, noBusyOverlay:isImplicit});
		}
	}
};

/**
 * @private
 */
ZmSettings.prototype._handleResponseSaveMetaData =
function(list, result) {
	for (var i = 0; i < list.length; i++) {
		var setting = list[i];
		if (!ZmSetting.IS_IMPLICIT[setting.id]) {
			setting.origValue = setting.copyValue();
			setting._notify(ZmEvent.E_MODIFY);
		}
	}
};

/**
 * @private
 */
ZmSettings.prototype._handleResponseSave =
function(list, callback, result) {
	var resp = result.getResponse();
	if (resp.ModifyPrefsResponse != null) {
		// notify each changed setting's listeners
		for (var i = 0; i < list.length; i++) {
			var setting = list[i];
			setting.origValue = setting.copyValue();
			if (!ZmSetting.IS_IMPLICIT[setting.id]) {
				setting._notify(ZmEvent.E_MODIFY);
			}
		}
		// notify any listeners on the settings as a whole
		this._notify(ZmEvent.E_MODIFY, {settings:list});
	}

	if (callback) {
		callback.run(result);
	}
};

ZmSettings.DEFAULT_SORT_PREF = {};

/**
 * Set defaults which are determined dynamically (which can't be set in static code).
 *
 * @private
 */
ZmSettings.prototype._setDefaults =
function() {

	var value = AjxUtil.formatUrl({host:location.hostname, path:"/service/soap/", qsReset:true});
	this._settings[ZmSetting.CSFE_SERVER_URI].setValue(value, null, false, true);

	// CSFE_MSG_FETCHER_URI
	value = AjxUtil.formatUrl({host:location.hostname, path:"/service/home/~/", qsReset:true, qsArgs:{auth:"co"}});
	this._settings[ZmSetting.CSFE_MSG_FETCHER_URI].setValue(value, null, false, true);

	// CSFE_UPLOAD_URI
	value = AjxUtil.formatUrl({host:location.hostname, path:"/service/upload", qsReset:true, qsArgs:{lbfums:""}});
	this._settings[ZmSetting.CSFE_UPLOAD_URI].setValue(value, null, false, true);

	// CSFE_ATTACHMENT_UPLOAD_URI
	value = AjxUtil.formatUrl({host:location.hostname, path:"/service/upload", qsReset:true});
	this._settings[ZmSetting.CSFE_ATTACHMENT_UPLOAD_URI].setValue(value, null, false, true);

	// CSFE EXPORT URI
	value = AjxUtil.formatUrl({host:location.hostname, path:"/service/home/~/", qsReset:true, qsArgs:{auth:"co", id:"{0}", fmt:"csv"}});
	this._settings[ZmSetting.CSFE_EXPORT_URI].setValue(value, null, false, true);

	var h = location.hostname;
	var isDev = ((h.indexOf(".zimbra.com") != -1) || (window.appDevMode && (/\.local$/.test(h) || (!appCtxt.isOffline && h == "localhost"))));
	this._settings[ZmSetting.IS_DEV_SERVER].setValue(isDev);
	if (isDev || window.isScriptErrorOn) {
		this._settings[ZmSetting.SHOW_SCRIPT_ERRORS].setValue(true, null, false, true);
	}

	this.setReportScriptErrorsSettings(AjxException, ZmController.handleScriptError);

};

ZmSettings.prototype.persistImplicitSortPrefs =
function(id){
    return ZmSettings.DEFAULT_SORT_PREF[id];
};

/**
 * sets AjxException static attributes. This is extracted so it can be called from ZmNewwindow as well.
 * this is since the child window gets its own AjxException variable.
 *
 * @param AjxExceptionClassVar
 * @param handler
 */
ZmSettings.prototype.setReportScriptErrorsSettings =
function(AjxExceptionClassVar, handler) {
	// script error reporting
	var rse = AjxExceptionClassVar.reportScriptErrors = this._settings[ZmSetting.SHOW_SCRIPT_ERRORS].getValue();
	if (rse) {
		AjxExceptionClassVar.setScriptErrorHandler(handler);
	}

};

/**
 * Loads the standard settings and their default values.
 *
 * @private
 */
ZmSettings.prototype._initialize =
function() {
	// CONFIG SETTINGS
    this.registerSetting("ADMIN_DELEGATED",                 {type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("AC_TIMER_INTERVAL",				{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_INT, defaultValue:300});
	this.registerSetting("ASYNC_MODE",						{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("BRANCH",							{type:ZmSetting.T_CONFIG, defaultValue:"HELIX"});

	// next 3 are replaced during deployment
	this.registerSetting("CLIENT_DATETIME",					{type:ZmSetting.T_CONFIG, defaultValue:"@buildDateTime@"});
	this.registerSetting("CLIENT_RELEASE",					{type:ZmSetting.T_CONFIG, defaultValue:"@buildRelease@"});
	this.registerSetting("CLIENT_VERSION",					{type:ZmSetting.T_CONFIG, defaultValue:"@buildVersion@"});
	this.registerSetting("CONFIG_PATH",						{type:ZmSetting.T_CONFIG, defaultValue:appContextPath + "/js/zimbraMail/config"});
	this.registerSetting("CSFE_EXPORT_URI",					{type:ZmSetting.T_CONFIG});
	this.registerSetting("CSFE_MSG_FETCHER_URI",			{type:ZmSetting.T_CONFIG});
	this.registerSetting("CSFE_SERVER_URI",					{type:ZmSetting.T_CONFIG});
	this.registerSetting("CSFE_UPLOAD_URI",					{type:ZmSetting.T_CONFIG});
	this.registerSetting("CSFE_ATTACHMENT_UPLOAD_URI",		{type:ZmSetting.T_CONFIG});
	this.registerSetting("DEV",								{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("FORCE_CAL_OFF",					{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("HELP_URI",						{type:ZmSetting.T_CONFIG, defaultValue:appContextPath + ZmMsg.helpURI});
	this.registerSetting("HTTP_PORT",						{type:ZmSetting.T_CONFIG, defaultValue:ZmSetting.HTTP_DEFAULT_PORT});
	this.registerSetting("HTTPS_PORT",						{type:ZmSetting.T_CONFIG, defaultValue:ZmSetting.HTTPS_DEFAULT_PORT});
	this.registerSetting("INSTANT_NOTIFY_INTERVAL",			{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_INT, defaultValue:500}); // milliseconds
	this.registerSetting("INSTANT_NOTIFY_TIMEOUT",			{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_INT, defaultValue:300}); // seconds
	this.registerSetting("IS_DEV_SERVER",					{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("LOG_REQUEST",						{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("LOGO_URI",						{type:ZmSetting.T_CONFIG, defaultValue:null});
	this.registerSetting("PROTOCOL_MODE",					{type:ZmSetting.T_CONFIG, defaultValue:ZmSetting.PROTO_HTTP});
	this.registerSetting("SERVER_VERSION",					{type:ZmSetting.T_CONFIG});
	this.registerSetting("SHOW_SCRIPT_ERRORS",				{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("TIMEOUT",							{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_INT, defaultValue:30}); // seconds
	this.registerSetting("USE_XML",							{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});

	// DOMAIN SETTINGS
	this.registerSetting("CHANGE_PASSWORD_URL",				{type:ZmSetting.T_CONFIG});
	this.registerSetting("PUBLIC_URL",						{type:ZmSetting.T_CONFIG});
	this.registerSetting("DISABLE_SENSITIVE_ZIMLETS_IN_MIXED_MODE",		{type:ZmSetting.T_CONFIG});
	this.registerSetting("FORCE_DISABLE_PASSWORD_LOCK",		{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});

	// COS SETTINGS - APPS
	this.registerSetting("ASSISTANT_ENABLED",				{name:"zimbraFeatureZimbraAssistantEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("BRIEFCASE_ENABLED",				{name:"zimbraFeatureBriefcasesEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("ATTACHMENTS_BLOCKED",				{name:"zimbraAttachmentsBlocked", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("CALENDAR_ENABLED",				{name:"zimbraFeatureCalendarEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("CALENDAR_UPSELL_ENABLED",			{name:"zimbraFeatureCalendarUpsellEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("CALENDAR_UPSELL_URL",				{name:"zimbraFeatureCalendarUpsellURL", type:ZmSetting.T_COS});
	this.registerSetting("CONTACTS_ENABLED",				{name:"zimbraFeatureContactsEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("CONTACTS_UPSELL_ENABLED",			{name:"zimbraFeatureContactsUpsellEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("CONTACTS_UPSELL_URL",				{name:"zimbraFeatureContactsUpsellURL", type:ZmSetting.T_COS});
	this.registerSetting("IM_ENABLED",						{name:"zimbraFeatureIMEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("IMPORT_ENABLED",					{name:"zimbraFeatureImportFolderEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("EXPORT_ENABLED",					{name:"zimbraFeatureExportFolderEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
    this.registerSetting("MAIL_ENABLED",					{name:"zimbraFeatureMailEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
    this.registerSetting("ADMIN_MAIL_ENABLED",				{name:"zimbraFeatureAdminMailEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("MAIL_UPSELL_ENABLED",				{name:"zimbraFeatureMailUpsellEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("MAIL_UPSELL_URL",					{name:"zimbraFeatureMailUpsellURL", type:ZmSetting.T_COS});
	this.registerSetting("NOTEBOOK_ENABLED",				{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("OPTIONS_ENABLED",					{name:"zimbraFeatureOptionsEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("PORTAL_ENABLED",					{name:"zimbraFeaturePortalEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("TASKS_ENABLED",					{name:"zimbraFeatureTasksEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("VOICE_ENABLED",					{name:"zimbraFeatureVoiceEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("VOICE_UPSELL_ENABLED",			{name:"zimbraFeatureVoiceUpsellEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("VOICE_UPSELL_URL",				{name:"zimbraFeatureVoiceUpsellURL", type:ZmSetting.T_COS});
	this.registerSetting("MAIL_SEND_LATER_ENABLED",			{name:"zimbraFeatureMailSendLaterEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	// COS SETTINGS
	this.registerSetting("AVAILABLE_SKINS",					{type:ZmSetting.T_COS, dataType:ZmSetting.D_LIST, isGlobal:true});
	this.registerSetting("AVAILABLE_CSVFORMATS",			{type:ZmSetting.T_COS, dataType:ZmSetting.D_LIST, isGlobal:true});
	this.registerSetting("BROWSE_ENABLED",					{name:"zimbraFeatureAdvancedSearchEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("CHANGE_PASSWORD_ENABLED",			{name:"zimbraFeatureChangePasswordEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("DISPLAY_NAME",					{name:"displayName", type:ZmSetting.T_COS});
	this.registerSetting("DUMPSTER_ENABLED",				{name:"zimbraDumpsterEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("ERROR_REPORT_URL",				{name:"zimbraErrorReportUrl", type:ZmSetting.T_COS, dataType:ZmSetting.D_STRING});
	this.registerSetting("FLAGGING_ENABLED",				{name:"zimbraFeatureFlaggingEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("FOLDERS_EXPANDED",				{name:"zimbraPrefFoldersExpanded", type:ZmSetting.T_METADATA, dataType: ZmSetting.D_HASH, isImplicit:true, section:ZmSetting.M_IMPLICIT});
	this.registerSetting("FOLDER_TREE_OPEN",				{name:"zimbraPrefFolderTreeOpen", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:true, isImplicit:true});
	this.registerSetting("GAL_AUTOCOMPLETE_ENABLED",		{name:"zimbraFeatureGalAutoCompleteEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN,	defaultValue:false});
	this.registerSetting("GAL_ENABLED",						{name:"zimbraFeatureGalEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN,	defaultValue:true});
	this.registerSetting("GROUP_CALENDAR_ENABLED",			{name:"zimbraFeatureGroupCalendarEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("HTML_COMPOSE_ENABLED",			{name:"zimbraFeatureHtmlComposeEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("IDLE_SESSION_TIMEOUT",			{name:"zimbraMailIdleSessionTimeout", type:ZmSetting.T_COS, dataType:ZmSetting.D_LDAP_TIME, defaultValue:0});
	this.registerSetting("IMAP_ACCOUNTS_ENABLED",			{name:"zimbraFeatureImapDataSourceEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("INSTANT_NOTIFY",					{name:"zimbraFeatureInstantNotify", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("LOCALE_CHANGE_ENABLED",			{name:"zimbraFeatureLocaleChangeEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("LOCALES",							{type:ZmSetting.T_COS, dataType:ZmSetting.D_LIST});
	this.registerSetting("LOGIN_URL",						{name:"zimbraWebClientLoginURL", type:ZmSetting.T_COS});
	this.registerSetting("LOGOUT_URL",						{name:"zimbraWebClientLogoutURL", type:ZmSetting.T_COS});
	this.registerSetting("MIN_POLLING_INTERVAL",			{name:"zimbraMailMinPollingInterval", type:ZmSetting.T_COS, dataType:ZmSetting.D_LDAP_TIME, defaultValue:120});
	this.registerSetting("MOBILE_SYNC_ENABLED",				{name:"zimbraFeatureMobileSyncEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("MOBILE_POLICY_ENABLED",			{name:"zimbraFeatureMobilePolicyEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("PEOPLE_SEARCH_ENABLED",			{name:"zimbraFeaturePeopleSearchEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("POP_ACCOUNTS_ENABLED",			{name:"zimbraFeaturePop3DataSourceEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("PORTAL_NAME",						{name:"zimbraPortalName", type:ZmSetting.T_COS, defaultValue:"example"});
	this.registerSetting("PWD_MAX_LENGTH",					{name:"zimbraPasswordMaxLength", type:ZmSetting.T_COS, dataType:ZmSetting.D_INT, defaultValue:64});
	this.registerSetting("PWD_MIN_LENGTH",					{name:"zimbraPasswordMinLength", type:ZmSetting.T_COS, dataType:ZmSetting.D_INT, defaultValue:6});
	this.registerSetting("QUOTA",							{name:"zimbraMailQuota", type:ZmSetting.T_COS, dataType:ZmSetting.D_INT, defaultValue:0});
	this.registerSetting("SAVED_SEARCHES_ENABLED",			{name:"zimbraFeatureSavedSearchesEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("SEARCH_TREE_OPEN",				{name:"zimbraPrefSearchTreeOpen", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:true, isImplicit:true});
	this.registerSetting("SHARING_ENABLED",					{name:"zimbraFeatureSharingEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("SHORTCUT_ALIASES_ENABLED",		{name:"zimbraFeatureShortcutAliasesEnabled", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("SHOW_OFFLINE_LINK",				{name:"zimbraWebClientShowOfflineLink", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("SIGNATURES_ENABLED",				{name:"zimbraFeatureSignaturesEnabled", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("SKIN_CHANGE_ENABLED",				{name:"zimbraFeatureSkinChangeEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
    this.registerSetting("SPAM_ENABLED",					{name:"zimbraFeatureAntispamEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("TAG_TREE_OPEN",					{name:"zimbraPrefTagTreeOpen", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:true, isImplicit:true});
	this.registerSetting("TAGGING_ENABLED",					{name:"zimbraFeatureTaggingEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("VIEW_ATTACHMENT_AS_HTML",			{name:"zimbraFeatureViewInHtmlEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("EXPAND_DL_ENABLED",				{name:"zimbraFeatureDistributionListExpandMembersEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});

	// user metadata (included with COS since the user can't change them)
	this.registerSetting("LICENSE_STATUS",					{type:ZmSetting.T_COS, defaultValue:ZmSetting.LICENSE_GOOD});
	this.registerSetting("QUOTA_USED",						{type:ZmSetting.T_COS, dataType:ZmSetting.D_INT});    
	this.registerSetting("USERID",							{name:"zimbraId", type:ZmSetting.T_COS});
	this.registerSetting("USERNAME",						{type:ZmSetting.T_COS});
	this.registerSetting("CN",								{name:"cn", type:ZmSetting.T_COS});
	this.registerSetting("LAST_ACCESS",						{type:ZmSetting.T_COS, dataType:ZmSetting.D_INT});
	this.registerSetting("PREVIOUS_SESSION",				{type:ZmSetting.T_COS, dataType:ZmSetting.D_INT});
	this.registerSetting("RECENT_MESSAGES",					{type:ZmSetting.T_COS, dataType:ZmSetting.D_INT});
	this.registerSetting("REST_URL",						{name:"rest" , type:ZmSetting.T_COS});
	this.registerSetting("IS_ADMIN",						{name:"zimbraIsAdminAccount", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue: false});
	this.registerSetting("IS_DELEGATED_ADMIN",				{name:"zimbraIsDelegatedAdminAccount", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue: false});
	this.registerSetting("ADMIN_REFERENCE",                 {name:"zimbraWebClientAdminReference", type:ZmSetting.T_COS, dataType:ZmSetting.D_STRING});
    this.registerSetting("ATTACHMENT_SIZE_LIMIT",           {type:ZmSetting.T_COS, dataType:ZmSetting.D_INT});
    this.registerSetting("DOCUMENT_SIZE_LIMIT",             {type:ZmSetting.T_COS, dataType:ZmSetting.D_INT});

	// CLIENT SIDE FEATURE SUPPORT
	this.registerSetting("ATTACHMENT_ENABLED",				{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("ATT_VIEW_ENABLED",				{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("EVAL_ENABLED",					{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("FEED_ENABLED",					{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("HELP_ENABLED",					{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("HISTORY_SUPPORT_ENABLED",			{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("MIXED_VIEW_ENABLED",				{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("NOTES_ENABLED",					{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("PRINT_ENABLED",					{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("SEARCH_ENABLED",					{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("SHORTCUT_LIST_ENABLED",			{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("OFFLINE_ENABLED",					{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:appCtxt.isOffline});
	this.registerSetting("SPELL_CHECK_ENABLED",				{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:!appCtxt.isOffline && (!AjxEnv.isSafari || AjxEnv.isSafari3up || AjxEnv.isChrome)});
	this.registerSetting("SPELL_CHECK_ADD_WORD_ENABLED",	{type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:!AjxEnv.isSafari || AjxEnv.isSafari3up || AjxEnv.isChrome});

	//SETTINGS SET AT DOMAIN LEVEL
	this.registerSetting("EMAIL_VALIDATION_REGEX",			{name:"zimbraMailAddressValidationRegex", type:ZmSetting.T_DOMAIN, dataType:ZmSetting.D_LIST});

	// USER PREFERENCES (mutable)

	// general preferences
	this.registerSetting("ACCOUNTS",						{type: ZmSetting.T_PREF, dataType: ZmSetting.D_HASH});
	this.registerSetting("ACCOUNT_TREE_OPEN",				{name:"zimbraPrefAccountTreeOpen", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isImplicit:true});
	this.registerSetting("CHILD_ACCTS_VISIBLE",				{name:"zimbraPrefChildVisibleAccount", type:ZmSetting.T_PREF, dataType:ZmSetting.D_LIST});
	this.registerSetting("CLIENT_TYPE",						{name:"zimbraPrefClientType", type:ZmSetting.T_PREF, defaultValue:ZmSetting.CLIENT_ADVANCED});
	this.registerSetting("COMPOSE_AS_FORMAT",				{name:"zimbraPrefComposeFormat", type:ZmSetting.T_PREF, defaultValue:ZmSetting.COMPOSE_TEXT, isGlobal:true});
	this.registerSetting("COMPOSE_INIT_FONT_COLOR",			{name:"zimbraPrefHtmlEditorDefaultFontColor", type:ZmSetting.T_PREF, defaultValue:ZmSetting.COMPOSE_FONT_COLOR, isGlobal:true});
	this.registerSetting("COMPOSE_INIT_FONT_FAMILY",		{name:"zimbraPrefHtmlEditorDefaultFontFamily", type:ZmSetting.T_PREF, defaultValue:ZmSetting.COMPOSE_FONT_FAM, isGlobal:true});
	this.registerSetting("COMPOSE_INIT_FONT_SIZE",			{name:"zimbraPrefHtmlEditorDefaultFontSize", type:ZmSetting.T_PREF, defaultValue:ZmSetting.COMPOSE_FONT_SIZE, isGlobal:true});
	this.registerSetting("DEFAULT_TIMEZONE",				{name:"zimbraPrefTimeZoneId", type:ZmSetting.T_PREF, dataType:ZmSetting.D_STRING, defaultValue:AjxTimezone.getServerId(AjxTimezone.DEFAULT), isGlobal:true});
    this.registerSetting("DEFAULT_PRINTFONTSIZE",	    	{name:"zimbraPrefDefaultPrintFontSize", type:ZmSetting.T_PREF, dataType:ZmSetting.D_STRING, defaultValue:ZmSetting.PRINT_FONT_SIZE, isGlobal:true});    
	this.registerSetting("FILTERS",							{type: ZmSetting.T_PREF, dataType: ZmSetting.D_HASH});
	this.registerSetting("IDENTITIES",						{type: ZmSetting.T_PREF, dataType: ZmSetting.D_HASH});
	this.registerSetting("INITIALLY_SEARCH_GAL",			{name:"zimbraPrefGalSearchEnabled", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("LIST_VIEW_COLUMNS",				{name:"zimbraPrefListViewColumns", type:ZmSetting.T_PREF, dataType:ZmSetting.D_HASH, isImplicit:true});
	this.registerSetting("LOCALE_NAME",						{name:"zimbraPrefLocale", type:ZmSetting.T_PREF, defaultValue:appRequestLocaleId, isGlobal:true});
	this.registerSetting("SHOW_SELECTION_CHECKBOX",			{name:"zimbraPrefShowSelectionCheckbox", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:true, isGlobal:true});
	// PAGE_SIZE: number of items to fetch for virtual paging; also used for number of msgs in one page of a conv
	this.registerSetting("PAGE_SIZE",						{name: "zimbraPrefItemsPerVirtualPage", type:ZmSetting.T_PREF, dataType:ZmSetting.D_INT, defaultValue:50, isGlobal:true});
	this.registerSetting("PASSWORD",						{type:ZmSetting.T_PREF, dataType:ZmSetting.D_NONE});
	this.registerSetting("POLLING_INTERVAL",				{name:"zimbraPrefMailPollingInterval", type:ZmSetting.T_PREF, dataType:ZmSetting.D_LDAP_TIME, defaultValue:300});
	this.registerSetting("POLLING_INTERVAL_ENABLED",		{name:"zimbraFeatureMailPollingIntervalPreferenceEnabled", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("SEARCH_INCLUDES_SPAM",			{name:"zimbraPrefIncludeSpamInSearch", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isGlobal:true});
	this.registerSetting("SEARCH_INCLUDES_TRASH",			{name:"zimbraPrefIncludeTrashInSearch", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isGlobal:true});
	this.registerSetting("SHORT_ADDRESS",					{name:"zimbraPrefShortEmailAddress", type:ZmSetting.T_PREF, dataType: ZmSetting.D_BOOLEAN, defaultValue: true});
	this.registerSetting("SHORTCUTS",						{name:"zimbraPrefShortcuts", type:ZmSetting.T_PREF});
	this.registerSetting("SHOW_SEARCH_STRING",				{name:"zimbraPrefShowSearchString", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isGlobal:true});
	this.registerSetting("SIGNATURES",						{type: ZmSetting.T_PREF, dataType: ZmSetting.D_HASH});
	this.registerSetting("SIGNATURES_MAX",					{name:"zimbraSignatureMaxNumEntries", type:ZmSetting.T_COS, dataType:ZmSetting.D_INT, defaultValue:20});
	this.registerSetting("SIGNATURES_MIN",					{name:"zimbraSignatureMinNumEntries", type:ZmSetting.T_COS, dataType:ZmSetting.D_INT, defaultValue:1});
	this.registerSetting("SKIN_NAME",						{name:"zimbraPrefSkin", type:ZmSetting.T_PREF, defaultValue:"skin", isGlobal:true});
	this.registerSetting("SORTING_PREF",					{name:"zimbraPrefSortOrder", type:ZmSetting.T_PREF, dataType:ZmSetting.D_HASH, isImplicit:true, isGlobal:true, dontSaveDefault: true});
	this.registerSetting("USE_ADDR_BUBBLES",				{name:"zimbraPrefAutocompleteAddressBubblesEnabled", type: ZmSetting.T_PREF, dataType: ZmSetting.D_BOOLEAN, defaultValue: true});
	this.registerSetting("USE_KEYBOARD_SHORTCUTS",			{name:"zimbraPrefUseKeyboardShortcuts", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("VIEW_AS_HTML",					{name:"zimbraPrefMessageViewHtmlPreferred", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isGlobal:true});
	this.registerSetting("VOICE_ACCOUNTS",					{type: ZmSetting.T_PREF, dataType: ZmSetting.D_HASH});
	this.registerSetting("WARN_ON_EXIT",					{name:"zimbraPrefWarnOnExit", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});

	this._registerOfflineSettings();
	this._registerZimletsSettings();

	// need to do this before loadUserSettings(), and zimlet settings are not tied to an app where it would normally be done
	this.registerSetting("ZIMLET_TREE_OPEN",				{name:"zimbraPrefZimletTreeOpen", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isImplicit:true});

	//shared settings
	this.registerSetting("MAIL_ALIASES",					{name:"zimbraMailAlias", type:ZmSetting.T_COS, dataType:ZmSetting.D_LIST});
	this.registerSetting("ALLOW_FROM_ADDRESSES",			{name:"zimbraAllowFromAddress", type:ZmSetting.T_COS, dataType:ZmSetting.D_LIST});

    this.registerSetting("AUTO_ARCHIVE_ENABLED",			{name:"zimbraPrefAutoArchiveEnabled", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isGlobal:true});
    this.registerSetting("AUTO_ARCHIVE_AGE",				{name:"zimbraPrefAutoArchiveNoOfDays", type:ZmSetting.T_PREF, dataType:ZmSetting.D_STRING, isGlobal:true});
    this.registerSetting("AUTO_ARCHIVE_FREQ",				{name:"zimbraPrefAutoArchiveFrequency", type:ZmSetting.T_PREF, dataType:ZmSetting.D_STRING, defaultValue:"On application launch", isGlobal:true});

    this.registerSetting("PASSWORD_LOCK_ENABLED",			{name:"zimbraPrefPasswordLockEnabled", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isGlobal:true});
};

/**
 * @private
 */
ZmSettings.prototype._registerZimletsSettings =
function() {
	// zimlet-specific
	this.registerSetting("CHECKED_ZIMLETS_ENABLED",			{name:"zimbraFeatureManageZimlets", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:true, isGlobal:true});
	this.registerSetting("CHECKED_ZIMLETS",					{name:"zimbraPrefZimlets", type:ZmSetting.T_PREF, dataType:ZmSetting.D_LIST, isGlobal:true});
    this.registerSetting("MANDATORY_ZIMLETS",		        {name:"zimbraZimletMandatoryZimlets", type:ZmSetting.T_COS, dataType:ZmSetting.D_LIST});
    this.registerSetting("ZIMLETS_SYNCHRONOUS",		        {name:"zimbraZimletLoadSynchronously", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});

};

/**
 * @private
 */
ZmSettings.prototype._registerOfflineSettings =
function() {
	if (!appCtxt.isOffline) { return; }

	// offline-specific
	this.registerSetting("OFFLINE_ACCOUNT_FLAVOR",			{name:"offlineAccountFlavor", type:ZmSetting.T_PREF, dataType:ZmSetting.D_STRING});
	this.registerSetting("OFFLINE_COMPOSE_ENABLED",			{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isGlobal:true});
	this.registerSetting("OFFLINE_DEBUG_TRACE",				{type:ZmSetting.T_CONFIG, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
	this.registerSetting("OFFLINE_IS_MAILTO_HANDLER",		{name:"zimbraPrefMailtoHandlerEnabled", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isGlobal:true});
	this.registerSetting("OFFLINE_NOTEBOOK_SYNC_ENABLED",	{name:"zimbraPrefNotebookSyncEnabled", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isGlobal:true});
	this.registerSetting("OFFLINE_REMOTE_SERVER_URI",		{name:"offlineRemoteServerUri", type:ZmSetting.T_PREF, dataType:ZmSetting.D_STRING});
	this.registerSetting("OFFLINE_REMOTE_SERVER_VERSION",	{name:"offlineRemoteServerVersion", type:ZmSetting.T_PREF, dataType:ZmSetting.D_STRING});
	this.registerSetting("OFFLINE_REPORT_EMAIL",			{type:ZmSetting.T_PREF, dataType:ZmSetting.D_STRING, defaultValue:"zdesktop-report@zimbra.com", isGlobal:true});
	this.registerSetting("OFFLINE_SHOW_ALL_MAILBOXES",		{name:"offlineShowAllMailboxes", type:ZmSetting.T_METADATA, dataType:ZmSetting.D_BOOLEAN, defaultValue:true, section:ZmSetting.M_OFFLINE, isGlobal:true});
	this.registerSetting("OFFLINE_ALL_MAILBOXES_TREE_OPEN",	{name:"offlineAllMailboxesTreeOpen", type:ZmSetting.T_METADATA, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, section:ZmSetting.M_OFFLINE, isGlobal:true, isImplicit:true});
	this.registerSetting("OFFLINE_NOTIFY_NEWMAIL_ON_INBOX",	{name:"offlineNotifyNewMailOnInbox", type:ZmSetting.T_METADATA, dataType:ZmSetting.D_BOOLEAN, defaultValue:true, section:ZmSetting.M_OFFLINE, isGlobal:true});
	this.registerSetting("OFFLINE_SAVED_SEARCHES_TREE_OPEN",{name:"offlineSavedSearchesTreeOpen", type:ZmSetting.T_METADATA, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, section:ZmSetting.M_OFFLINE, isGlobal:true, isImplicit:true});
	this.registerSetting("OFFLINE_SMTP_ENABLED",			{name:"zimbraDataSourceSmtpEnabled", type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:true});
	this.registerSetting("OFFLINE_SUPPORTS_MAILTO",			{type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isGlobal:true});
	this.registerSetting("OFFLINE_SUPPORTS_DOCK_UPDATE",	{type:ZmSetting.T_PREF, dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isGlobal:true});
	this.registerSetting("OFFLINE_WEBAPP_URI",				{name:"offlineWebappUri", type:ZmSetting.T_PREF, dataType:ZmSetting.D_STRING});
    this.registerSetting("OFFLINE_BACKUP_INTERVAL",	        {name:"zimbraPrefOfflineBackupInterval", type:ZmSetting.T_PREF, dataType:ZmSetting.D_INT, defaultValue:0, isGlobal:true});
    this.registerSetting("OFFLINE_BACKUP_PATH",	            {name:"zimbraPrefOfflineBackupPath", type:ZmSetting.T_PREF, dataType:ZmSetting.D_STRING, isGlobal:true});
    this.registerSetting("OFFLINE_BACKUP_KEEP",	            {name:"zimbraPrefOfflineBackupKeep", type:ZmSetting.T_PREF, dataType:ZmSetting.D_INT, isGlobal:true});
    this.registerSetting("OFFLINE_BACKUP_ACCOUNT_ID",       {name:"zimbraPrefOfflineBackupAccountId", type:ZmSetting.T_PREF, dataType:ZmSetting.D_INT, isGlobal:true});
    this.registerSetting("OFFLINE_BACKUP_RESTORE",          {name:"zimbraPrefOfflineBackupRestore", dataType:ZmSetting.D_INT, isGlobal:true});
    this.registerSetting("OFFLINE_BACKUP_NOW_BUTTON",       {name:"zimbraPrefOfflineBackupAccount", dataType:ZmSetting.D_BOOLEAN, defaultValue:false, isGlobal:true});
    this.registerSetting("OFFLINE_ZIMLET_SYNC_ACCOUNT_ID",  {name:"zimbraPrefOfflineZimletSyncAccountId", type:ZmSetting.T_PREF, dataType:ZmSetting.D_STRING, isGlobal:true});
	this.registerSetting("OFFLINE_WEBAPP_URI",				{name:"offlineWebappUri", type:ZmSetting.T_PREF, dataType:ZmSetting.D_STRING});

	// reset the help URI to zimbra.com for offline
	this.registerSetting("HELP_URI",						{type:ZmSetting.T_CONFIG, defaultValue:"http://www.zimbra.com/desktop7/"});
//	// make default false for DUMPSTER_ENABLED. shouldn't be necessary since GetInfoResponse includes zimbraDumpsterEnabled:"FALSE", but can't find why settings is not read correctly
	this.registerSetting("DUMPSTER_ENABLED",				{name:"zimbraDumpsterEnabled", type:ZmSetting.T_COS, dataType:ZmSetting.D_BOOLEAN, defaultValue:false});
    this.registerSetting("OFFLINE_UPDATE_NOTIFY",			{name:"zimbraPrefOfflineUpdateChannel", type: ZmSetting.T_PREF, dataType: ZmSetting.D_STRING, isGlobal:true});
};

/**
 * @private
 */
ZmSettings.prototype._changeListener =
function(ev) {
	if (ev.type != ZmEvent.S_SETTING) { return; }

	var id = ev.source.id;
	if (id == ZmSetting.QUOTA_USED) {
		appCtxt.getAppController().setUserInfo();
	} else if (id == ZmSetting.POLLING_INTERVAL) {
		appCtxt.getAppController().setPollInterval();
	} else if (id == ZmSetting.SKIN_NAME) {
		var cd = appCtxt.getYesNoMsgDialog();
		cd.reset();
		var skin = ev.source.getValue();
		cd.registerCallback(DwtDialog.YES_BUTTON, this._newSkinYesCallback, this, [skin, cd]);
		cd.setMessage(ZmMsg.skinChangeRestart, DwtMessageDialog.WARNING_STYLE);
		cd.popup();
	} else if (id == ZmSetting.LOCALE_NAME) {
		// When changing language preference make sure to write it in file system, because we don't want to store it in zimbra server
		if(appCtxt.isOffline && window.isNodeWebkit) {
			var localeId = appCtxt.get(ZmSetting.LOCALE_NAME);

			if(localeId) {
				NodeWebkitPrefs.addPreference('LOCALE_NAME', localeId);
				NodeWebkitUtils.updateSpellCheck(localeId);
				//Update Zd message as per locale
				NodeWebkitI18n.loadZdMessages();
			}
		}

		var cd = appCtxt.getYesNoMsgDialog();
		cd.reset();
		cd.registerCallback(DwtDialog.YES_BUTTON, this._refreshBrowserCallback, this, [cd]);
		cd.setMessage(ZmMsg.localeChangeRestart, DwtMessageDialog.WARNING_STYLE);
		cd.popup();
	} else if (id == ZmSetting.CHILD_ACCTS_VISIBLE || id == ZmSetting.OFFLINE_NOTEBOOK_SYNC_ENABLED) {
		var cd = appCtxt.getYesNoMsgDialog();
		cd.reset();
		cd.registerCallback(DwtDialog.YES_BUTTON, this._refreshBrowserCallback, this, [cd]);
		cd.setMessage(ZmMsg.accountChangeRestart, DwtMessageDialog.WARNING_STYLE);
		cd.popup();
	} else if (appCtxt.isOffline && id == ZmSetting.OFFLINE_IS_MAILTO_HANDLER) {
        if(window.isNodeWebkit) {
            NodeWebkitMailto.updateRegistry(!ev.source.getValue());
        }
	} else if (appCtxt.isOffline && id == ZmSetting.OFFLINE_UPDATE_NOTIFY) {
	    var notifySetting = ev.source.getValue();
        NodeWebkitPrefs.addPreference('AUTO_UPDATE_NOTIFICATION', notifySetting);

		if (notifySetting === "doNotNotify") {
		    NodeWebkitAutoUpdate.cancelAutoUpdateTimer();
		} else {
		    NodeWebkitAutoUpdate.scheduleAutoUpdate();
		}
	} else if (id == ZmSetting.PASSWORD_LOCK_ENABLED) {
		var el = Dwt.byId(ZmId.PASSSWORD_LOCK);
		if (el) {
			if (ev.source.value) {
				Dwt.setVisibility(el, true);
			}
			else {
				Dwt.setVisibility(el, false);
			}
		}
	}
};

ZmSettings.prototype._implicitChangeListener =
function(ev) {
	if (ev.type != ZmEvent.S_SETTING) { return; }
	var id = ev.source.id;
	var setting = this.getSetting(id);
	if (id == ZmSetting.FOLDERS_EXPANDED && window.duringExpandAll) {
		if (!window.afterExpandAllCallback) {
			window.afterExpandAllCallback = this.save.bind(this, [setting], null, null, appCtxt.getActiveAccount(), true);
		}
		return;
	}
	if (ZmSetting.IS_IMPLICIT[id] && setting) {
		this.save([setting], null, null, appCtxt.getActiveAccount(), true);
	}
};

/**
 * @private
 */
ZmSettings.prototype._newSkinYesCallback =
function(skin, dialog) {
	dialog.popdown();
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({qsArgs:{skin:skin}});
	DBG.println(AjxDebug.DBG1, "skin change, redirect to: " + url);
	ZmZimbraMail.sendRedirect(url); // redirect to self to force reload
};

/**
 * @private
 */
ZmSettings.prototype._refreshBrowserCallback =
function(dialog) {
	dialog.popdown();
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({
        // if language is changed then make sure localeId is updated in url
        qsArgs: {
            'localeId' : appCtxt.get(ZmSetting.LOCALE_NAME)
        }
    });
	window.location.replace(url);
};

// Fix for bug: 53347.
ZmSettings.prototype._base64toNormalSize =
function(base64) {
    if (!base64 || base64 === -1) { //-1 is unlimited
        return base64;
    }
    return base64 / ZmSettings.BASE64_TO_NORMAL_RATIO;
};