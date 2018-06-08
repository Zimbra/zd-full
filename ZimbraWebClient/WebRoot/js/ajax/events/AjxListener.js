/*
 * 
 */


/**
* Creates a new listener.
* @constructor
* @class
* This class represents a listener, which is a function to be called in response to an event.
* A listener is a slightly specialized callback: it has a {@link #handleEvent} method, and it does not
* return a value.
*
* @author Ross Dargahi
* 
* @param {Object}	obj	the object to call the function from
* @param {function}	func	the listener function
* @param {primative|array}	args   the default arguments
* 
* @extends		AjxCallback
*/
AjxListener = function(obj, method, args) {
	AjxCallback.call(this, obj, method, args);
}

AjxListener.prototype = new AjxCallback();
AjxListener.prototype.constructor = AjxListener;

AjxListener.prototype.toString = 
function() {
	return "AjxListener";
}

/**
* Invoke the listener function.
*
* @param {AjxEvent}		ev		the event object that gets passed to an event handler
*/
AjxListener.prototype.handleEvent =
function(ev) {
	return this.run(ev);
}
