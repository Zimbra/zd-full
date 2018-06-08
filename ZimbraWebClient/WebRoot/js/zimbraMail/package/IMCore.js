/*
 * 
 */
/*
 * Package: IM
 * 
 * Supports: The IM (chat) application
 * 
 * Loaded: upon IM notifications
 */

AjxPackage.require("ajax.dwt.events.DwtIdleTimer");
AjxPackage.require("ajax.util.AjxPluginDetector");
 
AjxPackage.require("zimbraMail.im.model.ZmImService");
AjxPackage.require("zimbraMail.im.model.ZmZimbraImService");
AjxPackage.require("zimbraMail.im.model.ZmImGateway");
AjxPackage.require("zimbraMail.im.model.ZmRoster");
AjxPackage.require("zimbraMail.im.model.ZmRosterItem");
AjxPackage.require("zimbraMail.im.model.ZmRosterItemList");
AjxPackage.require("zimbraMail.im.model.ZmRosterPresence");
AjxPackage.require("zimbraMail.im.model.ZmChat");
AjxPackage.require("zimbraMail.im.model.ZmChatList");
AjxPackage.require("zimbraMail.im.model.ZmChatMessage");
AjxPackage.require("zimbraMail.im.model.ZmImPrivacyList");

AjxPackage.require("zimbraMail.im.controller.ZmImServiceController");
AjxPackage.require("zimbraMail.im.controller.ZmZimbraImServiceController");
