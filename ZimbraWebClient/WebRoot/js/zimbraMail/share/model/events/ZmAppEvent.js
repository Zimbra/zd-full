/*
 * 
 */

/**
 * @overview
 * 
 * This file defines an application event.
 *
 */

/**
 * Creates an empty application event.
 * @class
 * This class represents an event related to a change of state for an individual
 * application or for ZCS as a whole.
 * 
 * @param {Object}	the application to which this event applies; if <code>null</code>, the event applies to ZCS
 * 
 * @extends		ZmEvent
 */
ZmAppEvent = function(app) {
	ZmEvent.call(this);
};

ZmAppEvent.prototype = new ZmEvent;
ZmAppEvent.prototype.constructor = ZmAppEvent;

/**
 * Event used to notify listeners before startup (i.e. before the first
 * app is activated). This is a bit of a misnomer because this event occurs
 * after the apps are initialized but before the first app is shown. This
 * allows code to be executed after the apps have registered settings
 * but before the app actually acts on those settings.
 *
 * @see ZmAppEvent.POST_STARTUP
 */
ZmAppEvent.PRE_STARTUP	= "PRESTARTUP";

/**
 * Defines the event used to notify listeners post-startup.
 */
ZmAppEvent.POST_STARTUP	= "POSTSTARTUP";
/**
 * Defines the event used to notify listeners pre-launch.
 */
ZmAppEvent.PRE_LAUNCH	= "PRELAUNCH";
/**
 * Defines the event used to notify listeners post-launch.
 */
ZmAppEvent.POST_LAUNCH	= "POSTLAUNCH";
/**
 * Defines the event used to notify listeners post-render.
 */
ZmAppEvent.POST_RENDER	= "POSTRENDER";

ZmAppEvent.ACTIVATE	= "ACTIVATE";

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmAppEvent.prototype.toString =
function() {
	return "ZmAppEvent";
};
