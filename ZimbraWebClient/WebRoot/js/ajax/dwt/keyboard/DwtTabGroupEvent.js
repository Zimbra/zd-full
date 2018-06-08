/*
 * 
 */

/**
 * @constructor
 * @class
 * This class represents a the tab event. This event is used to indicate changes in
 * the state of {@link DwtTabGroup} objects (e.g. member addition and deletion). 
 * 
 * @author Ross Dargahi
 * 
 * @private
 */
DwtTabGroupEvent = function() {
	/**
	 * Tab group for which the event is being generated
	 * @type DwtTabGroup
	 */
	this.tabGroup = null;
	
	/**
	 * New focus member
	 * @type DwtControl|HTMLElement
	 */
	this.newFocusMember = null;
}

/**
 * Returns a string representation of this object.
 * 
 * @return {string}	a string representation of this object
 */
DwtTabGroupEvent.prototype.toString = 
function() {
	return "DwtTabGroupEvent";
}

/**
 * Resets the members of the event.
 * 
 */
DwtTabGroupEvent.prototype.reset =
function() {
	this.tabGroup = null;
	this.newFocusMember = null;
}
