/*
 * 
 */

/**
 * 
 * 
 * @private
 */
DwtXModelEvent = function(instance, modelItem, refPath, details) {
	if (arguments.length == 0) return;
	this.instance = instance;
	this.modelItem = modelItem;
	this.refPath = refPath;
	this.details = details;
}

DwtEvent.prototype.toString = function() {
	return "DwtXModelEvent";
}