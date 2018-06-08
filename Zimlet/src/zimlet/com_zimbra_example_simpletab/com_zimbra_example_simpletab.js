/*
 * 
 */

/**
 * Defines the Zimlet handler class.
 *   
 */
function com_zimbra_example_simpletab_HandlerObject() {
}

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_simpletab_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_simpletab_HandlerObject.prototype.constructor = com_zimbra_example_simpletab_HandlerObject;

/**
* This method gets called by the Zimlet framework when the zimlet loads.
*  
*/
com_zimbra_example_simpletab_HandlerObject.prototype.init =
function() {

	this._simpleAppName = this.createApp("Simple Tab App", "zimbraIcon", "A simple app in a new tab");

};

/**
 * This method gets called by the Zimlet framework each time the application is opened or closed.
 *  
 * @param	appName		the application name
 * @param	active		if true, the application status is open; otherwise, false
 */
com_zimbra_example_simpletab_HandlerObject.prototype.appActive =
function(appName, active) {
	
	switch (appName) {
		case this._simpleAppName: {
		
			var app = appCtxt.getApp(appName); // get access to ZmZimletApp

			break;
		}
	}
	
	// do something
};

/**
 * This method gets called by the Zimlet framework when the application is opened for the first time.
 *  
 * @param	appName		the application name		
 */
com_zimbra_example_simpletab_HandlerObject.prototype.appLaunch =
function(appName) {

	switch (appName) {
		case this._simpleAppName: {
			// do something
		
			var app = appCtxt.getApp(appName); // get access to ZmZimletApp

			break;
		}
	}

};
