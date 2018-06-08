/*
 * 
 */


/**
 * This class represents an alert that highlights and flashes an application tab.
 *
 * @param {ZmApp}		app 		the application
 * @class
 * @private
 */
ZmAppAlert = function(app) {
	this.app = app;
};

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmAppAlert.prototype.toString =
function() {
	return "ZmAppAlert";
};

/**
 * Starts the alert.
 */
ZmAppAlert.prototype.start =
function() {
    if (!this._getAppButton().isSelected) {
		this._getAppButton().showAlert(true);
        //add a stop alert listener
        if (!this._stopAlertListenerObj) {
           this._stopAlertListenerObj = new AjxListener(this, this.stop);
           this._getAppButton().addSelectionListener(this._stopAlertListenerObj);
        }
    }
};

/**
 * Stops the alert.
 */
ZmAppAlert.prototype.stop =
function() {
    if (this._getAppButton().isSelected) {
        this._getAppButton().showAlert(false);
    }
};

ZmAppAlert.prototype._getAppButton =
function() {
	return appCtxt.getAppController().getAppChooserButton(this.app.getName());
};

