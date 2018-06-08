/*
 * 
 */


/**
 * 
 * @private
 */
DwtControlEvent = function() {
	this.reset();
}
DwtControlEvent.prototype = new DwtEvent;
DwtControlEvent.prototype.constructor = DwtControlEvent;

// type of control event
//      RESIZE	       -- for setSize
//      MOVE	       -- for setLocation
//      RESIZE | MOVE  -- for setBounts (bitwise or)

DwtControlEvent.RESIZE = 1;
DwtControlEvent.MOVE = 2;

DwtControlEvent.prototype.toString = 
function() {
	return "DwtControlEvent";
}

DwtControlEvent.prototype.reset = 
function(type) {
	this.oldX = Dwt.DEFAULT;
	this.oldY = Dwt.DEFAULT;
	this.oldWidth = Dwt.DEFAULT;
	this.oldHeight = Dwt.DEFAULT;
	this.newX = Dwt.DEFAULT;
	this.newY = Dwt.DEFAULT;
	this.newWidth = Dwt.DEFAULT;
	this.newHeight = Dwt.DEFAULT;
	this.type = type || null;
}
