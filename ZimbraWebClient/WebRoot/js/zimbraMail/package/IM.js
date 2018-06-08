/*
 * 
 */
/*
 * Package: IM
 *
 * Supports: The IM (chat) application
 *
 * Loaded:
 *    - When the user goes to the IM application
 *    - Upon incoming chat message
 *    - Right-click contact -> New IM
 *    - New Chat (in the New menu)
 *    - Show floating buddy list (in the New menu)
 */

AjxPackage.require("ajax.dwt.widgets.DwtButtonColorPicker");
AjxPackage.require("zimbraMail.share.view.htmlEditor.ZmLiteHtmlEditor");
AjxPackage.require("ajax.dwt.widgets.DwtSoundPlugin");

AjxPackage.require("zimbraMail.share.view.dialog.ZmPromptDialog");

AjxPackage.require("zimbraMail.im.model.ZmAssistantBuddy");

AjxPackage.require("zimbraMail.im.view.ZmChatWidget");
AjxPackage.require("zimbraMail.im.view.ZmImNewChatDlg");
AjxPackage.require("zimbraMail.im.view.ZmCustomStatusDlg");

AjxPackage.require("zimbraMail.im.view.popup.ZmTaskbarPopup");
AjxPackage.require("zimbraMail.im.view.popup.ZmChatPopup");
AjxPackage.require("zimbraMail.im.view.popup.ZmNewBuddyPopup");
AjxPackage.require("zimbraMail.im.view.popup.ZmSubscribePopup");
AjxPackage.require("zimbraMail.im.view.popup.ZmPresencePopup");
AjxPackage.require("zimbraMail.im.view.popup.ZmGatewayPopup");
AjxPackage.require("zimbraMail.im.view.popup.ZmBuddyListPopup");

AjxPackage.require("zimbraMail.im.controller.ZmImController");
