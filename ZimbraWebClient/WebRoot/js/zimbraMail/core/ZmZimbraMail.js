/*
 * 
 */

/**
 * @overview
 * This file contains the Zimbra mail controller class.
 * 
 */

/**
 * Creates a controller to run ZimbraMail. Do not call directly, instead use the run()
 * factory method.
 * @constructor
 * @class
 * This class is the "ubercontroller", as it manages all the apps as well as bootstrapping
 * the ZimbraMail application.
 *
 * @param {Hash}	params	a hash of parameters
 * @param {constant}    params.app		the starting app
 * @param  {Element}	params.userShell	the top-level skin container
 *        
 * @extends	ZmController
 */
ZmZimbraMail = function(params) {
	ZmController.call(this, null);

    appCtxt.setAppController(this);

	// ALWAYS set back reference into our world (also used by unload handler)
	window._zimbraMail = this;

    // app event handling
    this._evt = new ZmAppEvent();
    this._evtMgr = new AjxEventMgr();
    // copy over any statically registered listeners
    for (var type in ZmZimbraMail._listeners) {
        var list = ZmZimbraMail._listeners[type];
        if (list && list.length) {
            for (var i = 0; i < list.length; i++) {
                this._evtMgr.addListener(type, list[i]);
            }
        }
    }

    // all subsequent calls to register static app listeners go to instance
    ZmZimbraMail.addListener = AjxCallback.simpleClosure(this.addListener, this);
    ZmZimbraMail.addAppListener = AjxCallback.simpleClosure(this.addAppListener, this);

    // Create generic operations
    ZmOperation.initialize();

    // settings
    this._createSettings(params);
    this._createEnabledApps();
    this._initializeSettings(params);
	this._postInitializeSettings();

    // set internal state
	this._shell = appCtxt.getShell();
    this._userShell = params.userShell;

    this._requestMgr = new ZmRequestMgr(this); // NOTE: requires settings to be initialized

	this._upsellView = {};
	this._activeApp = null;
	this._sessionTimer = new AjxTimedAction(null, ZmZimbraMail.executeSessionTimer);
	this._sessionTimerId = -1;
	this._pollActionId = null;	// AjaxTimedAction ID of timer counting down to next poll time
	this._pollRequest = null;	// HTTP request of poll we've sent to server
	this._pollInstantNotifications = false; // if TRUE, we're in "instant notification" mode
	this.statusView = null;
	ZmZimbraMail._exitTimer = new AjxTimedAction(null, ZmZimbraMail.exitSession);
	ZmZimbraMail._exitTimerId = -1;
	ZmZimbraMail.stayOnPagePrompt = false;
	ZmZimbraMail.STAYONPAGE_INTERVAL = 2;  //in minutes
    // setup history support
    if (appCtxt.get(ZmSetting.HISTORY_SUPPORT_ENABLED) && !AjxEnv.isSafari) {
        window.historyMgr = appCtxt.getHistoryMgr();
    }

    // create app view manager
    this._appViewMgr = new ZmAppViewMgr(this._shell, this, false, true);

    // register handlers
	AjxDispatcher.setPackageLoadFunction("Zimlet", new AjxCallback(this, this._postLoadZimlet));

	AjxDispatcher.setPreLoadFunction(new AjxCallback(this, function() {
		this._appViewMgr.pushView(ZmId.VIEW_LOADING);
	}));
	AjxDispatcher.setPostLoadFunction(new AjxCallback(this, function() {
		this._appViewMgr._toRemove.push(ZmId.VIEW_LOADING);
	}));

	for (var i in ZmApp.QS_ARG) {
		ZmApp.QS_ARG_R[ZmApp.QS_ARG[i]] = i;
	}

	this._shell.addGlobalSelectionListener(new AjxListener(this, this._globalSelectionListener));

    /// go!
    this.startup(params);
};

ZmZimbraMail.prototype = new ZmController;
ZmZimbraMail.prototype.constructor = ZmZimbraMail;

// REVISIT: This is done so that we when we switch from being "beta"
//          to production, we don't have to ensure that all of the
//          translations are changed at the same time. We can simply
//          remove the beta suffix from the app name.
ZmMsg.BETA_documents = [ZmMsg.documents, ZmMsg.beta].join(" ");

// dummy app (needed when defining drop targets in _registerOrganizers)
ZmApp.MAIN = "ZmZimbraMail";
ZmApp.DROP_TARGETS[ZmApp.MAIN] = {};

// Static listener registration
ZmZimbraMail._listeners = {};

// Consts
ZmZimbraMail.UI_LOAD_BEGIN		= "ui_load_begin";
ZmZimbraMail.UI_LOAD_END		= "ui_load_end";
ZmZimbraMail.UI_NETWORK_UP		= "network_up";
ZmZimbraMail.UI_NETWORK_DOWN	= "network_down";


// Public methods

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmZimbraMail.prototype.toString =
function() {
	return "ZmZimbraMail";
};

/**
 * Sets up ZimbraMail, and then starts it by calling its constructor. It is assumed that the
 * CSFE is on the same host.
 *
 * @param {Hash}	params			a hash of parameters
 * @param {constant}      params.app				te starting app
 * @param {Boolean}      params.offlineMode		if <code>true</code>, this is the offline client
 * @param {Boolean}      params.devMode			if <code>true</code>, we are in development environment
 * @param {Hash}      params.settings			the server prefs/attrs
 * @param {constant}      params.protocolMode	the protocal mode (http, https or mixed)
 * @param {Boolean}      params.noSplashScreen	if <code>true</code>, do not show splash screen during startup
 */
ZmZimbraMail.run =
function(params) {

	if (params.noSplashScreen) {
		ZmZimbraMail.killSplash();
	}

	// Create the global app context
	window.appCtxt = new ZmAppCtxt();
	appCtxt.rememberMe = false;

	// Handle offline mode
	if (params.offlineMode) {
		DBG.println(AjxDebug.DBG1, "OFFLINE MODE");
		appCtxt.isOffline = true;
	}

	// Create the shell
	var userShell = params.userShell = window.document.getElementById(ZmId.SKIN_SHELL);
	if (!userShell) {
		alert("Could not get user shell - skin file did not load properly");
	}
	var shell = new DwtShell({userShell:userShell, docBodyScrollable:false, id:ZmId.SHELL});
	appCtxt.setShell(shell);

	// Go!
	new ZmZimbraMail(params);
};

/**
 * Unloads the controller. Allows parent window to walk list of open child windows and either "delete" 
 * or "disable" them.
 * 
 */
ZmZimbraMail.unload =
function() {

	if (!ZmZimbraMail._endSessionDone) {
		ZmZimbraMail._endSession();
	}

	if (ZmZimbraMail._isLogOff) {
		ZmZimbraMail._isLogOff = false;
		// stop keeping track of user input (if applicable)
		if (window._zimbraMail) {
			window._zimbraMail.setSessionTimer(false);
		}

		ZmCsfeCommand.noAuth = true;
	}

	var childWinList = window._zimbraMail ? window._zimbraMail._childWinList : null;
	if (childWinList) {
		// close all child windows
		for (var i = 0; i < childWinList.size(); i++) {
			var childWin = childWinList.get(i);
			childWin.win.onbeforeunload = null;
			childWin.win.parentController = null;
			childWin.win.close();
		}
	}
	
	ZmZimbraMail.stayOnPagePrompt = false;
	ZmZimbraMail.setExitTimer(false);
	ZmZimbraMail.sessionTimerInvoked = false;
	window._zimbraMail = window.onload = window.onunload = window.onresize = window.document.onkeypress = null;
};

/**
 * Returns sort order using a and b as keys into given hash.
 *
 * @param {Hash}	hash		a hash with sort values
 * @param {String}	a			a key into hash
 * @param {String}	b			a key into hash
 * @return	{int}	0 if the items are the same; 1 if "a" is before "b"; -1 if "b" is before "a"
 */
ZmZimbraMail.hashSortCompare =
function(hash, a, b) {
	var appA = a ? Number(hash[a]) : 0;
	var appB = b ? Number(hash[b]) : 0;
	if (appA > appB) { return 1; }
	if (appA < appB) { return -1; }
	return 0;
};

/**
 * Hides the splash screen.
 * 
 */
ZmZimbraMail.killSplash =
function() {
	// 	Splash screen is now a part of the skin, loaded in statically via the JSP
	//	as a well-known ID.  To hide the splash screen, just hide that div.
	Dwt.hide("skin_container_splash_screen");
};

/**
 * Startup the mail controller.
 * 
 * <p>
 * The following steps are performed:
 * <ul>
 * <li>check for skin, show it</li>
 * <li>create app view mgr</li>
 * <li>create components (sash, banner, user info, toolbar above overview, status view)</li>
 * <li>create apps</li>
 * <li>load user settings (using a <code>&lt;GetInfoRequest&gt;</code>)</li>
 * </ul>
 * 
 * @param {Hash}	params		a hash of parameters
 * @param {constant}	app			the starting app
 * @param {Hash}	settings		a hash of settings overrides
 */
ZmZimbraMail.prototype.startup =
function(params) {
	if (appCtxt.isOffline) {
		this.sendClientEventNotify(ZmZimbraMail.UI_LOAD_BEGIN);
	}

	appCtxt.inStartup = true;
	if (typeof(skin) == "undefined") {
		DBG.println(AjxDebug.DBG1, "No skin!");
	}

	skin.show("skin", true);
	if (!this._components) {
		this._components = {};
		this._components[ZmAppViewMgr.C_SASH] = new DwtSash({parent:this._shell, style:DwtSash.HORIZONTAL_STYLE,
															 className:"console_inset_app_l", threshold:20, id:ZmId.MAIN_SASH});
		this._components[ZmAppViewMgr.C_BANNER] = this._createBanner();
		this._components[ZmAppViewMgr.C_USER_INFO] = this._userNameField =
			this._createUserInfo("BannerTextUser", ZmAppViewMgr.C_USER_INFO, ZmId.USER_NAME);
		this._components[ZmAppViewMgr.C_QUOTA_INFO] = this._usedQuotaField =
			this._createUserInfo("BannerTextQuota", ZmAppViewMgr.C_QUOTA_INFO, ZmId.USER_QUOTA);
		this._components[ZmAppViewMgr.C_STATUS] = this.statusView =
			new ZmStatusView(this._shell, "ZmStatus", Dwt.ABSOLUTE_STYLE, ZmId.STATUS_VIEW);

		if (appCtxt.isOffline) {
			this._initOfflineUserInfo();
		}
	}

	this._registerOrganizers();

	// set up map of search types to item types
	for (var i in ZmSearch.TYPE) {
		ZmSearch.TYPE_MAP[ZmSearch.TYPE[i]] = i;
	}
	ZmZimbraMail.registerViewsToTypeMap();

	this._getStartApp(params);

	this._postRenderCallbacks = [];
	this._postRenderLast = 0;
	if (params.startApp == ZmApp.MAIL) {
		this._doingPostRenderStartup = true;
		var callback = new AjxCallback(this,
			function() {
				AjxDispatcher.require("Startup2");
				var account = appCtxt.multiAccounts && appCtxt.accountList.mainAccount;
				if (appCtxt.get(ZmSetting.CALENDAR_ENABLED, null, account)) {
					this.handleCalendarComponents();
				}
                if (appCtxt.get(ZmSetting.TASKS_ENABLED, null, account)) {
					this.handleTaskComponents();
				}
				var sc = appCtxt.getSearchController();
				sc.getSearchToolbar().initAutocomplete();
				if (!appCtxt.isChildWindow) {
					sc.peopleSearchToolBar.initAutocomplete();
				}
				if (ZmZimbraMail.showPasswordLockFeature() && appCtxt.get(ZmSetting.PASSWORD_LOCK_ENABLED) &&
						params.showPwdLock != false) {
					//Show the password lock dialog
					ZmZimbraMail._handlePasswordLock();
				}
                if(appCtxt.isOffline && window.isNodeWebkit) {
                    var mailtoArg = NodeWebkitUtils.getAppParam('mailto');

                    if(typeof mailtoArg != 'undefined') {
                        var hash = NodeWebkitUtils.getHashOfString(mailtoArg);

                        // if mailto url is already processed then we will ignore it, so we will not process it on every reload
                        if(localStorage.getItem('mailto_url_hash') !== hash) {
                            // Store hash of mailto url in localstorage so we can use it to ignore same url next time
                            // this value will be not valid for next launch as at that time jetty port will get changed
                            localStorage.setItem('mailto_url_hash', hash);

                            this.handleOfflineMailTo(mailtoArg);
                        } else {
                            console.log('mailto url is already processed, ignoring this call');
                        }
                    }
                }
			});
		this.addPostRenderCallback(callback, 0, 0, true);
	}

    // NOTE: We must go through the request mgr for default handling
    var getInfoResponse = AjxUtil.get(params, "getInfoResponse");
    if (getInfoResponse) {
        this._requestMgr.sendRequest({response:getInfoResponse});
    }

	// fetch meta data for the main account
	var respCallback = new AjxCallback(this, this._handleResponseGetMetaData, params);
	appCtxt.accountList.mainAccount.loadMetaData(respCallback);
};

ZmZimbraMail.registerViewsToTypeMap = function() {
	// organizer types based on view
	for (var i in ZmOrganizer.VIEWS) {
		var list = ZmOrganizer.VIEWS[i];
		for (var j = 0; j < list.length; j++) {
			ZmOrganizer.TYPE[list[j]] = i;
		}
	}
};

ZmZimbraMail.prototype._createSettings = function(params) {
    // We've received canned SOAP responses for GetInfoRequest and SearchRequest from the
    // launch JSP, wrapped in a BatchRequest. Jiggle them so that they look like real
    // responses, and pass them along.
    if (params.batchInfoResponse) {
        var batchResponse = params.batchInfoResponse.Body.BatchResponse;

        // always assume there's a get info response
		var infoResponse = batchResponse.GetInfoResponse[0];
		if(!infoResponse) {
			infoResponse ={}
		}
		//store per-domain settings in infoResponse obj so we can access it like other settings
		infoResponse.domainSettings = params.settings;
        params.getInfoResponse = {
            Header: params.batchInfoResponse.Header,
            Body: { GetInfoResponse: infoResponse}
        };
        var session = AjxUtil.get(params.getInfoResponse, "Header", "context", "session");
        if (session) {
            ZmCsfeCommand.setSessionId(session);
        }
        DBG.println(AjxDebug.DBG1, ["<b>RESPONSE (from JSP tag)</b>"].join(""), "GetInfoResponse");
        DBG.dumpObj(AjxDebug.DBG1, params.getInfoResponse, -1);

        // we may have an initial search response
        if (batchResponse.SearchResponse) {
            params.searchResponse = {
                Body: { SearchResponse: batchResponse.SearchResponse[0] }
            };
            DBG.println(AjxDebug.DBG1, ["<b>RESPONSE (from JSP tag)</b>"].join(""), "SearchResponse");
            DBG.dumpObj(AjxDebug.DBG1, params.searchResponse, -1);
        }
    }

    // create settings
    var settings = new ZmSettings()
    appCtxt.setSettings(settings);

    // We have to pre-initialize the settings in order to create
    // the enabled apps correctly.
    settings.setUserSettings({info:params.getInfoResponse.Body.GetInfoResponse, preInit:true});
};

ZmZimbraMail.prototype._initializeSettings = function(params) {
    var info = params.getInfoResponse.Body.GetInfoResponse;

    var settings = appCtxt.getSettings();
    // NOTE: Skip notify to avoid callbacks which reference objects that aren't set, yet
    settings.setUserSettings(info, null, true, true, true);
    settings.userSettingsLoaded = true;

    // settings structure and defaults
    var branch = appCtxt.get(ZmSetting.BRANCH);
    if (window.DBG && !DBG.isDisabled()) {
        DBG.setTitle("Debug (" + branch + ")");
    }

    // Note: removing cookie support will affect zdesktop when connecting 4.x remote server
    if (params.offlineMode) {
        var apps = AjxCookie.getCookie(document, ZmSetting.APPS_COOKIE);
        DBG.println(AjxDebug.DBG1, "apps: " + apps);
        if (apps) {
            for (var appsetting in ZmSetting.APP_LETTER) {
                var letter = ZmSetting.APP_LETTER[appsetting];
                if (apps.indexOf(letter) != -1) {
                    settings.getSetting(appsetting).setValue(true);
                }
            }
        }
    }

    // setting overrides
    if (params.settings) {
        for (var name in params.settings) {
            var id = settings.getSettingByName(name);
            if (id) {
                settings.getSetting(id).setValue(params.settings[name]);
            }
        }
    }

    // reset polling interval for offline
    if (appCtxt.isOffline) {
        appCtxt.set(ZmSetting.POLLING_INTERVAL, 60, null, null, true);
    }

    // Handle dev mode
    if (params.devMode == "1") {
        DBG.println(AjxDebug.DBG1, "DEV MODE");
        appCtxt.set(ZmSetting.DEV, true);
        appCtxt.set(ZmSetting.POLLING_INTERVAL, 0);
    }

    // Handle protocol mode - standardize on trailing :
    if (params.protocolMode) {
        var proto = (params.protocolMode.indexOf(":") == -1) ? params.protocolMode + ":" : params.protocolMode;
        appCtxt.set(ZmSetting.PROTOCOL_MODE, proto);
    }
    if (params.httpPort) {
        appCtxt.set(ZmSetting.HTTP_PORT, params.httpPort);
    }
    if (params.httpsPort) {
        appCtxt.set(ZmSetting.HTTPS_PORT, params.httpsPort);
    }

    // hide spam if not enabled
    if (!appCtxt.get(ZmSetting.SPAM_ENABLED)) {
        ZmFolder.HIDE_ID[ZmFolder.ID_SPAM] = true;
    }
};

/**
 * Perform any additional operation after initializing settings
 * @private
 */
ZmZimbraMail.prototype._postInitializeSettings =
function() {
	this._setCustomInvalidEmailPats();
};

/**
 * Set an array of invalid Email patterns(values of zimbraMailAddressValidationRegex in ldap) to
 * AjxEmailAddress.customInvalidEmailPats
 * @private
 */
ZmZimbraMail.prototype._setCustomInvalidEmailPats =
function() {
 	var customPatSetting = appCtxt.getSettings().getSetting(ZmSetting.EMAIL_VALIDATION_REGEX);
	var cPatList = [];
	if(customPatSetting) {
		cPatList = customPatSetting.value;
	}
	for(var i = 0; i< cPatList.length; i++) {
		var pat = cPatList[i];
		if(pat && pat != "") {
			  AjxEmailAddress.customInvalidEmailPats.push(new RegExp(pat))
		}
	}
};

/**
 * @private
 */
ZmZimbraMail.prototype._handleResponseGetMetaData =
function(params) {
    this._handleResponseLoadUserSettings(params);
};

/**
 * Shows the mini-calendar.
 * 
 */
ZmZimbraMail.prototype.showMiniCalendar =
function() {
	var calMgr = appCtxt.getCalManager();
	calMgr.getMiniCalendar();
	appCtxt.getAppViewMgr().showTreeFooter(true);
    calMgr.highlightMiniCal();
    calMgr.startDayRollTimer();
};

/**
 * Shows reminders.
 */
ZmZimbraMail.prototype.showReminder =
function() {
	var calMgr = appCtxt.getCalManager();
	var reminderController = calMgr.getReminderController();
	reminderController.refresh();
};

/**
 * Shows reminders.
 */
ZmZimbraMail.prototype.showTaskReminder =
function() {
	var taskMgr = appCtxt.getTaskManager();
	var taskReminderController = taskMgr.getReminderController();
	taskReminderController.refresh();
};

/**
 * @private
 */
ZmZimbraMail.prototype._handleErrorStartup =
function(params, ex) {
	ZmZimbraMail.killSplash();
	appCtxt.inStartup = false;
	return false;
};

/**
 * @private
 */
ZmZimbraMail.prototype._handleResponseLoadUserSettings =
function(params, result) {
	if (appCtxt.multiAccounts) {
		var callback = new AjxCallback(this, this._handleResponseStartup, [params, result]);
		appCtxt.accountList.loadAccounts(callback);
	} else {
		this._handleResponseStartup(params, result);
	}
};

/**
 * Startup: part 2
 * 	- create app toolbar component
 * 	- determine and launch starting app
 *
 * @param {Hash}	params			a hash of parameters
 * @param       {constant}	params.app				the starting app
 * @param       {Object}	params.settingOverrides	a hash of overrides of user settings
 * @param {ZmCsfeResult}	result		the result object from load of user settings
 * 
 * @private
 */
ZmZimbraMail.prototype._handleResponseStartup =
function(params, result) {

	if (params && params.settingOverrides) {
		this._needOverviewLayout = true;
		for (var id in params.settingOverrides) {
			var setting = appCtxt.getSetting(id);
			if (setting) {
				setting.setValue(params.settingOverrides[id]);
			}
		}
	}

	if (!appCtxt.isOffline) {
        if (appCtxt.get(ZmSetting.INSTANT_NOTIFY) && appCtxt.get(ZmSetting.INSTANT_NOTIFY_INTERVAL) == appCtxt.get(ZmSetting.POLLING_INTERVAL))
            AjxTimedAction.scheduleAction(new AjxTimedAction(this, this.setInstantNotify, [true]), 4000);
        else
		    this.setPollInterval(true);
	}

	window.onbeforeunload = ZmZimbraMail._confirmExitMethod;

	if (!this._components[ZmAppViewMgr.C_APP_CHOOSER]) {
		this._components[ZmAppViewMgr.C_APP_CHOOSER] = this._appChooser = this._createAppChooser();
	}

	ZmApp.initialize();

    if(appCtxt.get(ZmSetting.DEFAULT_TIMEZONE)) {
        AjxTimezone.DEFAULT_RULE = AjxTimezone._guessMachineTimezone(appCtxt.get(ZmSetting.DEFAULT_TIMEZONE));
        AjxTimezone.DEFAULT = AjxTimezone.getClientId(AjxTimezone.DEFAULT_RULE.serverId);
    }

	this._evtMgr.notifyListeners(ZmAppEvent.PRE_STARTUP, this._evt);

	params.result = result;
	var respCallback = new AjxCallback(this, this._handleResponseStartup1, params);

	// startup and packages have been optimized for quick mail display
	if (this._doingPostRenderStartup) {
		this.addAppListener(params.startApp, ZmAppEvent.POST_RENDER, new AjxListener(this, this._postRenderStartup));
		this._searchResponse = params.searchResponse;
	} else {
		AjxDispatcher.require("Startup2");
	}

	// Set up post-render callbacks

	// run app-related startup functions
	var callback = new AjxCallback(this,
		function() {
			this.runAppFunction("startup", false, params.result);
		});
	this.addPostRenderCallback(callback, 2, 100, true);

	callback = new AjxCallback(this,
		function() {
			this._setupTabGroups();
			this.focusContentPane();
		});
	this.addPostRenderCallback(callback, 3, 100);

	// miscellaneous post-startup housekeeping
	callback = new AjxCallback(this,
		function() {
			AjxDispatcher.enableLoadFunctions(true);
			appCtxt.inStartup = false;
			this._evtMgr.notifyListeners(ZmAppEvent.POST_STARTUP, this._evt);

			// bug fix #31996
			if (appCtxt.isOffline) {
				appCtxt.getSearchController().resetSearchToolbar();
			}

			var contactListPkg = appCtxt.multiAccounts ? "GetContactsForAllAccounts" : "GetContacts";
			AjxDispatcher.run(contactListPkg);
	
			if (appCtxt.get(ZmSetting.OFFLINE_SUPPORTS_MAILTO) && appCtxt.isOffline) {
				this.handleOfflineMailTo(location.search);
			}

			if (ZmZimbraMail.showPasswordLockFeature() && appCtxt.get(ZmSetting.PASSWORD_LOCK_ENABLED) &&
				params.showPwdLock !== false) {
				// Grab focus to password lock dialog when all other DWT components are loaded.
				var passwordLockDialog = appCtxt.getPasswordLockDialog();
				appCtxt.getKeyboardMgr().grabFocus(passwordLockDialog._nameField);
			}
		});
	this.addPostRenderCallback(callback, 5, 100);

	this.activateApp(params.startApp, false, respCallback, this._errorCallback, params);

	var account = appCtxt.multiAccounts && appCtxt.accountList.mainAccount;
	if (appCtxt.get(ZmSetting.CALENDAR_ENABLED, null, account) &&
		!this._doingPostRenderStartup &&
		(params.startApp != ZmApp.CALENDAR))
	{
		this.handleCalendarComponents();
	}
    if (appCtxt.get(ZmSetting.TASKS_ENABLED, null, account) &&
		!this._doingPostRenderStartup &&
		(params.startApp != ZmApp.TASKS))
	{
		this.handleTaskComponents();
	}
};

/**
 * Creates & show Task Reminders on delay
 *
 * @private
 */
ZmZimbraMail.prototype.handleTaskComponents =
function() {
    // reminder controlled by calendar preferences setting
    var reminderAction = new AjxTimedAction(this, this.showTaskReminder);
    var delay = appCtxt.isOffline ? 0 : ZmTasksApp.REMINDER_START_DELAY;
    AjxTimedAction.scheduleAction(reminderAction, delay);
};

/**
 * Creates mini calendar and shows reminders on delay
 * 
 * @private
 */
ZmZimbraMail.prototype.handleCalendarComponents =
function() {
	if (appCtxt.get(ZmSetting.CAL_ALWAYS_SHOW_MINI_CAL)) {
        var miniCalAction = new AjxTimedAction(this, this.showMiniCalendar);
		var delay = appCtxt.isOffline ? 0 : ZmCalendarApp.MINICAL_DELAY;
        AjxTimedAction.scheduleAction(miniCalAction, delay);
	}

	// reminder controlled by calendar preferences setting
    var reminderAction = new AjxTimedAction(this, this.showReminder);
    var delay = appCtxt.isOffline ? 0 : ZmCalendarApp.REMINDER_START_DELAY;
    AjxTimedAction.scheduleAction(reminderAction, delay);

};

/**
 * Startup: part 3
 * 	- populate user info
 * 	- create search bar
 * 	- set up keyboard handling (shortcuts and tab groups)
 * 	- kill splash, show UI
 * 	- check license
 *
 * @param {Hash}	params			a hash of parameters
 * @param {constant}	params.app				the starting app
 * @param {Object}	params.settingOverrides	a hash of overrides of user settings
 *        
 * @private
 */
ZmZimbraMail.prototype._handleResponseStartup1 =
function(params) {
	if (ZmZimbraMail.showPasswordLockFeature()) {
		this._createPasswordLock();
	}

	this._setExternalLinks();
	this.setUserInfo();

	if (appCtxt.get(ZmSetting.SEARCH_ENABLED)) {
		this._components[ZmAppViewMgr.C_SEARCH] = appCtxt.getSearchController().searchPanel;
	}

	if (appCtxt.get(ZmSetting.PEOPLE_SEARCH_ENABLED) &&
		(appCtxt.get(ZmSetting.CONTACTS_ENABLED) ||
		appCtxt.get(ZmSetting.GAL_ENABLED) ||
		appCtxt.isOffline))
	{
		this._components[ZmAppViewMgr.C_PEOPLE_SEARCH] = appCtxt.getSearchController().peopleSearchToolBar;
	}
	else {
		Dwt.hide(ZmId.SKIN_PEOPLE_SEARCH);
	}

	this.getKeyMapMgr();	// make sure keyboard handling is initialized

	this.setSessionTimer(true);
	ZmZimbraMail.killSplash();

	// Give apps a chance to add their own ui components.
	this.runAppFunction("addComponents", false, this._components);

	// next line makes the UI appear
	var viewComponents = this._appViewMgr._components;
	this._appViewMgr.addComponents(this._components, true);
	if (viewComponents) {
		// While adding the basic components we need to make sure the already
		// set view components are again fitted to perfection.
		this._appViewMgr.addComponents(viewComponents, true);
	}

	this._checkLicense();

	if (!this._doingPostRenderStartup) {
		this._postRenderStartup();
	}

};

// popup a warning dialog if there is a problem with the license
ZmZimbraMail.prototype._checkLicense =
function(ev) {

	var status = appCtxt.get(ZmSetting.LICENSE_STATUS);
	var msg = ZmSetting.LICENSE_MSG[status];
	if (msg) {
		AjxDispatcher.require("Startup2");
		var dlg = appCtxt.getMsgDialog();
		dlg.reset();
        dlg.setMessage(msg, DwtMessageDialog.WARNING_STYLE);
		dlg.popup();
	}
};

/**
 * The work to render the start app has been done. Now perform all the startup
 * work that remains - each piece of work is contained in a callback with an
 * associated order and delay.
 * 
 * @private
 */
ZmZimbraMail.prototype._postRenderStartup =
function(ev) {
	this._postRenderCallbacks.sort(function(a, b) {
		return a.order - b.order;
	});
	this._runNextPostRenderCallback();
};

/**
 * @private
 */
ZmZimbraMail.prototype._runNextPostRenderCallback =
function() {
	DBG.println(AjxDebug.DBG2, "POST-RENDER CALLBACKS: " + this._postRenderCallbacks.length);
	if (this._postRenderCallbacks && this._postRenderCallbacks.length) {
		var prcb = this._postRenderCallbacks.shift();
		if (!prcb) { return; }
		DBG.println(AjxDebug.DBG2, "POST-RENDER CALLBACK: #" + prcb.order + ", delay " + prcb.delay + " in " + prcb.callback.obj.toString());
		AjxTimedAction.scheduleAction(new AjxTimedAction(this,
			function() {
				prcb.callback.run();
				this._runNextPostRenderCallback();
			}), prcb.delay);
	} else {
		if (appCtxt.isOffline) {	
			this.sendClientEventNotify(ZmZimbraMail.UI_LOAD_END);

			if (window.isNodeWebkit) {
				this._firstTimeNetworkChange = true;
				this.handleNetworkChange();
				window.addEventListener('offline', this.handleNetworkChange.bind(this));
				window.addEventListener('online', this.handleNetworkChange.bind(this));
			}

		}
	}
};

/**
 * @private
 */
ZmZimbraMail.prototype.handleNetworkChange =
function() {
	this._isNodeWebkitOnline = navigator.onLine;
	if (this._isUserOnline || this._firstTimeNetworkChange) {
		this._updateNetworkStatus(navigator.onLine);
	}
};

ZmZimbraMail.prototype._updateNetworkStatus =
function(online) {
	// bug 48108 - Prism sometimes triggers network status change mutliple times
	// So don't bother if the last change is the same as current status
	if ((online && this._currentNetworkStatus == ZmZimbraMail.UI_NETWORK_UP) ||
		(!online && this._currentNetworkStatus == ZmZimbraMail.UI_NETWORK_DOWN))
	{
		return;
	}

	if (online) {
		if (!this._firstTimeNetworkChange) {
			this.setStatusMsg(ZmMsg.networkChangeOnline);
		} else {
			this._firstTimeNetworkChange = false;
			this._isUserOnline = online;
		}
		this._currentNetworkStatus = ZmZimbraMail.UI_NETWORK_UP;
        this.sendClientEventNotify(this._currentNetworkStatus, true);
	} else {
		this.setStatusMsg(ZmMsg.networkChangeOffline, ZmStatusView.LEVEL_WARNING);
		this._currentNetworkStatus = ZmZimbraMail.UI_NETWORK_DOWN;
        this.sendClientEventNotify(this._currentNetworkStatus);
	}

	this._networkStatusIcon.setToolTipContent(online ? ZmMsg.networkStatusOffline : ZmMsg.networkStatusOnline);
	this._networkStatusIcon.getHtmlElement().innerHTML = AjxImg.getImageHtml(online ? "Connect" : "Disconnect");
	var netStatus = online ? ZmMsg.imStatusOnline : ZmMsg.imStatusOffline;
	this._networkStatusText.getHtmlElement().innerHTML = netStatus.substr(0, 1).toUpperCase() + netStatus.substr(1);
};

/**
 * @private
 */
ZmZimbraMail._handlePasswordLock =
function() {
	var passwordLockDialog = appCtxt.getPasswordLockDialog();
	passwordLockDialog.popup();
};

/**
 * Sets up a callback to be run after the starting app has rendered, if we're doing
 * post-render callbacks. The callback is registered with an order that determines
 * when it will run relative to other callbacks. A delay can also be given, so that
 * the UI has a chance to do some work between callbacks.
 *
 * @param {AjxCallback}	callback		the callback
 * @param {int}	order			the run order for the callback
 * @param {int}	delay			how long to pause before running the callback
 * @param {Boolean}	runNow		if <code>true</code>, we are not doing post-render callbacks, run the callback now and don't add it to the list
 */
ZmZimbraMail.prototype.addPostRenderCallback =
function(callback, order, delay, runNow) {
	if (!this._doingPostRenderStartup && runNow) {
		callback.run();
	} else {
		order = order || this._postRenderLast++;
		this._postRenderCallbacks.push({callback:callback, order:order, delay:delay || 0});
	}
};

/**
 * @private
 */
ZmZimbraMail.prototype._getStartApp =
function(params) {
	// determine starting app
	var startApp;
	var account = appCtxt.multiAccounts && appCtxt.accountList.mainAccount;
	if (params && params.app) {
		startApp = ZmApp.QS_ARG_R[params.app.toLowerCase()];
		// make sure app given in QS is actually enabled
		var setting = ZmApp.SETTING[startApp];
		var upsellSetting = ZmApp.UPSELL_SETTING[startApp];

		if (setting && !appCtxt.get(setting, null, account) && (!upsellSetting || !appCtxt.get(upsellSetting))) { // an app is valid if it's enabled or has its upsell enabled
			startApp = null;
		}
	}
	if (!startApp) {
		for (var app in ZmApp.DEFAULT_SORT) {
			ZmApp.DEFAULT_APPS.push(app);
		}
		ZmApp.DEFAULT_APPS.sort(function(a, b) {
			return ZmZimbraMail.hashSortCompare(ZmApp.DEFAULT_SORT, a, b);
		});
		var defaultStartApp = null;
		for (var i = 0; i < ZmApp.DEFAULT_APPS.length; i++) {
			var app = ZmApp.DEFAULT_APPS[i];
			var setting = ZmApp.SETTING[app];
			if (!setting || appCtxt.get(setting, null, account)) {
				defaultStartApp = app;
				break;
			}
		}
		startApp = this._getDefaultStartAppName(account);
	}

	// parse query string, in case we are coming in with a deep link	
	var qsParams = AjxStringUtil.parseQueryString();
	if (qsParams && qsParams.view && !qsParams.app) {
		startApp = ZmApp.QS_VIEWS[qsParams.view];
	}

	params.startApp = startApp;
	params.qsParams = qsParams;
};

/**
 * @private
 */
ZmZimbraMail.prototype._getDefaultStartAppName =
function(account) {
	account = account || (appCtxt.multiAccounts && appCtxt.accountList.mainAccount) || null;
	
	for (var app in ZmApp.DEFAULT_SORT) {
		ZmApp.DEFAULT_APPS.push(app);
	}
	ZmApp.DEFAULT_APPS.sort(function(a, b) {
		return ZmZimbraMail.hashSortCompare(ZmApp.DEFAULT_SORT, a, b);
	});
	var defaultStartApp = null;
	for (var i = 0; i < ZmApp.DEFAULT_APPS.length; i++) {
		var app = ZmApp.DEFAULT_APPS[i];
		var setting = ZmApp.SETTING[app];
		if (!setting || appCtxt.get(setting, null, account)) {
			return app;
		}
	}
}

/**
 * Performs a 'running restart' of the app by clearing state and calling the startup method.
 * This method is run after a logoff, or a change in what's supported.
 * 
 * @private
 */
ZmZimbraMail.prototype.restart =
function(settings) {
	// need to decide what to clean up, what to have startup load lazily
	// could have each app do shutdown()
	DBG.println(AjxDebug.DBG1, "RESTARTING APP");
	this.reset();
	this.startup({settingOverrides:settings});
};

/**
 * Resets the controller.
 * 
 */
ZmZimbraMail.prototype.reset =
function() {

	ZmCsfeCommand.clearSessionId();	// so we get a refresh block
	appCtxt.accountList.resetTrees();

	if (!appCtxt.rememberMe()) {
		appCtxt.getLoginDialog().clearAll();
	}
	for (var app in this._apps) {
		this._apps[app] = null;
	}
	this._activeApp = null;
	this._appViewMgr.reset();
};

/**
 * Cancels the request.
 * 
 * @param	{String}	reqId		the request id
 * @param	{AjxCallback}	errorCallback		the callback
 * @param	{Boolean}	noBusyOverlay	if <code>true</code>, do not show busy overlay
 * @see	ZmRequestMgr#cancelRequest
 */
ZmZimbraMail.prototype.cancelRequest =
function(reqId, errorCallback, noBusyOverlay) {
	this._requestMgr.cancelRequest(reqId, errorCallback, noBusyOverlay);
};

/**
 * Sends the request.
 * 
 * @param	{Hash}	params		a hash of parameters
 * @see	ZmRequestMgr#sendRequest
 */
ZmZimbraMail.prototype.sendRequest =
function(params) {
	return this._requestMgr.sendRequest(params);
};

/**
 * Runs the given function for all enabled apps, passing args.
 *
 * @param {String}	funcName		the function name
 * @param {Boolean}	force			if <code>true</code>, run func for disabled apps as well
 */
ZmZimbraMail.prototype.runAppFunction =
function(funcName, force) {
	var args = [];
	for (var i = 2; i < arguments.length; i++) {
		args.push(arguments[i]);
	}
	for (var i = 0; i < ZmApp.APPS.length; i++) {
		var appName = ZmApp.APPS[i];
		var setting = ZmApp.SETTING[appName];
		var account = appCtxt.multiAccounts && appCtxt.accountList.mainAccount;
		if (!setting || appCtxt.get(setting, null, account) || force) {
			var app = appCtxt.getApp(appName, null, account);
			var func = app && app[funcName];
			if (func && (typeof(func) == "function")) {
				func.apply(app, args);
			}
		}
	}
};

/**
 * Instantiates enabled apps. An optional argument may be given limiting the set
 * of apps that may be created.
 *
 * @param {Hash}	apps	the set of apps to create
 * 
 * @private
 */
ZmZimbraMail.prototype._createEnabledApps =
function(apps) {
    this._apps = {};

	for (var app in ZmApp.CLASS) {
		if (!apps || apps[app]) {
			ZmApp.APPS.push(app);
		}
	}
	ZmApp.APPS.sort(function(a, b) {
		return ZmZimbraMail.hashSortCompare(ZmApp.LOAD_SORT, a, b);
	});

	// Instantiate enabled apps, which will invoke app registration.
	// We also create "upsell" apps, which will only show the content of a URL in an iframe,
	// to encourage the user to upgrade.
	for (var i = 0; i < ZmApp.APPS.length; i++) {
		var app = ZmApp.APPS[i];
		var account = appCtxt.multiAccounts && appCtxt.accountList.mainAccount;
		var appEnabled = ZmApp.SETTING[app] && appCtxt.get(ZmApp.SETTING[app], null, account);
		var upsellEnabled = ZmApp.UPSELL_SETTING[app] && appCtxt.get(ZmApp.UPSELL_SETTING[app]);
		if (appEnabled || upsellEnabled) {
			ZmApp.ENABLED_APPS[app] = true;
			this._createApp(app);
			this._apps[app].isUpsell = (!appEnabled && upsellEnabled);
		}
	}
};

/**
 * Static function to add a listener before this class has been instantiated.
 * During construction, listeners are copied to the event manager. This function
 * could be used by a skin, for example.
 *
 * @param {constant}	type		the event type
 * @param {AjxListener}	listener	a listener
 */
ZmZimbraMail.addListener =
function(type, listener) {
	if (!ZmZimbraMail._listeners[type]) {
		ZmZimbraMail._listeners[type] = [];
	}
	ZmZimbraMail._listeners[type].push(listener);
};

/**
 * Static function to add an app listener before this class has been
 * instantiated. This is separate from {@link ZmZimbraMail#addListener}
 * so that the caller doesn't need to know the specifics of how we
 * twiddle the type name for app events.
 * 
 * @param	{String}	appName		the application name
 * @param {constant}	type		the event type
 * @param {AjxListener}	listener	a listener
 * 
 */
ZmZimbraMail.addAppListener =
function(appName, type, listener) {
	type = [appName, type].join("_");
	ZmZimbraMail.addListener(type, listener);
};

/**
 * Adds a listener for the given event type.
 *
 * @param {constant}	type		the event type
 * @param {AjxListener}	listener	a listener
 * @return	{Boolean}	<code>true</code> if the listener is added
 * 
 */
ZmZimbraMail.prototype.addListener =
function(type, listener) {
	return this._evtMgr.addListener(type, listener);
};

/**
 * Removes a listener for the given event type.
 *
 * @param {constant}	type		the event type
 * @param {AjxListener}	listener	a listener
 * @return	{Boolean}	<code>true</code> if the listener is removed
 */
ZmZimbraMail.prototype.removeListener =
function(type, listener) {
	return this._evtMgr.removeListener(type, listener);
};

/**
 * Adds a listener for the given event type and app.
 *
 * @param {constant}	app		the app name
 * @param {constant}	type		the event type
 * @param {AjxListener}	listener	a listener
 * @return	{Boolean}	<code>true</code> if the listener is added
 */
ZmZimbraMail.prototype.addAppListener =
function(app, type, listener) {
	type = [app, type].join("_");
	return this.addListener(type, listener);
};

/**
 * Removes a listener for the given event type and app.
 *
 * @param {constant}	app		the app name
 * @param {constant}	type		the event type
 * @param {AjxListener}	listener	a listener
 * @return	{Boolean}	<code>true</code> if the listener is removed
 */
ZmZimbraMail.prototype.removeAppListener =
function(app, type, listener) {
	type = [app, type].join("_");
	return this.removeListener(type, listener);
};

/**
 * Sends a <code>&lt;NoOpRequest&gt;</code> to the server. Used for '$set:noop'
 */
ZmZimbraMail.prototype.sendNoOp =
function() {
	var soapDoc = AjxSoapDoc.create("NoOpRequest", "urn:zimbraMail");
	var accountName = appCtxt.isOffline && appCtxt.accountList.mainAccount.name;
	this.sendRequest({soapDoc:soapDoc, asyncMode:true, noBusyOverlay:true, accountName:accountName});
};

/**
 * Sends a <code>&lt;ClientEventNotifyRequest&gt;</code> to the server.
 * 
 * @param	{Object}	event		the event
 */
ZmZimbraMail.prototype.sendClientEventNotify =
function(event, isNetworkOn) {
	var params = {
		jsonObj: {
			ClientEventNotifyRequest: {
				_jsns:"urn:zimbraOffline",
				e: event
			}
		},
		asyncMode:true
	};

    if (isNetworkOn) {
        params.callback = new AjxCallback(this, this.handleClientEventNotifyResponse, event);
        params.noBusyOverlay = true;

        if (this.clientEventNotifyReqId) {
            appCtxt.getRequestMgr().cancelRequest(this.clientEventNotifyReqId);
        }
        this.clientEventNotifyTimerId = 
            AjxTimedAction.scheduleAction(new AjxTimedAction(this, this.sendClientEventNotify, [event, true]), 3000);
    } else {
        params.callback = new AjxCallback(this, this.setInstantNotify, true);
    }

    this.clientEventNotifyReqId = this.sendRequest(params);
};

ZmZimbraMail.prototype.handleClientEventNotifyResponse =
function(event, res) {
    if (!res.isException() && res.getResponse()) {
        if (this.clientEventNotifyTimerId) {
            AjxTimedAction.cancelAction(this.clientEventNotifyTimerId);
            this.clientEventNotityTimerId = null;
        }
        this.setInstantNotify(true);
    }
};

/**
 * Sets the client into "instant notifications" mode.
 * 
 * @param {Boolean}	on				if <code>true</code>, turn on instant notify
 */
ZmZimbraMail.prototype.setInstantNotify =
function(on) {
	if (on) {
		this._pollInstantNotifications = true;
		// set nonzero poll interval so cant ever get into a full-speed request loop
		this._pollInterval = appCtxt.get(ZmSetting.INSTANT_NOTIFY_INTERVAL);
		if (this._pollActionId) {
			AjxTimedAction.cancelAction(this._pollActionId);
			this._pollActionId = null;
		}
		this._kickPolling(true);
	} else {
		this._pollInstantNotifications = false;
		this._cancelInstantNotify();
		this.setPollInterval(true);
	}
};

/**
 * Gets the "instant notification" setting.
 * 
 * @return	{Boolean}	<code>true</code> if instant notification is "ON"
 */
ZmZimbraMail.prototype.getInstantNotify =
function() {
	return this._pollInstantNotifications;
};

ZmZimbraMail.prototype.handleOfflineMailTo =
function(uri, callback) {
	//window.navigator does not have isRegisteredProtocolHandler method
	//if (window.platform && !window.platform.isRegisteredProtocolHandler("mailto")) { return false; }

	var mailApp = this.getApp(ZmApp.MAIL);
	var idx = (uri.indexOf("mailto"));
	if (idx >= 0) {
		var query = "to=" + decodeURIComponent(uri.substring(idx+7));
		query = query.replace(/\?/g, "&");

        // Remove extra double quote from end of the string
        query = query.replace('"', '');

		var controller = mailApp._showComposeView(callback, query);
        this._checkOfflineMailToAttachments(controller, query);

		return true;
	}

	return false;
};

ZmZimbraMail.prototype._checkOfflineMailToAttachments =
function(controller, queryStr) {
    var qs = queryStr || location.search;

    var match = qs.match(/\bto=([^&]+)/);
    var to = match ? AjxStringUtil.urlComponentDecode(match[1]) : null;

    match = qs.match(/\battachments=([^&]+)/);
    var attachments = match ? (AjxStringUtil.urlComponentDecode(match[1]).replace(/\+/g, " ")) : null;

    if (to && to.indexOf('mailto') == 0) {
        to = to.replace(/mailto:/,'');
        var mailtoQuery = to.split('?');
        if (mailtoQuery.length > 1) {
            mailtoQuery = mailtoQuery[1];
            match = mailtoQuery.match(/\battachments=([^&]+)/);
            if(!attachments) attachments = match ? (AjxStringUtil.urlComponentDecode(match[1]).replace(/\+/g, " ")) : null;
        }
    }

    if(attachments) {
        attachments = attachments.replace(/;$/, "");
        attachments = attachments.split(";");
        this._mailtoAttachmentsLength = attachments.length;
        this._attachmentsProcessed = 0;        
        this.attachment_ids = [];
        for(var i=0; i<attachments.length; i++) {
            this._handleMailToAttachment(attachments[i], controller);
        }
    }
};

ZmZimbraMail.prototype._handleMailToAttachment =
function(attachment, controller) {

    var filePath = attachment;
    var fileName = filePath.replace(/^.*\\/, '');

    DBG.println("Uploading File :" + fileName + ",filePath:" + filePath);

    var self = this,
        fs = require('fs');

    // Read file contents and send it to server for file upload
    fs.readFile(filePath, function(error, data) {
        if (error) {
            console.error('error reading file contents: ', error.code);
            return;
        }

        // FileUploadServlet.java is also detecting content type based on file extension
        var contentType = self._getAttachmentContentType(fileName);

        var req = new XMLHttpRequest();
        req.open('POST', appCtxt.get(ZmSetting.CSFE_UPLOAD_URI)+'&fmt=extended,raw', true);
        req.setRequestHeader('Cache-Control', 'no-cache');
        req.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
        req.setRequestHeader('Content-Type', (contentType || 'application/octet-stream'));
        // As per RFC 5987, use filename* field in header to send utf-8 filename, http://stackoverflow.com/a/20933751
        req.setRequestHeader('Content-Disposition', 'attachment; filename*=UTF-8\'\''+ encodeURIComponent(fileName));

        var reqObj = req;
        req.onreadystatechange = AjxCallback.simpleClosure(self._handleUploadResponse, self, reqObj, controller);
        req.send(data.buffer);

        delete req;
    });
};

ZmZimbraMail.prototype._getAttachmentContentType =
function(fileName) {
    var mime = require('mime-types');

    return mime.lookup(fileName);
};

ZmZimbraMail.prototype._handleUploadErrorResponse = function(respCode) {
    var warngDlg = appCtxt.getMsgDialog();
    var style = DwtMessageDialog.CRITICAL_STYLE;
    if (respCode == '200') {
        return true;
    } else if(respCode == '413') {
        warngDlg.setMessage(ZmMsg.errorAttachmentTooBig, style);
    } else {
       var msg = AjxMessageFormat.format(ZmMsg.errorAttachment, (respCode || AjxPost.SC_NO_CONTENT));
       warngDlg.setMessage(msg, style);
    }
    warngDlg.popup();
};

ZmZimbraMail.prototype._handleUploadResponse = function(req, controller) {
    if(req) {
        if(req.readyState == 4 && req.status == 200) {
            var resp = eval("["+req.responseText+"]");
            this._attachmentsProcessed++;
            this._handleUploadErrorResponse(resp[0]);
            if(resp.length > 2) {
                var respObj = resp[2];
                for (var i = 0; i < respObj.length; i++) {
                    if(respObj[i].aid != "undefined") {
                        this.attachment_ids.push(respObj[i].aid);
                    }
                }

                if(this.attachment_ids.length > 0 && this._attachmentsProcessed == this._mailtoAttachmentsLength) {
                    var attachment_list = this.attachment_ids.join(",");
                    if(!controller) {
                        var msg = new ZmMailMsg();
                        controller = AjxDispatcher.run("GetComposeController");
                        controller._setView({action:ZmOperation.NEW_MESSAGE, msg:msg, inNewWindow:false});
                    }
                    var callback = new AjxCallback (controller,controller._handleResponseSaveDraftListener);
        		    controller.sendMsg(attachment_list, ZmComposeController.DRAFT_TYPE_MANUAL,callback);
                    this.getAppViewMgr().pushView(controller.viewId);
                }
            }
        }
    }

};

/**
 * Resets the interval between poll requests, based on what's in the settings,
 * only if we are not in instant notify mode.
 *
 * @param {Boolean}	kickMe	if <code>true</code>, start the poll timer
 * @return	{Boolean}	<code>true</code> if poll interval started; <code>false</code> if in "instant notification" mode
 */
ZmZimbraMail.prototype.setPollInterval =
function(kickMe) {
	if (!this._pollInstantNotifications) {
		this._pollInterval = appCtxt.get(ZmSetting.POLLING_INTERVAL) * 1000;

		if (this._pollInterval) {
			DBG.println(AjxDebug.DBG1, "poll interval = " + this._pollInterval + "ms");
			if (kickMe)
				this._kickPolling(true);
		} else {
			// cancel timer if it is waiting...
			if (this._pollActionId) {
				AjxTimedAction.cancelAction(this._pollActionId);
				this._pollActionId = null;
			}
		}
		return true;
	} else {
		this._pollInterval = appCtxt.get(ZmSetting.INSTANT_NOTIFY_INTERVAL);
		DBG.println(AjxDebug.DBG1, "Ignoring Poll Interval (in instant-notify mode)");
		return false;
	}
};

/**
 * @private
 */
ZmZimbraMail.prototype._cancelInstantNotify =
function() {
	if (this._pollRequest) {
		this._requestMgr.cancelRequest(this._pollRequest);
		this._pollRequest = null;
	}

	if (this._pollActionId) {
		AjxTimedAction.cancelAction(this._pollActionId);
		this._pollActionId = null;
	}
};

/**
 * Make sure the polling loop is running.  Basic flow:
 *
 *       1) kickPolling():
 *             - cancel any existing timers
 *             - set a timer for _pollInterval time
 *             - call execPoll() when the timer goes off
 *
 *       2) execPoll():
 *             - make the NoOp request, if we're in "instant notifications"
 *               mode, this request will hang on the server until there is more data,
 *               otherwise it will return immediately.  Call into a handle() func below
 *
 *       3) handleDoPollXXXX():
 *             - call back to kickPolling() above
 *
 * resetBackoff = TRUE e.g. if we've just received a successful
 * response from the server, or if the user just changed our
 * polling settings and we want to start in fast mode
 * 
 * @private
 */
ZmZimbraMail.prototype._kickPolling =
function(resetBackoff) {
	DBG.println(AjxDebug.DBG2, [
		"ZmZimbraMail._kickPolling ",
		this._pollInterval, ", ",
		this._pollActionId, ", ",
		this._pollRequest ? "request_pending" : "no_request_pending"
	].join(""));

	// reset the polling timeout
	if (this._pollActionId) {
		AjxTimedAction.cancelAction(this._pollActionId);
		this._pollActionId = null;
	}

	if (resetBackoff && this._pollInstantNotifications) {
		// we *were* backed off -- reset the delay back to 1s fastness
		var interval = appCtxt.get(ZmSetting.INSTANT_NOTIFY_INTERVAL);
		if (this._pollInterval > interval) {
			this._pollInterval = interval;
		}
	}

	if (this._pollInterval && !this._pollRequest) {
		try {
			this._pollActionId = AjxTimedAction.scheduleAction(new AjxTimedAction(this, this._execPoll), this._pollInterval);
		} catch (ex) {
			this._pollActionId = null;
			DBG.println(AjxDebug.DBG1, "Caught exception in ZmZimbraMail._kickPolling.  Polling chain broken!");
		}
	}
};

/**
 * We've finished waiting, do the actual poll itself
 *
 * @private
 */
ZmZimbraMail.prototype._execPoll =
function() {
	this._cancelInstantNotify();

	// It'd be more efficient to make these instance variables, but for some
	// reason that breaks polling in IE.
	var soapDoc = AjxSoapDoc.create("NoOpRequest", "urn:zimbraMail");
	try {
        if (this._pollInstantNotifications) {
            var method = soapDoc.getMethod();
            method.setAttribute("wait", 1);
            method.setAttribute("limitToOneBlocked", 1);
            if (window.isNotifyDebugOn) {
                var str = appCtxt.getNotifyDebug();
                soapDoc.set("DEBUG", str);
                appCtxt.clearNotifyDebug();
            }
        }
		var params = {
			soapDoc: soapDoc,
			asyncMode: true,
			callback: new AjxCallback(this, this._handleResponseDoPoll),
			errorCallback: new AjxCallback(this, this._handleErrorDoPoll),
			noBusyOverlay: true,
			timeout: appCtxt.get(ZmSetting.INSTANT_NOTIFY_TIMEOUT),
			accountName: appCtxt.isOffline && appCtxt.accountList.mainAccount.name
		};
		this._pollRequest = this.sendRequest(params);

		// bug #42664 - handle case where sync-status-changes fall between 2 client requests
		if (appCtxt.isOffline &&
			!appCtxt.accountList.isInitialSyncing() &&
			appCtxt.accountList.isSyncStatus(ZmZimbraAccount.STATUS_RUNNING))
		{
			this.sendNoOp();
		}
	} catch (ex) {
		this._handleErrorDoPoll(ex); // oops!
	}
};

/**
 * @private
 */
ZmZimbraMail.prototype._handleErrorDoPoll =
function(ex) {
	if (this._pollRequest) {
		// reset the polling timeout
		if (this._pollActionId) {
			AjxTimedAction.cancelAction(this._pollActionId);
			this._pollActionId = null;
		}
		this._requestMgr.cancelRequest(this._pollRequest);
		this._pollRequest = null;
	}

	if (this._pollInstantNotifications) {
		// very simple-minded exponential backoff
		this._pollInterval *= 2;
		if (this._pollInterval > (1000 * 60 * 2)) {
			this._pollInterval = 1000 * 60 * 2;
		}
	}

	var isAuthEx = (ex.code == ZmCsfeException.SVC_AUTH_EXPIRED ||
					ex.code == ZmCsfeException.SVC_AUTH_REQUIRED ||
					ex.code == ZmCsfeException.NO_AUTH_TOKEN);

	// restart poll timer if we didn't get an auth exception
	if (!isAuthEx) {
		this._kickPolling(false);
	}

	return !isAuthEx;
};

/**
 * @private
 */
ZmZimbraMail.prototype._handleResponseDoPoll =
function(result) {
	this._pollRequest = null;
	var noopResult = result.getResponse().NoOpResponse;
	if (noopResult.waitDisallowed) {
		this._waitDisallowed = true;
		// revert to polling mode - server doesn't want us to use instant notify.
		this.setInstantNotify(false);
	}  else {
		// restart poll timer if we didn't get an exception
		this._kickPolling(true);
	}
};

/**
 * Gets the key map manager.
 * 
 * @return	{DwtKeyMapMgr}	the key map manager
 */
ZmZimbraMail.prototype.getKeyMapMgr =
function() {
	var kbMgr = appCtxt.getKeyboardMgr();
	if (!kbMgr.__keyMapMgr) {
		this._initKeyboardHandling();
	}
	return kbMgr.__keyMapMgr;
};

/**
 * @private
 */
ZmZimbraMail.prototype._initKeyboardHandling =
function() {
	var kbMgr = appCtxt.getKeyboardMgr();
	if (kbMgr.__keyMapMgr) { return; }
	if (appCtxt.get(ZmSetting.USE_KEYBOARD_SHORTCUTS)) {
		// Register our keymap and global key action handler with the shell's keyboard manager
		kbMgr.enable(true);
		kbMgr.registerKeyMap(new ZmKeyMap());
		kbMgr.pushDefaultHandler(this);
	} else {
		kbMgr.enable(false);
	}
};

/**
 * @private
 */
ZmZimbraMail.prototype._setupTabGroups =
function() {
	DBG.println(AjxDebug.DBG2, "SETTING SEARCH CONTROLLER TAB GROUP");
	var rootTg = appCtxt.getRootTabGroup();
	if (appCtxt.get(ZmSetting.SEARCH_ENABLED)) {
		rootTg.addMember(appCtxt.getSearchController().getTabGroup());
	}
	this._components[ZmAppViewMgr.C_APP_CHOOSER].noFocus = true;

	var curApp = appCtxt.getCurrentApp();
	var ovId = curApp && curApp.getOverviewId();
	var overview = ovId && appCtxt.getOverviewController().getOverview(ovId);
	if (overview) {
		rootTg.addMember(overview);
		ZmController._currentOverview = overview;
	}
	
	appCtxt.getKeyboardMgr().setTabGroup(rootTg);
};

/**
 * @private
 */
ZmZimbraMail.prototype._registerOrganizers =
function() {

	ZmOrganizer.registerOrg(ZmOrganizer.FOLDER,
							{app:				ZmApp.MAIL,
							 nameKey:			"folder",
							 defaultFolder:		ZmOrganizer.ID_INBOX,
							 soapCmd:			"FolderAction",
							 firstUserId:		256,
							 orgClass:			"ZmFolder",
							 orgPackage:		"MailCore",
							 treeController:	"ZmMailFolderTreeController",
							 labelKey:			"folders",
							 itemsKey:			"messages",
							 hasColor:			true,
							 defaultColor:		ZmOrganizer.C_NONE,
							 treeType:			ZmOrganizer.FOLDER,
							 dropTargets:		[ZmOrganizer.FOLDER],
							 views:				["message", "conversation"],
							 folderKey:			"mailFolder",
							 mountKey:			"mountFolder",
							 createFunc:		"ZmOrganizer.create",
							 compareFunc:		"ZmFolder.sortCompare",
							 newOp:				ZmOperation.NEW_FOLDER,
							 displayOrder:		100,
							 openSetting:		ZmSetting.FOLDER_TREE_OPEN
							});

	ZmOrganizer.registerOrg(ZmOrganizer.SEARCH,
							{app:				ZmApp.MAIN,
							 nameKey:			"savedSearch",
							 precondition:		ZmSetting.SAVED_SEARCHES_ENABLED,
							 soapCmd:			"FolderAction",
							 firstUserId:		256,
							 orgClass:			"ZmSearchFolder",
							 treeController:	"ZmSearchTreeController",
							 labelKey:			"searches",
							 treeType:			ZmOrganizer.FOLDER,
 							 dropTargets:		[ZmOrganizer.FOLDER, ZmOrganizer.SEARCH],
							 createFunc:		"ZmSearchFolder.create",
							 compareFunc:		"ZmFolder.sortCompare",
							 openSetting:		ZmSetting.SEARCH_TREE_OPEN,
							 displayOrder:		300
							});

    ZmOrganizer.registerOrg(ZmOrganizer.SHARE, {
        orgClass:       "ZmShareProxy",
        treeController: "ZmShareTreeController",
        labelKey:       "sharedFoldersHeader",
        compareFunc:	"ZmFolder.sortCompare",
        displayOrder:	101, // NOTE: Always show shares below primary folder tree
        hideEmpty:		false
    });

	ZmOrganizer.registerOrg(ZmOrganizer.TAG,
							{app:				ZmApp.MAIN,
							 nameKey:			"tag",
							 precondition:		ZmSetting.TAGGING_ENABLED,
							 soapCmd:			"TagAction",
							 firstUserId:		64,
							 orgClass:			"ZmTag",
							 treeController:	"ZmTagTreeController",
							 hasColor:			true,
							 defaultColor:		ZmOrganizer.C_ORANGE,
							 labelKey:			"tags",
							 treeType:			ZmOrganizer.TAG,
							 createFunc:		"ZmTag.create",
							 compareFunc:		"ZmTag.sortCompare",
							 newOp:				ZmOperation.NEW_TAG,
							 openSetting:		ZmSetting.TAG_TREE_OPEN,
							 displayOrder:		400
							});

	ZmOrganizer.registerOrg(ZmOrganizer.ZIMLET,
							{orgClass:			"ZmZimlet",
							 treeController:	"ZmZimletTreeController",
							 labelKey:			"zimlets",
							 compareFunc:		"ZmZimlet.sortCompare",
							 openSetting:		ZmSetting.ZIMLET_TREE_OPEN,
							 hideEmpty:			true
							});
	
	// Technically, we don't need to do this because the drop listeners for dragged organizers typically do their
	// own checks on the class of the dragged object. But it's better to do it anyway, in case it ever gets
	// validated within the drop target against the valid types.
	this._name = ZmApp.MAIN;
	ZmApp.prototype._setupDropTargets.call(this);
};

/**
 * Gets a handle to the given app.
 *
 * @param {String}	appName		the app name
 * @return	{ZmApp}	the app
 */
ZmZimbraMail.prototype.getApp =
function(appName) {
	if (!ZmApp.ENABLED_APPS[appName]) {
		return null;
	}
	if (!this._apps[appName]) {
		this._createApp(appName);
	}
	return this._apps[appName];
};

/**
 * Gets a handle to the app view manager.
 * 
 * @return	{ZmAppViewMgr}	the app view manager
 */
ZmZimbraMail.prototype.getAppViewMgr =
function() {
	return this._appViewMgr;
};

/**
 * Gets the active app.
 * 
 * @return	{ZmApp}	the app
 */
ZmZimbraMail.prototype.getActiveApp =
function() {
	return this._activeApp;
};

/**
 * Gets the previous application.
 * 
 * @return	{ZmApp}	the app
 */
ZmZimbraMail.prototype.getPreviousApp =
function() {
	return this._previousApp;
};

/**
 * Activates the given application.
 *
 * @param {constant}	appName		the application name
 * @param {Boolean}	force			if <code>true</code>, launch the app
 * @param {AjxCallback}	callback		the callback
 * @param {AjxCallback}	errorCallback	the error callback
 * @param {Hash}	params		a hash of parameters		(see {@link #startup} for full list)
 * @param {Boolean}	params.checkQS		if <code>true</code>, check query string for launch args
 * @param {ZmCsfeResult}	params.result		the result object from load of user settings
 */
ZmZimbraMail.prototype.activateApp =
function(appName, force, callback, errorCallback, params) {
	DBG.println(AjxDebug.DBG1, "activateApp: " + appName + ", current app = " + this._activeApp);

	var account = appCtxt.multiAccounts && appCtxt.accountList.mainAccount;
	var view = this._appViewMgr.getAppView(appName);
	if (view && !force) {
		// if the app has been launched, make its view the current one
		DBG.println(AjxDebug.DBG3, "activateApp, current " + appName + " view: " + view);
		if (this._appViewMgr.pushView(view)) {
			this._appViewMgr.setAppView(appName, view);
            if (!appCtxt.get(ZmApp.SETTING[appName], null, account) && appCtxt.get(ZmApp.UPSELL_SETTING[appName])) {
                var title = [ZmMsg.zimbraTitle, appName].join(": ");
                Dwt.setTitle(title);
            }            
		}
		if (callback) {
			callback.run();
		}
	} else {
		// launch the app
		if (!this._apps[appName]) {
			this._createApp(appName);
		}

		if (!appCtxt.get(ZmApp.SETTING[appName], null, account) &&
			appCtxt.get(ZmApp.UPSELL_SETTING[appName]))
		{
			this._createUpsellView(appName);
			if (callback) {
				callback.run();
			}
		}
		else
		{
			DBG.println(AjxDebug.DBG1, "Launching app " + appName);
			var respCallback = new AjxCallback(this, this._handleResponseActivateApp, [callback, appName]);
			var eventType = [appName, ZmAppEvent.PRE_LAUNCH].join("_");
			this._evt.item = this._apps[appName];
			this._evtMgr.notifyListeners(eventType, this._evt);
			params = params || {};
			params.searchResponse = this._searchResponse;
			this._apps[appName].launch(params, respCallback);
			delete this.searchResponse;
		}
	}
};

/**
 * @private
 */
ZmZimbraMail.prototype._handleResponseActivateApp =
function(callback, appName) {
	if (callback) {
		callback.run();
	}

	if (ZmApp.DEFAULT_SEARCH[appName]) {
		appCtxt.getSearchController().setDefaultSearchType(ZmApp.DEFAULT_SEARCH[appName]);
	}

	var eventType = [appName, ZmAppEvent.POST_LAUNCH].join("_");
	this._evt.item = this._apps[appName];
	this._evtMgr.notifyListeners(eventType, this._evt);
};

/**
 * Handles a change in which app is current. The change will be reflected in the
 * current app toolbar and the overview. The previous and newly current apps are
 * notified of the change. This method is called after a new view is pushed.
 *
 * @param {constant}	appName		the app
 * @param {constant}	view		the view
 * @param	{Boolean}	isTabView	if <code>true</code>, the app has a tab view
 */
ZmZimbraMail.prototype.setActiveApp =
function(appName, view, isTabView) {

	// update app chooser
	if (!isTabView) {
		this._components[ZmAppViewMgr.C_APP_CHOOSER].setSelected(appName);
	}

	// app not actually enabled if this is result of upsell view push
	var account = appCtxt.multiAccounts && appCtxt.accountList.mainAccount;
	var appEnabled = !ZmApp.SETTING[appName] || appCtxt.get(ZmApp.SETTING[appName], null, account);

	this._activeTabId = null;	// app is active; tab IDs are for non-apps

	if (this._activeApp != appName) {
		// deactivate previous app
	    if (this._activeApp) {
			// some views are not stored in _apps collection, so check if it exists.
			var app = this._apps[this._activeApp];
			if (app) {
				app.activate(false, view);
			}
			this._previousApp = this._activeApp;
		}

		// switch app
		this._activeApp = appName;
		if (appEnabled) {
			var app = this._apps[this._activeApp];

			if (appCtxt.get(ZmSetting.SEARCH_ENABLED)) {
				var searchType = app ? app.getInitialSearchType() : null;
				if (!searchType) {
					searchType = ZmApp.DEFAULT_SEARCH[appName];
				}
				if (searchType) {
					appCtxt.getSearchController().setDefaultSearchType(searchType);
				}

				// set search string value to match current app's last search, if applicable
				var stb = appCtxt.getSearchController().getSearchToolbar();
				if (appCtxt.get(ZmSetting.SHOW_SEARCH_STRING) && stb) {
					var value = app.currentSearch ? app.currentSearch.query : app.currentQuery;
					stb.setSearchFieldValue(value || "");
				}
			}

			// activate current app - results in rendering of overview
			if (app) {
				if (appCtxt.inStartup && this._doingPostRenderStartup) {
					var callback = new AjxCallback(this,
						function() {
							app.activate(true);
						});
					this.addPostRenderCallback(callback, 1, 100, true);
				} else {
					app.activate(true);
				}
			}
		}
		this._evt.item = this._apps[appName];
		this._evtMgr.notifyListeners(ZmAppEvent.ACTIVATE, this._evt);
	}
	else if (this._activeApp && this._apps[this._activeApp]) {
		this._apps[this._activeApp].stopAlert();
	}
};

/**
 * Gets the app chooser button.
 * 
 * @param	{String}	id		the id
 * @return	{ZmAppButton}	the button
 */
ZmZimbraMail.prototype.getAppChooserButton =
function(id) {
	return this._components[ZmAppViewMgr.C_APP_CHOOSER].getButton(id);
};

/**
 * An app calls this once it has fully rendered, so that we may notify
 * any listeners.
 * 
 * @param	{String}	appName		the app name
 */
ZmZimbraMail.prototype.appRendered =
function(appName) {
	var eventType = [appName, ZmAppEvent.POST_RENDER].join("_");
	this._evtMgr.notifyListeners(eventType, this._evt);

	if (window._facadeCleanup) {
		window._facadeCleanup();
		window._facadeCleanup = null;
	}
};

/**
 * Adds the application.
 * 
 * @param	{ZmApp}		app		the app
 */
ZmZimbraMail.prototype.addApp = function(app) {
	var appName = app.getName();
	this._apps[appName] = app;
	ZmApp.ENABLED_APPS[appName] = true;
};

// Private methods

/**
 * Creates an app object, which doesn't necessarily do anything just yet.
 * 
 * @private
 */
ZmZimbraMail.prototype._createApp =
function(appName) {
	if (!appName || this._apps[appName]) return;
	DBG.println(AjxDebug.DBG1, "Creating app " + appName);
	var appClass = eval(ZmApp.CLASS[appName]);
	this.addApp(new appClass(this._shell));
};

/**
 * @private
 */
ZmZimbraMail.prototype._setExternalLinks =
function() {
	var el = document.getElementById("skin_container_links");
	if (el) {
		// bug: 41313 - admin console link
		var adminUrl;
		if (!appCtxt.isOffline &&
			(appCtxt.get(ZmSetting.IS_ADMIN) ||
			 appCtxt.get(ZmSetting.IS_DELEGATED_ADMIN)))
		{
			adminUrl = appCtxt.get(ZmSetting.ADMIN_REFERENCE);
			if (!adminUrl) {
				adminUrl = ["https://", location.hostname, ":7071"].join("");
			}
		}

		var data = {
			showOfflineLink: (!appCtxt.isOffline && appCtxt.get(ZmSetting.SHOW_OFFLINE_LINK)),
			helpIcon: (appCtxt.getSkinHint("helpButton", "hideIcon") ? null : "Help"),
			logoutIcon: (appCtxt.getSkinHint("logoutButton", "hideIcon") ? null : "Logoff"),
			logoutText: (appCtxt.isOffline ? ZmMsg.setup : ZmMsg.logOff),
			adminUrl: adminUrl
		};
		el.innerHTML = AjxTemplate.expand("share.App#UserInfo", data);
	}
	
	el = document.getElementById("skin_container_help_button");
	if (el) {
		this._helpButton = this.getHelpButton(DwtShell.getShell(window));
		this._helpButton.reparentHtmlElement("skin_container_help_button");
	}
};


ZmZimbraMail.ONLINE_HELP_URL = "http://help.zimbra.com/?";
ZmZimbraMail.NEW_FEATURES_URL = "http://www.zimbra.com/docs/whats-new/?";

/**
* Adds a "help" submenu.
*
* @param {DwtComposite}		parent		the parent widget
* @return {ZmActionMenu}	the menu
*/
ZmZimbraMail.prototype.getHelpButton =
function(parent) {

	var button = new DwtLinkButton({parent: parent, className: DwtButton.LINK_BUTTON_CLASS});
	button.dontStealFocus();
	button.setSize(Dwt.DEFAULT);
	button.setAlign(DwtLabel.ALIGN_LEFT);
	button.setText(ZmMsg.help);
	var menu = new ZmPopupMenu(button);

	var helpListener = new AjxListener(this, this._helpListener);
	button.addSelectionListener(helpListener);

	var mi = menu.createMenuItem("documentation", {text: ZmMsg.productHelp});
	mi.addSelectionListener(helpListener);

	var mi = menu.createMenuItem("onlinehelp", {text: ZmMsg.onlineHelp});
	mi.addSelectionListener(new AjxListener(this, this._onlineHelpListener));


	mi = menu.createMenuItem("newFeatures", {text: ZmMsg.newFeatures});
	mi.addSelectionListener(new AjxListener(this, this._newFeaturesListener));

	menu.createSeparator();

	mi = menu.createMenuItem("about", {text: ZmMsg.about});
	mi.addSelectionListener(new AjxListener(this, this._aboutListener));

	button.setMenu(menu);
	return button;
};

ZmZimbraMail.prototype._helpListener =
function(ev) {
	ZmZimbraMail.helpLinkCallback();
};


ZmZimbraMail.prototype._getVersion =
function() {
	return appCtxt.get(ZmSetting.CLIENT_VERSION);
};


ZmZimbraMail.prototype._getQueryParams =
function() {

	var appName = appCtxt.getCurrentAppName().toLowerCase();
	var prod = appCtxt.isOffline ? "zd" : "zcs";
	return ["utm_source=", appName, "&utm_medium=", prod, "&utm_content=", this._getVersion(), "&utm_campaign=help"].join("");
};


ZmZimbraMail.prototype._onlineHelpListener =
function(ev) {
	ZmZimbraMail.unloadHackCallback();
	var url = [ZmZimbraMail.ONLINE_HELP_URL, this._getQueryParams()].join("");
	window.open(url);
};

ZmZimbraMail.prototype._newFeaturesListener =
function(ev) {
	ZmZimbraMail.unloadHackCallback();
	var url = [ZmZimbraMail.NEW_FEATURES_URL, this._getQueryParams()].join("");
	window.open(url);
};

ZmZimbraMail.prototype._aboutListener =
function(ev) {
	var dialog = appCtxt.getMsgDialog();
	dialog.reset();
	var version = this._getVersion();
	var release = appCtxt.get(ZmSetting.CLIENT_RELEASE);
	var aboutMsg = appCtxt.isOffline ? ZmMsg.aboutMessageZD : ZmMsg.aboutMessage;
	dialog.setMessage(AjxMessageFormat.format(aboutMsg, [version, release]), DwtMessageDialog.INFO_STYLE, ZmMsg.about);
	dialog.popup();

};


ZmZimbraMail.prototype._initOfflineUserInfo =
function() {
	var htmlElId = this._userNameField.getHTMLElId();
	this._userNameField.getHtmlElement().innerHTML = AjxTemplate.expand('share.App#NetworkStatus', {id:htmlElId});
	this._userNameField.addClassName("BannerTextUserOffline");

	var params = {
		parent: this._userNameField,
		parentElement: (htmlElId+"_networkStatusIcon")
	};
	this._networkStatusIcon = new DwtComposite(params);

	var params1 = {
		parent: this._userNameField,
		parentElement: (htmlElId+"_networkStatusText")
	};
	this._networkStatusText = new DwtComposite(params1);

	var topTreeEl = document.getElementById("skin_container_tree_top");
	if (topTreeEl) {
		Dwt.setSize(topTreeEl, Dwt.DEFAULT, "20");
	}
};

/**
 * Sets the user info.
 *
 */
ZmZimbraMail.prototype.setUserInfo =
function() {
	if (appCtxt.isOffline) { return; }

	// username
	var login = appCtxt.getLoggedInUsername();
	var username = (appCtxt.get(ZmSetting.DISPLAY_NAME)) || login;
	if (username) {
		this._userNameField.getHtmlElement().innerHTML =  AjxStringUtil.htmlEncode(AjxStringUtil.clipByLength(username, 24));
		if (AjxEnv.isLinux) {	// bug fix #3355
			this._userNameField.getHtmlElement().style.lineHeight = "13px";
		}
	}

    this.setQuotaInfo(login, username);
};

ZmZimbraMail.prototype.setQuotaInfo =
function(login, username) {
    var quota = appCtxt.get(ZmSetting.QUOTA);
	var usedQuota = (appCtxt.get(ZmSetting.QUOTA_USED)) || 0;
	var data = {
		id: this._usedQuotaField._htmlElId,
		login: login,
		username: username,
		quota: quota,
		usedQuota: usedQuota,
		size: (AjxUtil.formatSize(usedQuota, false, 1))
	};

	var quotaTemplateId;
	if (data.quota) {
		quotaTemplateId = 'UsedLimited';
		data.limit = AjxUtil.formatSize(data.quota, false, 1);
		data.percent = Math.min(Math.round((data.usedQuota / data.quota) * 100), 100);
		data.desc = AjxMessageFormat.format(ZmMsg.quotaDescLimited, [data.percent+'%', data.limit]);
	}
    else {
		data.desc = AjxMessageFormat.format(ZmMsg.quotaDescUnlimited, [data.size]);
		quotaTemplateId = 'UsedUnlimited';
	}
    this._usedQuotaField.getHtmlElement().innerHTML = AjxTemplate.expand('share.Quota#'+quotaTemplateId, data);
	// tooltip for username/quota fields
	var html = AjxTemplate.expand('share.Quota#Tooltip', data);
	this._components[ZmAppViewMgr.C_USER_INFO].setToolTipContent(html);
	this._components[ZmAppViewMgr.C_QUOTA_INFO].setToolTipContent(html);
};

/**
 * If a user has been prompted and elects to stay on page, this timer automatically logs them off after an interval of time.
 * @param startTimer {boolean} true to start timer, false to cancel
 */
ZmZimbraMail.setExitTimer = 
function(startTimer) {
	if (startTimer && ZmZimbraMail.stayOnPagePrompt) {
		DBG.println(AjxDebug.DBG1, "user has clicked stay on page. scheduled exit timer at " + new Date().toLocaleString());
		if (ZmZimbraMail._exitTimerId == -1) {
			ZmZimbraMail._exitTimerId = AjxTimedAction.scheduleAction(ZmZimbraMail._exitTimer, ZmZimbraMail.STAYONPAGE_INTERVAL * 60 * 1000); //give user 2 minutes
			if (AjxEnv.isFirefox) {
				var msg = AjxMessageFormat.format(ZmMsg.appExitPrompt, [ZmZimbraMail.STAYONPAGE_INTERVAL]);
				var msgDialog = appCtxt.getMsgDialog();
				msgDialog.setMessage(msg, DwtMessageDialog.CRITICAL_STYLE); //Firefox 4+ doesn't allow custom stay on page message. Prompt user they have X minutes
				//wait 2 seconds before popping up  so FF doesn't show dialog when leave page is clicked
				setTimeout(function() { msgDialog.popup()}, 1000 * 2);
			}
		}

	}
	else if (!startTimer && ZmZimbraMail._exitTimerId) {
		DBG.println(AjxDebug.DBG1, "canceling exit timer at " + new Date().toLocaleString());
		AjxTimedAction.cancelAction(ZmZimbraMail._exitTimerId);
		ZmZimbraMail._exitTimerId = -1;
	}
	
};

// Listeners

/**
 * Logs off the application.
 * 
 */
ZmZimbraMail.logOff =
function(ev, relogin) {
	ZmZimbraMail._isLogOff = true;

	var urlParams = {
		path: appContextPath,
		qsArgs: {
			loginOp: relogin ? 'relogin' : 'logout',
			// pass localeId in url so jsp pages can use to set locale
			localeId: appCtxt.get(ZmSetting.LOCALE_NAME)
		}
	};

	if (relogin) {
		urlParams.qsArgs.username = appCtxt.getLoggedInUsername();
	}

	var url = AjxUtil.formatUrl(urlParams);
	ZmZimbraMail.sendRedirect(url);	// will trigger onbeforeunload
	if (AjxEnv.isFirefox) {
		DBG.println(AjxDebug.DBG1, "calling setExitTimer from logoff "  + new Date().toLocaleString());
		ZmZimbraMail.setExitTimer(true);	
	}
};

/**
 * Logs user off when session has expired and user has choosen to stay on page when prompted
 */
ZmZimbraMail.exitSession =
function() {
	DBG.println(AjxDebug.DBG1, "exit timer called  " + new Date().toLocaleString());
	ZmZimbraMail.logOff();
};

ZmZimbraMail.executeSessionTimer = 
function() {
	ZmZimbraMail.sessionTimerInvoked = true;
	DBG.println(AjxDebug.DBG1, "session timer invoked  " + new Date().toLocaleString());
	ZmZimbraMail.logOff();
};


/**
 * @private
 */
ZmZimbraMail._onClickLogOff =
function() {
	if (AjxEnv.isIE) {
		// Don't the the default <a> handler process the event. It can bring up
		// an unwanted "Are you sure you want to exit?" dialog.
		var ev = DwtUiEvent.getEvent();
		ev.returnValue = false;
	}
	DBG.println(AjxDebug.DBG1, "ZmZimbraMail._onClickLogOff : invoking logout");
	ZmZimbraMail.logOff();
};

/**
 * @private
 */
ZmZimbraMail.helpLinkCallback =
function() {
	ZmZimbraMail.unloadHackCallback();

	var ac = window.parentAppCtxt || window.appCtxt;
	var url;
	if (!ac.isOffline) {
		try { url = skin.hints.helpButton.url; } catch (e) { /* ignore */ }
		url = url || ac.get(ZmSetting.HELP_URI);
		var sep = url.match(/\?/) ? "&" : "?";
		url = [url, sep, "locid=", AjxEnv.DEFAULT_LOCALE].join("");
	} else {
		url = ac.get(ZmSetting.HELP_URI).replace(/\/$/,"");
		// bug fix #35098 - offline help is only available in en_US for now
		url = [url, "help", "en_US", "Zimbra_Mail_Help.htm"].join("/");
//		url = [url, "help", AjxEnv.DEFAULT_LOCALE, "Zimbra_Mail_Help.htm"].join("/");
	}
	window.open(url);
};

/**
 * Sends a redirect.
 * 
 * @param	{String}	locationStr		the redirect location
 */
ZmZimbraMail.sendRedirect =
function(locationStr) {
	// not sure why IE doesn't allow this to process immediately, but since
	// it does not, we'll set up a timed action.
	if (AjxEnv.isIE) {
		var act = new AjxTimedAction(null, ZmZimbraMail.redir, [locationStr]);
		AjxTimedAction.scheduleAction(act, 1);
	} else {
		ZmZimbraMail.redir(locationStr);
	}
};

/**
 * Redirect.
 * 
 * @param	{String}	locationStr		the redirect location
 */
ZmZimbraMail.redir =
function(locationStr){
	// IE has a tendency to throw a mysterious error when the "are you sure" dialog pops up and the user presses "cancel".
	// Pressing cancel, however, equals doing nothing, so we can just catch the exception and ignore it (bug #59853)
	try {
		window.location = locationStr;
	} catch (e) {
	}
};

/**
 * Sets the session timer.
 * 
 * @param	{Boolean}	bStartTimer		if <code>true</code>, start the timer
 */
ZmZimbraMail.prototype.setSessionTimer =
function(bStartTimer) {

	// if no timeout value, user's client never times out from inactivity
	var timeout = appCtxt.get(ZmSetting.IDLE_SESSION_TIMEOUT) * 1000;
	if (timeout <= 0) {
		return;
	}

	if (bStartTimer) {
		DBG.println(AjxDebug.DBG3, "INACTIVITY TIMER SET (" + (new Date()).toLocaleString() + ")");
		this._sessionTimerId = AjxTimedAction.scheduleAction(this._sessionTimer, timeout);

		DwtEventManager.addListener(DwtEvent.ONMOUSEUP, ZmZimbraMail._userEventHdlr);
		this._shell.setHandler(DwtEvent.ONMOUSEUP, ZmZimbraMail._userEventHdlr);
		if (AjxEnv.isIE)
			this._shell.setHandler(DwtEvent.ONMOUSEDOWN, ZmZimbraMail._userEventHdlr);
		else
			window.onkeydown = ZmZimbraMail._userEventHdlr;
	} else {
		DBG.println(AjxDebug.DBG3, "INACTIVITY TIMER CANCELED (" + (new Date()).toLocaleString() + ")");

		AjxTimedAction.cancelAction(this._sessionTimerId);
		this._sessionTimerId = -1;

		DwtEventManager.removeListener(DwtEvent.ONMOUSEUP, ZmZimbraMail._userEventHdlr);
		this._shell.clearHandler(DwtEvent.ONMOUSEUP);
		if (AjxEnv.isIE)
			this._shell.clearHandler(DwtEvent.ONMOUSEDOWN);
		else
			window.onkeydown = null;
	}
};

/**
 * Adds a child window.
 * 
 * @private
 */
ZmZimbraMail.prototype.addChildWindow =
function(childWin) {
	if (this._childWinList == null) {
		this._childWinList = new AjxVector();
	}

	// NOTE: we now save childWin w/in Object so other params can be added to it.
	// Otherwise, Safari breaks (see http://bugs.webkit.org/show_bug.cgi?id=7162)
	var newWinObj = {win:childWin};
	this._childWinList.add(newWinObj);

	return newWinObj;
};

/**
 * Gets a child window.
 * 
 * @private
 */
ZmZimbraMail.prototype.getChildWindow =
function(childWin) {
	if (this._childWinList) {
		for (var i = 0; i < this._childWinList.size(); i++) {
			if (childWin == this._childWinList.get(i).win) {
				return this._childWinList.get(i);
			}
		}
	}
	return null;
};

/**
 * Removes a child window.
 * 
 * @private
 */
ZmZimbraMail.prototype.removeChildWindow =
function(childWin) {
	if (this._childWinList) {
		for (var i = 0; i < this._childWinList.size(); i++) {
			if (childWin == this._childWinList.get(i).win) {
				this._childWinList.removeAt(i);
				break;
			}
		}
	}
};

/**
 * Checks for a certain type of exception, then hands off to standard
 * exception handler.
 *
 * @param {AjxException}	ex				the exception
 * @param {Object}	continuation		the original request params
 * 
 * @private
 */
ZmZimbraMail.prototype._handleException =
function(ex, continuation) {
	var handled = false;
	if (ex.code == ZmCsfeException.MAIL_NO_SUCH_FOLDER) {
		// check for fault when getting folder perms
		var organizerTypes = [ZmOrganizer.CALENDAR, ZmOrganizer.NOTEBOOK, ZmOrganizer.ADDRBOOK];
		if (ex.data.itemId && ex.data.itemId.length) {
			var itemId = ex.data.itemId[0];
			var index = itemId.lastIndexOf(':');
			var zid = itemId.substring(0, index);
			var rid = itemId.substring(index + 1, itemId.length);
			var ft = appCtxt.getFolderTree();
			for (var type = 0; type < organizerTypes.length; type++) {
				handled |= ft.handleNoSuchFolderError(organizerTypes[type], zid, rid, true);
			}
		}
	}
	if (!handled) {
		ZmController.prototype._handleException.apply(this, arguments);
	}
};

/**
 * This method is called by the window.onbeforeunload handler
 * 
 * @private
 */
ZmZimbraMail._confirmExitMethod =
function() {

	if (!ZmCsfeCommand.noAuth) {
		appCtxt.accountList.saveImplicitPrefs();

		if (appCtxt.get(ZmSetting.WARN_ON_EXIT) && !ZmZimbraMail._isOkToExit()) {
			if (ZmZimbraMail.stayOnPagePrompt) {
				DBG.println(AjxDebug.DBG1, "user has already been prompted. Forcing exit " + new Date().toLocaleString());
				return;
			}
			
			ZmZimbraMail._isLogOff = false;
			DBG.println(AjxDebug.DBG1, "prompting to user to stay on page or leave " + new Date().toLocaleString());
			var msg = (appCtxt.isOffline) ? ZmMsg.appExitWarningZD : ZmMsg.appExitWarning;
			
			if (ZmZimbraMail.sessionTimerInvoked) {
				ZmZimbraMail.stayOnPagePrompt = true;
				msg = AjxMessageFormat.format(msg + ZmMsg.appExitTimeWarning, [ZmZimbraMail.STAYONPAGE_INTERVAL]); //append time warning
			}
			if (!AjxEnv.isFirefox) {
				DBG.println(AjxDebug.DBG1, "calling setExitTimer  "  + new Date().toLocaleString());
				ZmZimbraMail.setExitTimer(true);
			}
			return msg;
			
		}

		ZmZimbraMail._endSession();
	}
	
	ZmZimbraMail._endSessionDone = true;
};

/**
 * Returns true if there is no unsaved work. If that's the case, it also
 * cancels any pending poll. Typically called by onbeforeunload handling.
 * 
 * @private
 */
ZmZimbraMail._isOkToExit =
function() {
	var appCtlr = window._zimbraMail;
	if (!appCtlr) { return true; }
	var okToExit = appCtlr._appViewMgr.isOkToUnload() && ZmZimbraMail._childWindowsOkToUnload();
	if (okToExit && !AjxEnv.isPrism && appCtlr._pollRequest) {
		appCtlr._requestMgr.cancelRequest(appCtlr._pollRequest);
	}
	return okToExit;
};

// returns true if no child windows are dirty
ZmZimbraMail._childWindowsOkToUnload =
function() {
	var childWinList = window._zimbraMail ? window._zimbraMail._childWinList : null;
	if (childWinList) {
		for (var i = 0; i < childWinList.size(); i++) {
			var childWin = childWinList.get(i);
			if (childWin.win.ZmNewWindow._confirmExitMethod()) {
				return false;
			}
		}
	}
	return true;
};

ZmZimbraMail.handleNetworkStatusClick =
function() {
	var ac = window["appCtxt"].getAppController();

	// if already offline, then ignore this click
	if (!ac._isNodeWebkitOnline) { return; }

	ac._isUserOnline = !ac._isUserOnline;
	ac._updateNetworkStatus(ac._isUserOnline);
};

/**
 * @private
 */
ZmZimbraMail.unloadHackCallback =
function() {
	window.onbeforeunload = null;
	var f = function() { window.onbeforeunload = ZmZimbraMail._confirmExitMethod; };
	AjxTimedAction.scheduleAction((new AjxTimedAction(null, f)), 3000);
};

/**
 * @private
 */
ZmZimbraMail._userEventHdlr =
function(ev) {
	var zm = window._zimbraMail;
	if (zm) {
		// cancel old timer and start a new one
		AjxTimedAction.cancelAction(zm._sessionTimerId);
		var timeout = appCtxt.get(ZmSetting.IDLE_SESSION_TIMEOUT) * 1000;
		if (timeout <= 0) {
			return;
		}
		zm._sessionTimerId = AjxTimedAction.scheduleAction(zm._sessionTimer, timeout);
	}
	DBG.println(AjxDebug.DBG3, "INACTIVITY TIMER RESET (" + (new Date()).toLocaleString() + ")");
};

/**
 * @private
 */
ZmZimbraMail.prototype._createBanner =
function() {
	var banner = new DwtComposite({parent:this._shell, posStyle:Dwt.ABSOLUTE_STYLE, id:ZmId.BANNER});
	var logoUrl = appCtxt.getSkinHint("banner", "url") || appCtxt.get(ZmSetting.LOGO_URI);
	var data = {url:logoUrl, isOffline:appCtxt.isOffline};
	banner.getHtmlElement().innerHTML  = AjxTemplate.expand('share.App#Banner', data);
	return banner;
};


/**
 * @private
 */
ZmZimbraMail.prototype._createPasswordLock =
function() {
	var el = Dwt.byId(ZmId.PASSSWORD_LOCK);
	var data = {
		toolTip: ZmMsg.passwordLockToolTip
	};
	el.innerHTML = AjxTemplate.expand('share.App#PasswordLock', data);

	if (!appCtxt.get(ZmSetting.PASSWORD_LOCK_ENABLED)) {
		//Hide the div if the password lock feature is not enabled
		Dwt.setVisibility(el, false);
	}
};


/**
 * @private
 */
ZmZimbraMail.prototype._createUserInfo =
function(className, cid, id) {
	var position = appCtxt.getSkinHint(cid, "position");
	var posStyle = position || Dwt.ABSOLUTE_STYLE;
	var ui = new DwtComposite({parent:this._shell, className:className, posStyle:posStyle, id:id});
	ui._setMouseEventHdlrs();
	return ui;
};

/**
 * @private
 */
ZmZimbraMail.prototype._createAppChooser =
function() {

	var buttons = [];
	for (var id in ZmApp.CHOOSER_SORT) {
		if (id == ZmAppChooser.SPACER || id == ZmAppChooser.B_HELP || id == ZmAppChooser.B_LOGOUT) {
			continue;
		}

		var account = appCtxt.multiAccounts && appCtxt.accountList.mainAccount;
		var setting = ZmApp.SETTING[id];
		var upsellSetting = ZmApp.UPSELL_SETTING[id];
		if ((setting && appCtxt.get(setting, null, account)) || (upsellSetting && appCtxt.get(upsellSetting))) {
			buttons.push(id);
		}
	}
	buttons.sort(function(a, b) {
		return ZmZimbraMail.hashSortCompare(ZmApp.CHOOSER_SORT, a, b);
	});

	var appChooser = new ZmAppChooser({parent:this._shell, buttons:buttons, id:ZmId.APP_CHOOSER, refElementId:ZmId.SKIN_APP_CHOOSER});

	var buttonListener = new AjxListener(this, this._appButtonListener);
	appChooser.addSelectionListener(buttonListener);

	return appChooser;
};

/**
 * @private
 */
ZmZimbraMail.prototype._appButtonListener =
function(ev) {
	try {
		var id = ev.item.getData(Dwt.KEY_ID);
		DBG.println(AjxDebug.DBG1, "ZmZimbraMail button press: " + id);
		if (id == ZmAppChooser.B_HELP) {
			window.open(appCtxt.get(ZmSetting.HELP_URI));
		} else if (id == ZmAppChooser.B_LOGOUT) {
			DBG.println(AjxDebug.DBG1, "ZmZimbraMail : invoking logout.")
			ZmZimbraMail.logOff();
		} else if (id && ZmApp.ENABLED_APPS[id] && (id != this._activeTabId)) {
			this.activateApp(id);
			if (appCtxt.zimletsPresent()) {
				appCtxt.getZimletMgr().notifyZimlets("onSelectApp", id);
			}
		} else {
			if (id != this._activeTabId) {
				this._appViewMgr.pushView(id);
			}
			if (ev.target && (ev.target.className == "ImgClose")) {
				this._appViewMgr.popView();
			}
		}
	} catch (ex) {
		this._handleException(ex);
	}
};

/**
 * Gets the application chooser.
 * 
 * @return	{ZmAppChooser}	the chooser
 */
ZmZimbraMail.prototype.getAppChooser =
function() {
	return this._appChooser;
};

/**
 * Sets the active tab.
 * 
 * @param	{String}	id		the tab id
 */
ZmZimbraMail.prototype.setActiveTabId =
function(id) {
	this._activeTabId = id;
	this._appChooser.setSelected(id);
};

/**
 * Displays a status message.
 * 
 * @param	{Hash}	params		a hash of parameters
 * @param {String}	params.msg		the message
 * @param {constant}	[params.level] ZmStatusView.LEVEL_INFO, ZmStatusView.LEVEL_WARNING, or ZmStatusView.LEVEL_CRITICAL
 * @param {constant}	[params.detail] 	the details
 * @param {constant}	[params.transitions]		the transitions
 * @param {constant}	[params.toast]		the toast control 
 * @param {boolean}     [force]        force any displayed toasts out of the way (dismiss them and run their dismissCallback). Enqueued messages that are not yet displayed will not be displayed
 * @param {AjxCallback}    [dismissCallback]    callback to run when the toast is dismissed (by another message using [force], or explicitly calling ZmStatusView.prototype.dismiss())
 * @param {AjxCallback}    [finishCallback]     callback to run when the toast finishes its transitions by itself (not when dismissed)
 */
ZmZimbraMail.prototype.setStatusMsg =
function(params) {
	params = Dwt.getParams(arguments, ZmStatusView.MSG_PARAMS);
	this.statusView.setStatusMsg(params);
};

/**
 * Dismisses the displayed status message, if any
 */

ZmZimbraMail.prototype.dismissStatusMsg =
function(all) {
	this.statusView.dismissStatusMsg(all);
};

/**
 * Gets the key map name.
 * 
 * @return	{String}	the key map name
 */
ZmZimbraMail.prototype.getKeyMapName =
function() {
	var ctlr = appCtxt.getCurrentController();
	if (ctlr && ctlr.getKeyMapName) {
		return ctlr.getKeyMapName();
	}
	return "Global";
};

/**
 * Handles the key action.
 * 
 * @param	{constant}		actionCode		the action code
 * @param	{Object}	ev		the event
 * @see		ZmApp.ACTION_CODES_R
 * @see		ZmKeyMap
 */
ZmZimbraMail.prototype.handleKeyAction =
function(actionCode, ev) {

	DwtMenu.closeActiveMenu();

	var app = ZmApp.GOTO_ACTION_CODE_R[actionCode];
	if (app) {
		if (app == this.getActiveApp()) { return false; }
		this.activateApp(app);
		return true;
	}

	// don't honor plain Enter in an input field as an app shortcut, since it often
	// equates to button press in that situation
	if (ev && (ev.keyCode == 13 || ev.keyCode == 3) &&
		!(ev.altKey || ev.ctrlKey || ev.metaKey || ev.shiftKey) &&
		 ev.target && (ev.target.id != DwtKeyboardMgr.FOCUS_FIELD_ID)) { return false; }

	switch (actionCode) {
		case ZmKeyMap.DBG_NONE:
			appCtxt.setStatusMsg("Setting Debug Level To: " + AjxDebug.NONE);
			DBG.setDebugLevel(AjxDebug.NONE);
			break;

		case ZmKeyMap.DBG_1:
			appCtxt.setStatusMsg("Setting Debug Level To: " + AjxDebug.DBG1);
			DBG.setDebugLevel(AjxDebug.DBG1);
			break;

		case ZmKeyMap.DBG_2:
			appCtxt.setStatusMsg("Setting Debug Level To: " + AjxDebug.DBG2);
			DBG.setDebugLevel(AjxDebug.DBG2);
			break;

		case ZmKeyMap.DBG_3:
			appCtxt.setStatusMsg("Setting Debug Level To: " + AjxDebug.DBG3);
			DBG.setDebugLevel(AjxDebug.DBG3);
			break;

		case ZmKeyMap.DBG_TIMING: {
			var on = DBG._showTiming;
			var newState = on ? "off" : "on";
			appCtxt.setStatusMsg("Turning Timing Info " + newState);
			DBG.showTiming(!on);
			break;
		}

		case ZmKeyMap.ASSISTANT: {
			if (appCtxt.get(ZmSetting.ASSISTANT_ENABLED)) {
				if (!this._assistantDialog) {
					AjxDispatcher.require("Assistant");
					this._assistantDialog = new ZmAssistantDialog();
				}
				this._assistantDialog.popup();
			}
			break;
		}

		case ZmKeyMap.QUICK_REMINDER: {
            var account = appCtxt.multiAccounts && appCtxt.accountList.mainAccount;
            if (appCtxt.get(ZmSetting.CALENDAR_ENABLED, null, account)) {
                var calMgr = appCtxt.getCalManager();
                calMgr.showQuickReminder();
            }
			break;
		}

		case ZmKeyMap.LOGOFF: {
			DBG.println(AjxDebug.DBG1, "ZmZimbraMail.prototype.handleKeyAction:matched ZmKeyMap.LOGOFF, invoking logout");
			ZmZimbraMail.logOff();
			break;
		}

		case ZmKeyMap.FOCUS_SEARCH_BOX: {
			var stb = appCtxt.getSearchController().getSearchToolbar();
			if (stb) {
				var searchBox = stb.getSearchField();
				appCtxt.getKeyboardMgr().grabFocus(searchBox);
				if (ZmSearchAutocomplete) {
					ZmSearchAutocomplete._ignoreNextKey = true;
				}
			}
			break;
		}

		case ZmKeyMap.FOCUS_CONTENT_PANE: {
			this.focusContentPane();
			break;
		}

		case ZmKeyMap.FOCUS_TOOLBAR: {
			this.focusToolbar();
			break;
		}

		case ZmKeyMap.UNDO: {
			if (!appCtxt.isChildWindow) {
				var actionController = appCtxt.getActionController();
				if (actionController)
					actionController.undoCurrent();
			}
			break;
		}

		case ZmKeyMap.SHORTCUTS: {

			var panel = appCtxt.getShortcutsPanel();
			var curMap = this.getKeyMapName();
			var km = appCtxt.getAppController().getKeyMapMgr();
			var maps = km.getAncestors(curMap);
			var inherits = (maps && maps.length > 0);
			maps.unshift(curMap);
			var maps1 = [], maps2 = [];
			if (inherits) {
				if (maps.length > 1 && maps[maps.length - 1] == "Global") {
					maps.pop();
					maps2.push("global");
				}
			}
			for (var i = 0; i < maps.length; i++) {
				maps1.push(ZmKeyMap.MAP_NAME_R[maps[i]] || DwtKeyMap.MAP_NAME_R[maps[i]]);
			}

			var col1 = {}, col2 = {};
			col1.type = ZmShortcutList.TYPE_APP;
			col1.maps = maps1;
			var colList = [col1];
			if (maps2.length) {
				col2.type = ZmShortcutList.TYPE_APP;
				col2.maps = maps2;
				colList.push(col2);
			}
			var col3 = {};
			col3.type = ZmShortcutList.TYPE_SYS;
			col3.maps = [];
			var ctlr = appCtxt.getCurrentController();
			var testMaps = ["list", "editor", "tabView"];
			for (var i = 0; i < testMaps.length; i++) {
				if (ctlr && ctlr.mapSupported(testMaps[i])) {
					col3.maps.push(testMaps[i]);
				}
			}
			col3.maps.push("button", "menu", "tree", "dialog", "toolbarHorizontal");
			colList.push(col3);
			panel.popup(colList);
			break;
		}

		// this action needs to be last
		case ZmKeyMap.CANCEL: {
			// see if there's a current drag operation we can cancel
			var handled = false;
			var captureObj = (DwtMouseEventCapture.getId() == "DwtControl") ? DwtMouseEventCapture.getCaptureObj() : null;
			var obj = captureObj && captureObj.targetObj;
			if (obj && (obj._dragging == DwtControl._DRAGGING)) {
				captureObj.release();
				obj.__lastDestDwtObj = null;
				obj._setDragProxyState(false);					// turn dnd icon red so user knows no drop is happening
				DwtControl.__badDrop(obj, DwtShell.mouseEvent);	// shell's mouse ev should have latest info
				handled = true;
			}
			if (handled) { break; }
		}

		default: {
			var ctlr = appCtxt.getCurrentController();
			return (ctlr && ctlr.handleKeyAction)
				? ctlr.handleKeyAction(actionCode, ev)
				: false;
		}
	}
	return true;
};

/**
 * Focuses on the content pane.
 * 
 */
ZmZimbraMail.prototype.focusContentPane =
function() {
	// Set focus to the list view that's in the content pane. If there is no
	// list view in the content pane, nothing happens. The list view will be
	// found in the root tab group hierarchy.
	var ctlr = appCtxt.getCurrentController();
	var content = ctlr ? ctlr.getCurrentView() : null;
	if (content) {
		appCtxt.getKeyboardMgr().grabFocus(content);
	}
};

/**
 * Focuses on the toolbar.
 * 
 */
ZmZimbraMail.prototype.focusToolbar =
function() {
	// Set focus to the toolbar that's in the content pane.
	var ctlr = appCtxt.getCurrentController();
	var toolbar = ctlr ? ctlr.getCurrentToolbar() : null;
	if (toolbar) {
		appCtxt.getKeyboardMgr().grabFocus(toolbar);
	}
};

/**
 * Creates an "upsell view", which is a placeholder view for an app that's not
 * enabled but which has a button so that it can be promoted. The app will have
 * a URL for its upsell content, which we put into an IFRAME.
 *
 * @param {constant}	appName	the name of app
 * 
 * @private
 */
ZmZimbraMail.prototype._createUpsellView =
function(appName) {
	var viewName = [appName, "upsell"].join("_");
	if (!this._upsellView[appName]) {
		var upsellView = this._upsellView[appName] = new ZmUpsellView({parent:this._shell, posStyle:Dwt.ABSOLUTE_STYLE, className: 'ZmUpsellView'});
		var upsellUrl = appCtxt.get(ZmApp.UPSELL_URL[appName]);
		var el = upsellView.getHtmlElement();
		var htmlArr = [];
		var idx = 0;
		htmlArr[idx++] = "<iframe id='iframe_" + upsellView.getHTMLElId() + "' width='100%' height='100%' frameborder='0' src='";
		htmlArr[idx++] = upsellUrl;
		htmlArr[idx++] = "'>";
		el.innerHTML = htmlArr.join("");
		var elements = {};
		elements[ZmAppViewMgr.C_APP_CONTENT_FULL] = upsellView;
		var callbacks = {}
		callbacks[ZmAppViewMgr.CB_POST_SHOW] = new AjxCallback(this, this._displayUpsellView);
		this._appViewMgr.createView({viewId:viewName, appName:appName, elements:elements, isTransient:true, callbacks:callbacks});
	}
	this._appViewMgr.pushView(viewName);
};

ZmZimbraMail.prototype._displayUpsellView =
function(appName) {
	var title = [ZmMsg.zimbraTitle, appName].join(": ");
	Dwt.setTitle(title);
	appCtxt.getApp(this._getDefaultStartAppName()).setOverviewPanelContent(false);
};

/**
 * Sets up Zimlet organizer type. This is run if we get zimlets in the
 * GetInfoResponse. Note that this will run before apps are instantiated,
 * which is necessary because they depend on knowing whether there are zimlets.
 * 
 * @private
 */
ZmZimbraMail.prototype._postLoadZimlet =
function() {
	appCtxt.setZimletsPresent(true);
};

/**
 * @private
 */
ZmZimbraMail.prototype._globalSelectionListener =
function(ev) {
	// bug 47514
	if (this._waitDisallowed) {
		this._waitDisallowed = false;
		this.setInstantNotify(true);
	}

	if (!appCtxt.areZimletsLoaded()) { return; }

	var item = ev.item;

	// normalize action
	var text = (item && item.getText) ? (item.getText() || item._toggleText) : null;
	if (item && !text) {
		text = item.getData(ZmOperation.KEY_ID) || item.getData(Dwt.KEY_ID);
	}
	if (text) {
		var type;
		if (item instanceof ZmAppButton) {
			type = "app";
		} else if (item instanceof DwtMenuItem) {
			type = "menuitem";
		} else if (item instanceof DwtButton) {
			type = "button";
		} else if (item instanceof DwtTreeItem) {
			if (!item.getSelected()) { return; }
			type = "treeitem";
		} else {
			type = item.toString();
		}

		var avm = appCtxt.getAppViewMgr();
		var currentViewId = avm.getCurrentViewId();
		var lastViewId = avm.getLastViewId();
		var action = (AjxStringUtil.split((""+text), " ")).join("");
		appCtxt.notifyZimlets("onAction", [type, action, currentViewId, lastViewId]);
	}
};

/**
 * @private
 */
ZmZimbraMail._endSession =
function() {
	if (!AjxEnv.isPrism) {
		// Let the server know that the session is ending.
		var errorCallback = new AjxCallback(null, function() { return true; } ); // Ignores any error.
		var args = {
			jsonObj: { EndSessionRequest: { _jsns: "urn:zimbraAccount" } },
			asyncMode: true,
			errorCallback: errorCallback
		};
		appCtxt.getAppController().sendRequest(args);
	}
};

// YUCK:
ZmOrganizer.ZIMLET = "ZIMLET";

/**
 * Decides if password lock feature is to be shown to the user
 *
 * @return	{bool}  True if password lock feature is to be shown to the user
 */
ZmZimbraMail.showPasswordLockFeature =
function () {
	if (!appCtxt.get(ZmSetting.FORCE_DISABLE_PASSWORD_LOCK)
		&& appCtxt.isOffline
		&& appCtxt.accountList.accountTypeExists(ZmAccount.TYPE_ZIMBRA)) {
		return true;
	}
	return false;
};
