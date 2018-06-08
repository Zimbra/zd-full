/*
 * 
 */


/**
 * 
 * @private
 */
DwtHoverEvent = function(type, delay, object, x, y) {
	if (arguments.length == 0) return;
	DwtEvent.call(this, true);
	this.type = type;
	this.delay = delay;
	this.object = object;
	this.x = x || -1;
	this.y = y || -1;
}

DwtHoverEvent.prototype = new DwtEvent;
DwtHoverEvent.prototype.constructor = DwtHoverEvent;

DwtHoverEvent.prototype.toString = function() { return "DwtHoverEvent"; };

DwtHoverEvent.prototype.reset =
function() {
	this.type = 0;
	this.delay = 0;
	this.object = null;
	this.x = -1;
	this.y = -1;
};
