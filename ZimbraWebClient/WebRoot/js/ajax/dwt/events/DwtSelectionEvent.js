/*
 * 
 */

/**
 * 
 * 
 * @private
 */
DwtSelectionEvent = function(init) {
	if (arguments.length == 0) return;
	DwtUiEvent.call(this, true);
	this.reset(true);
}

DwtSelectionEvent.prototype = new DwtUiEvent;
DwtSelectionEvent.prototype.constructor = DwtSelectionEvent;

DwtSelectionEvent.prototype.toString = 
function() {
	return "DwtSelectionEvent";
}

DwtSelectionEvent.prototype.reset =
function(dontCallParent) {
	if (!dontCallParent)
		DwtUiEvent.prototype.reset.call(this);
	this.button = 0;
	this.detail = null;
	this.item = null;
}

