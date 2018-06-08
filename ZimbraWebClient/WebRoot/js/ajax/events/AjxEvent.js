/*
 * 
 */

/**
 * Default constructor.
 * @class
 * 
 * @private
 */
AjxEvent = function() {
	this.data = null;
}

AjxEvent.HISTORY = "HISTORY";

/**
 * Returns a string representation of the object.
 * 
 * @return	{string}		a string representation of the object
 */
AjxEvent.prototype.toString = 
function() {
	return "AjxEvent";
}
