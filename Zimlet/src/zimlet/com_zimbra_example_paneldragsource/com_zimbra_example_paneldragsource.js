/*
 * 
 */


/**
 * Defines the Zimlet handler class.
 *   
 */
function com_zimbra_example_paneldragsource_HandlerObject() {
}

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_paneldragsource_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_paneldragsource_HandlerObject.prototype.constructor = com_zimbra_example_paneldragsource_HandlerObject;

/**
 * This method gets called by the Zimlet framework when the zimlet loads.
 *  
 */
com_zimbra_example_paneldragsource_HandlerObject.prototype.init =
function() {
	// do something
};


/**
 * This method gets called by the Zimlet framework when an item or items are dropped on the panel.
 * 
 * @param	obj		the dropped object
 */
com_zimbra_example_paneldragsource_HandlerObject.prototype.doDrop =
function(obj) {

	var type = obj.TYPE;
	switch(type) {
		case "ZmAppt": {
			// do something with ZmAppt
			break;
		}
		case "ZmContact": {
			// do something with ZmContact
			break;
		}
		case "ZmConv": {
			// do something with ZmConv
			break;
		}
		case "ZmMailMsg": {
			// do something with ZmMailMsg
			break;
		}
	}

};

