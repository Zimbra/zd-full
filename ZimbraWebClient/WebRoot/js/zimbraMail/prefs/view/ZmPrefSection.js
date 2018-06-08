/*
 * 
 */

/**
 * Creates an empty preferences section of the given type.
 * @constructor
 * @class
 * This class represents a single page of preferences available by selecting one of the
 * preference tabs. During construction, skeleton HTML is created. The preferences
 * aren't added until the page becomes visible.
 *
 * @param {DwtControl}	parent			the containing widget
 * @param {constant}	section			the preferences page
 * @param {ZmController}	controller		the prefs controller
 * 
 * @extends		DwtTabViewPage
 * 
 * @private
 */
ZmPrefSection = function(parent, section, controller) {
	DwtTabViewPage.call(this, parent, "ZmPreferencesPage");

	this._section = section;
	this._controller = controller;
	this._title = [ZmMsg.zimbraTitle, controller.getApp().getDisplayName(), section.title].join(": ");

	this._rendered = false; // for DwtTabViewPage
	this.hasRendered = false;
};

ZmPrefSection.prototype = new DwtTabViewPage;
ZmPrefSection.prototype.constructor = ZmPrefSection;

ZmPrefSection.prototype.toString =
function () {
    return "ZmPrefSection";
};

//
// Public methods
//

// DwtTabViewPage methods

ZmPrefSection.prototype.showMe =
function() {
	Dwt.setTitle(this._title);
    if (this.hasRendered) { return; }

    // expand section template
    var templateId = this._section.templateId;
    var data = { id: this._htmlElId };
    data.isEnabled = AjxCallback.simpleClosure(this._isEnabled, this);
    data.expandField = AjxCallback.simpleClosure(this._expandField, this, data);

    this._contentEl.innerHTML = AjxTemplate.expand(templateId, data);

    // create controls
    var details = ZmPreferencesApp[this._section.id];
    var prefs = details && details.prefs;
    if (prefs) {
        for (var i = 0; i < prefs.length; i++) {
            var settingId = prefs[i];
        }
    }
};

/**
 * Gets the title.
 * 
 * @return	{String}		the title
 */
ZmPrefSection.prototype.getTitle =
function() {
	return this._title;
};

//
// Protected methods
//

ZmPrefSection.prototype._isEnabled =
function(prefId) {
    return this._controller.checkPreCondition(ZmPref.SETUP[prefId]);
};

ZmPrefSection.prototype._expandField =
function(data, prefId) {
    var templateId = this._section.templateId.replace(/#.*$/, "#"+prefId);
    return AjxTemplate.expand(templateId, data);
};