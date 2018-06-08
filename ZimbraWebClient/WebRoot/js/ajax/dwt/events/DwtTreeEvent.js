/*
 * 
 */


/**
 * 
 * 
 * @private
 */
DwtTreeEvent = function() {
	DwtSelectionEvent.call(this, true);
}

DwtTreeEvent.prototype = new DwtSelectionEvent;
DwtTreeEvent.prototype.constructor = DwtTreeEvent;

DwtTreeEvent.prototype.toString = 
function() {
	return "DwtTreeEvent";
}

DwtTreeEvent.prototype.setFromDhtmlEvent =
function(ev, obj) {
	ev = DwtSelectionEvent.prototype.setFromDhtmlEvent.apply(this, arguments);
}
