/*
 * 
 */

com_zimbra_example_sampleprops_HandlerObject = function() {
};
com_zimbra_example_sampleprops_HandlerObject.prototype = new ZmZimletBase;
com_zimbra_example_sampleprops_HandlerObject.prototype.constructor = com_zimbra_example_sampleprops_HandlerObject;


/**
 * This method is called by the Zimlet framework when a menu item is selected.
 * 
 */
com_zimbra_example_sampleprops_HandlerObject.prototype.menuItemSelected = 
function(itemId) {
	var str = this.getMessage("helloworld_status");
	switch (itemId) {
		case "sampleprops_menuItemId":
			appCtxt.getAppController().setStatusMsg(str);
			break;
	}
};