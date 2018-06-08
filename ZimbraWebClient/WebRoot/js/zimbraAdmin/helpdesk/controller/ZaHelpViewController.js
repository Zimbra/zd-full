/*
 * 
 */

/**
* @class ZaHelpViewController 
* @contructor ZaHelpViewController
* @param appCtxt
* @param container
* @param app
* @author Greg Solovyev
**/
ZaHelpViewController = function(appCtxt, container) {

	ZaController.call(this, appCtxt, container, "ZaHelpViewController");
	this.tabConstructor = ZaHelpView;
}

ZaHelpViewController.prototype = new ZaController();
ZaHelpViewController.prototype.constructor = ZaHelpViewController;
ZaController.initToolbarMethods["ZaHelpViewController"] = new Array();

ZaHelpViewController.initToolbarMethod =
function () {
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.SEP);
	this._toolbarOrder.push(ZaOperation.NONE);
}
ZaController.initToolbarMethods["ZaHelpViewController"].push(ZaHelpViewController.initToolbarMethod);

ZaHelpViewController.prototype.show = 
function(openInNewTab) {
    if (!this._contentView) {
		var elements = new Object();
		this._contentView = new this.tabConstructor(this._container);
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab() 
		}
		//ZaApp.getInstance().createView(ZaZimbraAdmin._HELP_VIEW, elements);
		ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
		this._UICreated = true;
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	}
	//ZaApp.getInstance().pushView(ZaZimbraAdmin._HELP_VIEW);
	ZaApp.getInstance().pushView(this.getContentViewId());
	
	/*
	if (openInNewTab) {//when a ctrl shortcut is pressed
		
	}else{ //open in the main tab
		this.updateMainTab ("Help", ZaMsg.Help_view_title ) ;	
	} */
};
