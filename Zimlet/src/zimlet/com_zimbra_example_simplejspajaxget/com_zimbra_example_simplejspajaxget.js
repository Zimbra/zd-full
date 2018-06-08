/*
 * 
 */

com_zimbra_example_simplejspajaxget_HandlerObject = function() {
};
com_zimbra_example_simplejspajaxget_HandlerObject.prototype = new ZmZimletBase;
com_zimbra_example_simplejspajaxget_HandlerObject.prototype.constructor = com_zimbra_example_simplejspajaxget_HandlerObject;

/**
 * Double clicked.
 */
com_zimbra_example_simplejspajaxget_HandlerObject.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * Single clicked.
 */
com_zimbra_example_simplejspajaxget_HandlerObject.prototype.singleClicked =
function() {
	this._displayDialog();
};

/**
 * Displays the zimlet jsp page.
 * 
 */
com_zimbra_example_simplejspajaxget_HandlerObject.prototype._displayDialog = 
function() {
	
	var jspUrl = this.getResource("jspfile.jsp");

	var response = AjxRpc.invoke(null, jspUrl, null, null, true);

	if (response.success == true) {
		appCtxt.getAppController().setStatusMsg(response.text);		
	}
	
};

