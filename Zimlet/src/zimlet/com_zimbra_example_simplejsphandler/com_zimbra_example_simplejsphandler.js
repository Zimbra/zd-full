/*
 * 
 */

com_zimbra_example_simplejsphandler_HandlerObject = function() {
};
com_zimbra_example_simplejsphandler_HandlerObject.prototype = new ZmZimletBase;
com_zimbra_example_simplejsphandler_HandlerObject.prototype.constructor = com_zimbra_example_simplejsphandler_HandlerObject;

/**
 * Double clicked.
 */
com_zimbra_example_simplejsphandler_HandlerObject.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * Single clicked.
 */
com_zimbra_example_simplejsphandler_HandlerObject.prototype.singleClicked =
function() {
	this._displayDialog();
};

/**
 * Displays the zimlet jsp page.
 * 
 */
com_zimbra_example_simplejsphandler_HandlerObject.prototype._displayDialog = 
function() {
	
	var jspUrl = this.getResource("jspfile.jsp");
	
	window.open(jspUrl, "toolbar=yes, location=yes, status=yes, menubar=yes, scrollbars=yes, resizable=yes");
	
};

