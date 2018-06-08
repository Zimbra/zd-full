/*
 * 
 */

/**
 * Defines the Zimlet handler class.
 *   
 */
function com_zimbra_example_menuitemsjs_HandlerObject() {
}

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_menuitemsjs_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_menuitemsjs_HandlerObject.prototype.constructor = com_zimbra_example_menuitemsjs_HandlerObject;

/**
 * This method gets called by the Zimlet framework when the zimlet loads.
 *  
 */
com_zimbra_example_menuitemsjs_HandlerObject.prototype.init =
function() {
	// do something
};

/**
 * This method gets called by the Zimlet framework when a context menu item is selected.
 * 
 * @param	itemId		the Id of selected menu item
 */
com_zimbra_example_menuitemsjs_HandlerObject.prototype.menuItemSelected =
function(itemId) {
	switch (itemId) {
		case "SOME_MENU_ITEM_ID1":
			window.open ("http://www.yahoo.com",
					"mywindow","menubar=1,resizable=1,width=800,height=600"); 
			break;
		case "SOME_MENU_ITEM_ID2":
			window.open ("http://sports.yahoo.com",
					"mywindow","menubar=1,resizable=1,width=800,height=600"); 
			break;
		default:
			// do nothing
			break;
	}

};
