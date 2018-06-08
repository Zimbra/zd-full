/*
 * 
 */

/**
 * Creates a point.
 * @constructor
 * @class
 * This class represents a point. A point has an x-coordinate and y-coordinate.
 * 
 * @author Ross Dargahi
 * 
 * @param {number} x 	the x coordinate
 * @param {number} y 	the y coordinate
 * 
 */
DwtPoint = function(x, y) {
	/**
	 * The x-coordinate.
	 * @type	number
	 */
	this.x = x || 0;
	/**
	 * The y-coordinate.
	 * @type	number
	 */
	this.y = y || 0;
}

DwtPoint.tmp = new DwtPoint(0, 0);

/**
 * Returns a string representation of the object.
 * 
 * @return		{string}		a string representation of the object
 */
DwtPoint.prototype.toString = 
function() {
	return "DwtPoint";
}

/**
 * Sets the values of a point
 * 
 * @param {number} x 	the x coordinate
 * @param {number} y 	the y coordinate
 */
 DwtPoint.prototype.set =
 function(x, y) {
 	this.x = x;
 	this.y = y;
 }
 
