/*
 * 
 */

/**
 * @overview
 * This file defines a mountpoint organizer class.
 */

/**
 * Creates a mountpoint organizer.
 * @class
 * This class represents a mountpoint organizer. This class can be used to represent generic
 * mountpoints in an overview tree but is mostly used as a utility to create mountpoints.
 * 
 * @param	{Hash}	params		a hash of parameters
 * 
 * @extends		ZmOrganizer
 */
ZmMountpoint = function(params) {
	params.type = ZmOrganizer.MOUNTPOINT;
	ZmOrganizer.call(this, params);
	this.view = params.view;
}

ZmMountpoint.prototype = new ZmOrganizer;
ZmMountpoint.prototype.constructor = ZmMountpoint;

// Constants
ZmMountpoint.__CREATE_PARAMS = { "l":1, "name":1, "zid":1, "rid":1, "owner":1, "path":1, "view":1, "color":1, "f":1 };


// Public Methods

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmMountpoint.prototype.toString =
function() {
	return "ZmMountpoint";
};

/**
 * Creates the mountpoint.
 * 
 * @param {Hash}	params		a hash of parameters
 * @param	{String}	params.name		the name
 */
ZmMountpoint.create =
function(params, callback) {
	var soapDoc = AjxSoapDoc.create("CreateMountpointRequest", "urn:zimbraMail");

	var linkNode = soapDoc.set("link");
	for (var p in params) {
		if (!(p in ZmMountpoint.__CREATE_PARAMS)) continue;
		linkNode.setAttribute(p, params[p]);
	}

	var errorCallback = new AjxCallback(null, ZmMountpoint._handleCreateError, params.name);
	appCtxt.getAppController().sendRequest({soapDoc:soapDoc,
											asyncMode:true,
											callback:callback,
											errorCallback:errorCallback});
};

/**
 * @private
 */
ZmMountpoint._handleCreateError =
function(name, response) {

	var msg;
	if (response.code == ZmCsfeException.SVC_PERM_DENIED || response.code == ZmCsfeException.MAIL_NO_SUCH_FOLDER) {
		msg = ZmCsfeException.getErrorMsg(response.code);
	} else if (response.code == ZmCsfeException.MAIL_ALREADY_EXISTS) {
		msg = AjxMessageFormat.format(ZmMsg.errorAlreadyExists, [name]);
	}
	if (msg) {
		appCtxt.getAppController().popupErrorDialog(msg, null, null, true);
		return true;
	}
};
