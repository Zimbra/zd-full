/*
 * 
 */

/**
 * Creates a SOAP exception.
 * @class
 * 
 * 
 * @param {string} 		[msg]		the human readable message
 * @param {constant}		code		the exception code
 * @param {string} 		[method] 	the name of the method throwing the exception
 * @param {string} 		[detail]		any additional detail
 * 
 * @extends		AjxException
 * 
 * @private
 */
AjxSoapException = function(msg, code, method, detail) {
	AjxException.call(this, msg, code, method, detail);
}

AjxSoapException.prototype.toString = 
function() {
	return "AjxSoapException";
}

AjxSoapException.prototype = new AjxException;
AjxSoapException.prototype.constructor = AjxSoapException;

/**
 * Defines an "internal error" exception.
 */
AjxSoapException.INTERNAL_ERROR 	= "INTERNAL_ERROR";
/**
 * Defines an "invalid PDU" exception.
 */
AjxSoapException.INVALID_PDU 		= "INVALID_PDU";
/**
 * Defines an "element exists" exception.
 */
AjxSoapException.ELEMENT_EXISTS 	= "ELEMENT_EXISTS";
