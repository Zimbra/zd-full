/*
 * 
 */

/**
 * Creates an exception.
 * @constructor
 * @class
 * This is the base class for all exceptions in the Ajax toolkit.
 * 
 * @author Ross Dargahi
 * 
 * @param {string} [msg] 		the human readable message
 * @param {string|number} [code]	 any error or fault code
 * @param {string} [method] 	the name of the method throwing the exception
 * @param {string} [detail] 	any additional detail
 * 
 * @extends		AjxException
 */
DwtException = function(msg, code, method, detail) {
	if (arguments.length === 0) {return;}
	AjxException.call(this, msg, code, method, detail);
}

DwtException.prototype = new AjxException();
DwtException.prototype.constructor = DwtException;

DwtException.prototype.toString = 
function() {
	return "DwtException";
};

/**
 * Invalid parent exception code.
 */
DwtException.INVALIDPARENT = -1;

/**
 * Invalid operation exception code.
 */
DwtException.INVALID_OP = -2;

/**
 * Internal error exception code.
 */
DwtException.INTERNAL_ERROR = -3;

/**
 * Invalid parameter exception code.
 */
DwtException.INVALID_PARAM = -4;
