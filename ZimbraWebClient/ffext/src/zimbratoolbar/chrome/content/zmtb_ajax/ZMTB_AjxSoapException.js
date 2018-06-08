/*
 * 
 */



function ZMTB_AjxSoapException(msg, code, method, detail) {
	ZMTB_AjxException.call(this, msg, code, method, detail);
}

ZMTB_AjxSoapException.prototype.toString = 
function() {
	return "ZMTB_AjxSoapException";
}

ZMTB_AjxSoapException.prototype = new ZMTB_AjxException;
ZMTB_AjxSoapException.prototype.constructor = ZMTB_AjxSoapException;

ZMTB_AjxSoapException.INTERNAL_ERROR 	= "INTERNAL_ERROR";
ZMTB_AjxSoapException.INVALID_PDU 		= "INVALID_PDU";
ZMTB_AjxSoapException.ELEMENT_EXISTS 	= "ELEMENT_EXISTS";
