/*
 * 
 */

/**
 * Creates a new, empty filter rules controller.
 * @class
 * This class represents the filter rules controller. This controller manages
 * the filter rules page, which has a button toolbar and a list view of the rules.
 *
 * @author Conrad Damon
 *
 * @param {DwtShell}		container		the shell
 * @param {ZmPreferencesApp}	prefsApp		the preferences application
 * 
 * @extends		ZmController
 */
ZmFilterController = function(container, prefsApp, prefsView, section, parent) {

	ZmPrefController.call(this, container, prefsApp);

	this._prefsView = prefsView;

	this._parent = parent;
	this._toolbar = parent._toolbar;

	this._filterView = new ZmFilterPage(prefsView, section, this);
	this._incomingFilterRulesController = new ZmFilterRulesController(container, prefsApp, this._filterView, this, false);
	this._outgoingFilterRulesController = new ZmFilterRulesController(container, prefsApp, this._filterView, this, true);
};

ZmFilterController.prototype = new ZmPrefController;
ZmFilterController.prototype.constructor = ZmFilterController;

ZmFilterController.prototype.toString =
function() {
	return "ZmFilterController";
};

/**
 * Gets the filter rules view, which is comprised of a toolbar and a list view.
 * 
 * @return	{ZmFilterRulesView}		the filter rules view
 */
ZmFilterController.prototype.getFilterView =
function() {
	return this._filterView;
};

ZmFilterController.prototype.getIncomingFilterRulesController =
function() {
	return this._incomingFilterRulesController;
};

ZmFilterController.prototype.getOutgoingFilterRulesController =
function() {
	return this._outgoingFilterRulesController;
};
/**
 * Initializes the controller.
 * 
 * @param	{ZmToolBar}	toolbar		the toolbar
 * @param	{ZmListView}	listView		the list view
 */
ZmFilterController.prototype.initialize =
function() {

};

ZmFilterController.prototype.hasOutgoingFiltersActive =
function(callback) {
	var rules = this._outgoingFilterRulesController.getRules();
	if (!rules._initialized) {
		rules.loadRules(false, new AjxCallback(this, this._handleLoadFilters, [callback]));
	} else {
		var outgoingActive = rules.getActiveRules().size() > 0;
		if (callback)
			callback.run(outgoingActive);
		return outgoingActive;
	}
};

ZmFilterController.prototype._handleLoadFilters =
function(callback) {
	var outgoingActive = this._outgoingFilterRulesController.getRules().getActiveRules().size() > 0;
	if (callback)
		callback.run(outgoingActive);
	return outgoingActive;
};

ZmFilterController.prototype._stateChangeListener =
function (ev) {
   var index, sel, rules = null;

   var listView = this._incomingFilterRulesController.getListView();
   if (listView) {
       sel = listView.getSelection()[0];
       rules = this._incomingFilterRulesController.getRules();
       index = sel ? rules.getIndexOfRule(sel) : null;
       this._incomingFilterRulesController.resetListView(index);
   }

   var outListView = this._outgoingFilterRulesController.getListView();
   if (outListView) {
       sel = outListView.getSelection()[0];
       rules = this._outgoingFilterRulesController.getRules();
       index = sel ? rules.getIndexOfRule(sel) : null;
       this._outgoingFilterRulesController.resetListView(index);
   }

};
