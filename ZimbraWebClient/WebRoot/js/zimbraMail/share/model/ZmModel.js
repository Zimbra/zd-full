/*
 * 
 */

/**
 * @overview
 * 
 * This class represents a data model which can process change events.
 *
 */

/**
 * Creates the data model.
 * @class
 * This class represents a data model which can process change events.
 * 
 * @author Conrad Damon
 *
 * @param {constant}		type	the event source type {@see ZmEvent}
 */
ZmModel = function(type) {
 	if (arguments.length == 0) return;

	this._evt = new ZmEvent(type);
	this._evtMgr = new AjxEventMgr();
}

/**
 * Returns a string representation of the zimlet.
 * 
 * @return		{String}		a string representation of the zimlet
 */
ZmModel.prototype.toString = 
function() {
	return "ZmModel";
}

/**
* Adds a change listener.
*
* @param {AjxListener}	listener	the change listener to add
*/
ZmModel.prototype.addChangeListener = 
function(listener) {
	return this._evtMgr.addListener(ZmEvent.L_MODIFY, listener);
}

/**
* Removes the given change listener.
*
* @param {AjxListener}	listener		the change listener to remove
*/
ZmModel.prototype.removeChangeListener = 
function(listener) {
	return this._evtMgr.removeListener(ZmEvent.L_MODIFY, listener);    	
}

/**
* Removes all change listeners.
* 
*/
ZmModel.prototype.removeAllChangeListeners = 
function() {
	return this._evtMgr.removeAll(ZmEvent.L_MODIFY);    	
}

/**
* Notifies listeners of the given change event.
*
* @param {constant}		event		the event type {@see ZmEvent}
* @param {Hash}			details		additional information
* 
* @private
*/
ZmModel.prototype._notify =
function(event, details) {
	if (this._evtMgr.isListenerRegistered(ZmEvent.L_MODIFY)) {
		this._evt.set(event, this);
		this._evt.setDetails(details);
		this._evtMgr.notifyListeners(ZmEvent.L_MODIFY, this._evt);
	}
};

/**
 * @private
 */
ZmModel.notifyEach =
function(list, event, details) {
	if (!(list && list.length)) { return; }
	for (var i = 0; i < list.length; i++) {
		list[i]._notify(event, details);
	}
};
