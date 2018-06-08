/*
 * 
 */
/*
 * Package: Mail
 * 
 * Supports: The Mail application
 * 
 * Loaded:
 * 	- When composing a message
 *  - To attach a file
 *  - When viewing a single msg or conv
 */
AjxPackage.require("ajax.dwt.events.DwtIdleTimer");

AjxPackage.require("zimbraMail.mail.view.ZmComposeView");
AjxPackage.require("zimbraMail.mail.view.ZmConvView");
AjxPackage.require("zimbraMail.mail.view.ZmMailAssistant");
AjxPackage.require("zimbraMail.mail.view.ZmAttachmentsView");
AjxPackage.require("zimbraMail.mail.view.ZmMailConfirmView");
AjxPackage.require("zimbraMail.mail.view.ZmSelectAddrDialog");

AjxPackage.require("zimbraMail.mail.controller.ZmComposeController");
AjxPackage.require("zimbraMail.mail.controller.ZmMsgController");
AjxPackage.require("zimbraMail.mail.controller.ZmConvController");
AjxPackage.require("zimbraMail.mail.controller.ZmAttachmentsController");
AjxPackage.require("zimbraMail.mail.controller.ZmMailConfirmController");
