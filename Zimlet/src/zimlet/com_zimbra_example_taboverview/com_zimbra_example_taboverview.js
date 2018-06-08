/*
 * 
 */

/**
 * Defines the Zimlet handler class.
 *   
 */
function com_zimbra_example_taboverview_HandlerObject() {
};

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_taboverview_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_taboverview_HandlerObject.prototype.constructor = com_zimbra_example_taboverview_HandlerObject;

/**
 * This method gets called by the Zimlet framework when the zimlet loads.
 *  
 */
com_zimbra_example_taboverview_HandlerObject.prototype.init =
function() {
	
	// create the tab application
	this._tabAppName = this.createApp("Tab Label", "zimbraIcon", "Tab Tool Tip");
	
};

/**
 * This method gets called by the Zimlet framework each time the application is opened or closed.
 *  
 * @param	{String}	appName		the application name
 * @param	{Boolean}	active		if <code>true</code>, the application status is open; otherwise, <code>false</code>
 */
com_zimbra_example_taboverview_HandlerObject.prototype.appActive =
function(appName, active) {
	switch(appName) {
		case this._tabAppName: {			
			if (active) {
			
				var app = appCtxt.getApp(this._tabAppName); // returns ZmZimletApp
				app.setContent("<b>THIS IS THE TAB APPLICATION CONTENT AREA</b>");

				var toolbar = app.getToolbar(); // returns ZmToolBar
				toolbar.setContent("<b>THIS IS THE TAB APPLICATION TOOLBAR AREA</b>");

				var overview = app.getOverview(); // returns ZmOverview
				overview.setContent("<b>THIS IS THE TAB APPLICATION OVERVIEW AREA</b>");

				var controller = appCtxt.getAppController();
				var appChooser = controller.getAppChooser();

				// change the tab label and tool tip
				var appButton = appChooser.getButton(this._tabAppName);
				appButton.setText("NEW TAB LABEL");
				appButton.setToolTipContent("NEW TAB TOOL TIP");

			}
			break;
		}
	}
};

/**
 * This method gets called by the Zimlet framework when the application is opened for the first time.
 *  
 * @param	{String}	appName		the application name		
 */
com_zimbra_example_taboverview_HandlerObject.prototype.appLaunch =
function(appName) {
	switch(appName) {
		case this._tabAppName: {
			// the app is launched, do something
			break;	
		}	
	}
};

