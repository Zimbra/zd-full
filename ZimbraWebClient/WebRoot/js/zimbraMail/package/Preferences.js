/*
 * 
 */
/*
 * Package: Preferences
 * 
 * Supports: The Options (preferences) application
 * 
 * Loaded:
 * 	- When the user goes to the Options application
 * 	- When the user creates a filter rule from message headers
 */
AjxPackage.require("zimbraMail.prefs.model.ZmFilterRule");
AjxPackage.require("zimbraMail.prefs.model.ZmFilterRules");
AjxPackage.require("zimbraMail.prefs.model.ZmLocale");
AjxPackage.require("zimbraMail.prefs.model.ZmMobileDevice");

AjxPackage.require("zimbraMail.mail.model.ZmIdentity");
AjxPackage.require("zimbraMail.mail.model.ZmIdentityCollection");
AjxPackage.require("zimbraMail.mail.model.ZmDataSource");
AjxPackage.require("zimbraMail.mail.model.ZmDataSourceCollection");
AjxPackage.require("zimbraMail.mail.model.ZmPopAccount");
AjxPackage.require("zimbraMail.mail.model.ZmImapAccount");
AjxPackage.require("zimbraMail.mail.model.ZmSignature");
AjxPackage.require("zimbraMail.mail.model.ZmSignatureCollection");

AjxPackage.require("zimbraMail.prefs.view.ZmPreferencesPage");
AjxPackage.require("zimbraMail.prefs.view.ZmShortcutsPage");
AjxPackage.require("zimbraMail.prefs.view.ZmBackupPage");
AjxPackage.require("zimbraMail.prefs.view.ZmPrefView");
AjxPackage.require("zimbraMail.prefs.view.ZmFilterRulesView");
AjxPackage.require("zimbraMail.prefs.view.ZmFilterRuleDialog");
AjxPackage.require("zimbraMail.prefs.view.ZmZimletsPage");
AjxPackage.require("zimbraMail.prefs.view.ZmMobileDevicesPage");
AjxPackage.require("zimbraMail.prefs.view.ZmSharingPage");
AjxPackage.require("zimbraMail.prefs.view.ZmFilterPage");
AjxPackage.require("zimbraMail.prefs.view.ZmNotificationsPage");
AjxPackage.require("zimbraMail.prefs.view.ZmAutoArchivePage");
AjxPackage.require("zimbraMail.calendar.view.ZmCalendarPrefsPage");

AjxPackage.require("zimbraMail.mail.view.prefs.ZmAccountsPage");
AjxPackage.require("zimbraMail.mail.view.prefs.ZmAccountTestDialog");
AjxPackage.require("zimbraMail.mail.view.prefs.ZmMailPrefsPage");
AjxPackage.require("zimbraMail.mail.view.prefs.ZmSignaturesPage");
AjxPackage.require("zimbraMail.mail.view.prefs.ZmTrustedPage");

AjxPackage.require("zimbraMail.im.view.prefs.ZmImGatewayControl");

AjxPackage.require("zimbraMail.prefs.controller.ZmPrefController");
AjxPackage.require("zimbraMail.prefs.controller.ZmFilterController");
AjxPackage.require("zimbraMail.prefs.controller.ZmFilterRulesController");
AjxPackage.require("zimbraMail.prefs.controller.ZmMobileDevicesController");

AjxPackage.require("zimbraMail.share.controller.ZmProgressController");
