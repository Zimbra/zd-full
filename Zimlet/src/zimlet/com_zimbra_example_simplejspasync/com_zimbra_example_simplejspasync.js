/*
 * 
 */

com_zimbra_example_simplejspasync_HandlerObject = function() {
};
com_zimbra_example_simplejspasync_HandlerObject.prototype = new ZmZimletBase;
com_zimbra_example_simplejspasync_HandlerObject.prototype.constructor = com_zimbra_example_simplejspasync_HandlerObject;

/**
 * Double clicked.
 */
com_zimbra_example_simplejspasync_HandlerObject.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * Single clicked.
 */
com_zimbra_example_simplejspasync_HandlerObject.prototype.singleClicked =
function() {
	this._displayDialog();
};

/**
 * Displays the zimlet jsp page.
 * 
 */
com_zimbra_example_simplejspasync_HandlerObject.prototype._displayDialog = 
function() {
	
	var jspUrl = this.getResource("jspfile.jsp");

	var callback = new AjxCallback(this, this._rpcCallback, ["param1", "param2"])

	AjxRpc.invoke(null, jspUrl, null, callback, true);
	
};

/**
 * Called from the ajax callback.
 * 
 */
com_zimbra_example_simplejspasync_HandlerObject.prototype._rpcCallback =
function(p1, p2, response) {

	if (response.success == true) {
		appCtxt.getAppController().setStatusMsg(response.text);		
	}

};
