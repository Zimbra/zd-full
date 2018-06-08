/*
 * 
 */


/**
 * Defines the Zimlet handler class.
 *   
 */
function com_zimbra_example_panelitemclicked_HandlerObject() {
}

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_panelitemclicked_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_panelitemclicked_HandlerObject.prototype.constructor = com_zimbra_example_panelitemclicked_HandlerObject;

/**
 * This method gets called by the Zimlet framework when single-click is performed.
 *  
 */
com_zimbra_example_panelitemclicked_HandlerObject.prototype.singleClicked =
function() {
	this.displayStatusMessage("Single-click performed");
};

/**
 * This method gets called by the Zimlet framework when double-click is performed.
 *  
 */
com_zimbra_example_panelitemclicked_HandlerObject.prototype.doubleClicked =
function() {
	this.displayStatusMessage("Double-click performed");
};
