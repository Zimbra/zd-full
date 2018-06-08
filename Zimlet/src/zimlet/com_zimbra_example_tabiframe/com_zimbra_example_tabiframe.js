/*
 * 
 */

/**
 * Defines the Zimlet handler class.
 *   
 */
function com_zimbra_example_tabiframe_HandlerObject() {
}

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_tabiframe_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_tabiframe_HandlerObject.prototype.constructor = com_zimbra_example_tabiframe_HandlerObject;

/**
 * This method gets called by the Zimlet framework when the zimlet loads.
 *  
 */
com_zimbra_example_tabiframe_HandlerObject.prototype.init =
function() {

	this._simpleAppName = this.createApp("Tab iFrame App", "zimbraIcon", "An app in a new tab");

};

/**
 * This method gets called by the Zimlet framework each time the application is opened or closed.
 *  
 * @param	{String}	appName		the application name
 * @param	{Boolean}	active		if true, the application status is open; otherwise, false
 */
com_zimbra_example_tabiframe_HandlerObject.prototype.appActive =
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
 * @param	{String}	appName		the application name		
 */
com_zimbra_example_tabiframe_HandlerObject.prototype.appLaunch =
function(appName) {

	switch (appName) {
		case this._simpleAppName: {
			// do something
		
			var app = appCtxt.getApp(appName); // get access to ZmZimletApp

			app.setContent("<iframe id=\"tabiframe-app\" name=\"tabiframe-app\" src=\"http://www.yahoo.com\" width=\"100%\" height=\"100%\" /></iframe>"); // write HTML to app

			break;
		}
	}

};
