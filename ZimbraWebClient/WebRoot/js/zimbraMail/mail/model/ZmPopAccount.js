/*
 * 
 */

/**
 * Creates an POP account.
 * @class
 * This class represents an POP account.
 * 
 * @param	{String}	id		the id
 * 
 * @extends		ZmDataSource
 */ZmPopAccount = function(id) {
	ZmDataSource.call(this, ZmAccount.TYPE_POP, id);
};

ZmPopAccount.prototype = new ZmDataSource;
ZmPopAccount.prototype.constructor = ZmPopAccount;

// Constants
/**
 * Defines the "cleartext" port.
 * 
 * @type	int
 */
ZmPopAccount.PORT_CLEAR 	= 110;
/**
 * Defines the "ssl" port.
 * 
 * @type	int
 */
ZmPopAccount.PORT_SSL 		= 995;
ZmPopAccount.PORT_DEFAULT	= ZmPopAccount.PORT_CLEAR;


// advanced settings
ZmPopAccount.prototype.ELEMENT_NAME = "pop3";
ZmPopAccount.prototype.port = ZmPopAccount.PORT_DEFAULT;


// Public methods

ZmPopAccount.prototype.toString =
function() {
	return "ZmPopAccount";
};

/**
 * Gets the default port.
 * 
 * @return	{int}		the port
 */
ZmPopAccount.prototype.getDefaultPort =
function() {
	return (this.connectionType == ZmDataSource.CONNECT_SSL)
		? ZmPopAccount.PORT_SSL : ZmPopAccount.PORT_DEFAULT;
};
