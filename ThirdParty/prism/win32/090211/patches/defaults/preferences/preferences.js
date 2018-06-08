//@line 2 "c:\mozilla\workdir\mozilla\192src\prism\defaults\preferences\preferences.js"
/*
//@line 39 "c:\mozilla\workdir\mozilla\192src\prism\defaults\preferences\preferences.js"
*/

/*
# 
*/

/*
* Portions Copyright (c) VMware, Inc. [1998 - 2011]. All Rights Reserved.
*/

pref("toolkit.defaultChromeURI", "chrome://webrunner/content/webrunner.xul");  // - main xul window
pref("browser.chromeURL", "chrome://webrunner/content/webrunner.xul");         // - allow popup windows to open
pref("toolkit.singletonWindowType", "navigator:browser");

pref("general.useragent.extra.prism", "Prism zdesktop/@version@");

/* prefwindow prefs (see: MDC - Preferences System and bug 350528) */
pref("browser.preferences.animateFadeIn", "false");
pref("browser.preferences.instantApply", "false");

/* debugging prefs */
pref("browser.dom.window.dump.enabled", true);
pref("javascript.options.showInConsole", true);
pref("javascript.options.strict", true);
pref("nglayout.debug.disable_xul_cache", false);
pref("nglayout.debug.disable_xul_fastload", false);

/* default security dialogs like firefox */
pref("security.warn_entering_secure.show_once", false);
pref("security.warn_leaving_secure.show_once", false);
pref("security.warn_submit_insecure.show_once", false);

/* disable warnings when opening external links */
pref("network.protocol-handler.warn-external.http", false);
pref("network.protocol-handler.warn-external.https", false);
pref("network.protocol-handler.warn-external.ftp", false);

/* use system proxy settings */
pref("network.proxy.type", 5);

/* download manager */
pref("browser.download.useDownloadDir", false);
pref("browser.download.folderList", 2);
pref("browser.download.manager.showAlertOnComplete", true);
pref("browser.download.manager.showAlertInterval", 2000);
pref("browser.download.manager.retention", 2);
pref("browser.download.manager.showWhenStarting", true);
pref("browser.download.manager.useWindow", true);
pref("browser.download.manager.closeWhenDone", false);
pref("browser.download.manager.openDelay", 0);
pref("browser.download.manager.focusWhenStarting", false);
pref("browser.download.manager.flashCount", 2);
pref("browser.download.manager.displayedHistoryDays", 7);

/* for preferences */
pref("browser.download.show_plugins_in_list", true);
pref("browser.download.hide_plugins_without_extensions", true);

/* download alerts */
pref("alerts.slideIncrement", 1);
pref("alerts.slideIncrementTime", 10);
pref("alerts.totalOpenTime", 6000);
pref("alerts.height", 50);

/* password manager */
pref("signon.rememberSignons", false);
pref("signon.expireMasterPassword", false);
pref("signon.SignonFileName", "signons.txt");

/* autocomplete */
pref("browser.formfill.enable", true);

/* spellcheck */
pref("layout.spellcheckDefault", 1);

/* extension manager and xpinstall */
pref("xpinstall.dialog.confirm", "chrome://mozapps/content/xpinstall/xpinstallConfirm.xul");
pref("xpinstall.dialog.progress.skin", "chrome://mozapps/content/extensions/extensions.xul?type=themes");
pref("xpinstall.dialog.progress.chrome", "chrome://mozapps/content/extensions/extensions.xul?type=extensions");
pref("xpinstall.dialog.progress.type.skin", "Extension:Manager-themes");
pref("xpinstall.dialog.progress.type.chrome", "Extension:Manager-extensions");
pref("extensions.update.enabled", true);
pref("extensions.update.interval", 86400);
pref("extensions.dss.enabled", false);
pref("extensions.dss.switchPending", false);
pref("extensions.ignoreMTimeChanges", false);
pref("extensions.logging.enabled", false);

/* NB these point at AMO */
pref("extensions.update.url", "chrome://mozapps/locale/extensions/extensions.properties");
pref("extensions.getMoreExtensionsURL", "chrome://mozapps/locale/extensions/extensions.properties");
pref("extensions.getMoreThemesURL", "chrome://mozapps/locale/extensions/extensions.properties");

/* findbar support */
pref("accessibility.typeaheadfind", true);
pref("accessibility.typeaheadfind.timeout", 5000);
pref("accessibility.typeaheadfind.flashBar", 1);
pref("accessibility.typeaheadfind.linksonly", false);
pref("accessibility.typeaheadfind.casesensitive", 0);

/* enable xul error pages */
pref("browser.xul.error_pages.enabled", false);

/* SSL error page behaviour */
pref("browser.ssl_override_behavior", 2);
pref("browser.xul.error_pages.expert_bad_cert", false);

/* Prism-specific prefs */
pref("prism.shortcut.aboutConfig.enabled", false);
pref("prism.shortcut.fullScreen.disabled", true);

// increase timeout for slow javascripts
pref("dom.max_script_run_time", 15);

// increase connections to avoid multiple zimlets hanging javascripts
pref("network.http.max-connections", 24);
pref("network.http.max-connections-per-server", 8);
pref("network.http.max-persistent-connections-per-proxy", 8);
pref("network.http.max-persistent-connections-per-server", 8);

// update service stuff
//pref("app.update.lastUpdateDate.background-update-timer", 0);
pref("app.update.enabled", true); //enables or disables the background update checker; default=true
pref("app.update.auto", false); //enables background download of updates according to the policy described by app.update.mode; default=true
pref("app.update.mode", 2); //defines the policy by which background downloads are done; 0=download all; 1=download compatible; 2=download minor and prompt for major; default=1
pref("app.update.silent", false); //disables all notification UI for updates; default=false

pref("app.update.timer", 600000); //the number of milliseconds between checker update interval expiry checks; default=600000
pref("app.update.interval", 86400); //the number of seconds between service XML pings; default=86400
pref("app.update.idletime", 60); //the number of seconds of idle time that must pass before displaying a pending software update dialog; default=60
pref("app.update.nagTimer.download", 86400); //the number of seconds after user dismissal to wait before prompting to download (if no background download); default=86400
pref("app.update.nagTimer.restart", 86400); //the number of seconds after user dismissal to wait before prompting to restart (if background download); default=86400

pref("app.update.channel", "@channel@");
pref("app.update.url", "https://www.zimbra.com/aus/zdesktop2/update.php?chn=%CHANNEL%&ver=@version@&bid=@buildid@&bos=@buildos@");
pref("app.update.url.manual", "http://www.zimbra.com/products/desktop.html");
pref("app.update.url.details", "http://www.zimbra.com/products/desktop.html");
pref("app.update.log.all", true);
