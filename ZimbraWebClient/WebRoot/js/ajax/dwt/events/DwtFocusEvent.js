/*
 * 
 */

/**
 * 
 * @private
 */
DwtFocusEvent = function(init) {
	if (arguments.length == 0) return;
	DwtEvent.call(this, true);
	this.reset();
}
DwtFocusEvent.prototype = new DwtEvent;
DwtFocusEvent.prototype.constructor = DwtFocusEvent;

DwtFocusEvent.FOCUS = 1;
DwtFocusEvent.BLUR = 2;

DwtFocusEvent.prototype.toString = 
function() {
	return "DwtFocusEvent";
}

DwtFocusEvent.prototype.reset = 
function() {
	this.dwtObj = null;
	this.state = DwtFocusEvent.FOCUS;
}
