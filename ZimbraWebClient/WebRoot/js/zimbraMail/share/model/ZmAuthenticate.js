/*
 * 
 */

/**
 * @overview
 * 
 * This file defines authentication.
 *
 */

/**
 * Constructor. Use {@link execute} to construct the authentication.
 * @class
 * This class represents in-app authentication following the expiration of the session.
 * 
 * @see		#execute
 */
ZmAuthenticate = function() {}

ZmAuthenticate._isAdmin = false;

/**
 * Sets the authentication as "admin".
 * 
 * @param	{Boolean}	isAdmin		<code>true</code> if admin
 */
ZmAuthenticate.setAdmin =
function(isAdmin) {
	ZmAuthenticate._isAdmin = isAdmin;
};

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmAuthenticate.prototype.toString = 
function() {
	return "ZmAuthenticate";
};

/**
 * Executes an authentication.
 * 
 * @param	{String}	uname		the username
 * @param	{String}	pword		the password
 * @param	{AjxCallback}	callback	the callback
 */
ZmAuthenticate.prototype.execute =
function(uname, pword, callback) {
	var command = new ZmCsfeCommand();
	var soapDoc;
	if (!ZmAuthenticate._isAdmin) {
		soapDoc = AjxSoapDoc.create("AuthRequest", "urn:zimbraAccount");
		var el = soapDoc.set("account", uname);
		el.setAttribute("by", "name");
	} else {
		soapDoc = AjxSoapDoc.create("AuthRequest", "urn:zimbraAdmin", null);
		soapDoc.set("name", uname);
	}
	soapDoc.set("virtualHost", location.hostname);	
	soapDoc.set("password", pword);
	var respCallback = new AjxCallback(this, this._handleResponseExecute, callback);
	command.invoke({soapDoc: soapDoc, noAuthToken: true, noSession: true, asyncMode: true, callback: respCallback})
};

/**
 * @private
 */
ZmAuthenticate.prototype._handleResponseExecute =
function(callback, result) {
	if (!result.isException()) {
		ZmCsfeCommand.noAuth = false;
	}

	if (callback) {
		callback.run(result);
	}
};
