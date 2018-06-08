/*
 * 
 */

com_zimbra_example_toolbarhook_HandlerObject = function() {
};
com_zimbra_example_toolbarhook_HandlerObject.prototype = new ZmZimletBase;
com_zimbra_example_toolbarhook_HandlerObject.prototype.constructor = com_zimbra_example_toolbarhook_HandlerObject;

/**
 * This method gets called by the Zimlet framework when a toolbar is created.
 * 
 * http://files.zimbra.com/docs/zimlet/zcs/6.0/jsdocs/symbols/ZmZimletBase.html#initializeToolbar
 */
com_zimbra_example_toolbarhook_HandlerObject.prototype.initializeToolbar =
function(app, toolbar, controller, viewId) {

    if (viewId == ZmId.VIEW_CONVLIST || viewId == ZmId.VIEW_TRAD) {
        // get the index of "View" menu so we can display the button after that
        var buttonIndex = 0;
        for (var i = 0; i < toolbar.opList.length; i++) {
                if (toolbar.opList[i] == ZmOperation.VIEW_MENU) {
                        buttonIndex = i + 1;
                        break;
                }
        }

        var buttonParams = {
                text    : "Toolbar Button",
                tooltip: "This button shows up in Conversation view, traditional view, and in convlist view",
                index: buttonIndex, // position of the button
                image: "zimbraicon" // icon
        };

        // creates the button with an id and params containing the button details
        var button = toolbar.createOp("HELLOTEST_ZIMLET_TOOLBAR_BUTTON", buttonParams);
        button.addSelectionListener(new AjxListener(this, this._showSelectedMail, controller));   
    }
};

/**
 * Shows the selected mail.
 * 
 */
com_zimbra_example_toolbarhook_HandlerObject.prototype._showSelectedMail =
function(controller) {

	var message = controller.getMsg();

	appCtxt.getAppController().setStatusMsg("Subject:"+ message.subject);
};
