/*
 * 
 */

/**
* @class ZaZimletViewController controls display of a single Account
* @contructor ZaZimletViewController
* @param appCtxt
* @param container
* @param abApp
* @author Greg Solovyev
**/

ZaZimletViewController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container,"ZaZimletViewController");
	this._UICreated = false;
	this.objType = ZaEvent.S_ZIMLET;
	this._helpURL = ZaAccountViewController.helpURL;
	this.deleteMsg = ZaMsg.Q_DELETE_ACCOUNT;
	this.tabConstructor = ZaZimletXFormView;	
}

ZaZimletViewController.prototype = new ZaXFormViewController();
ZaZimletViewController.prototype.constructor = ZaZimletViewController;
ZaZimletViewController.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/provisioning_accounts.htm?locid="+AjxEnv.DEFAULT_LOCALE;
//public methods

ZaController.initToolbarMethods["ZaZimletViewController"] = new Array();
ZaController.setViewMethods["ZaZimletViewController"] = new Array();

ZaZimletViewController.prototype.show = 
function(entry, skipRefresh) {
    if (! this.selectExistingTabByItemId(entry.id)){
		this._setView(entry, true);
	}
}


/**
*	@method setViewMethod 
*	@param entry - isntance of ZaDomain class
*/
ZaZimletViewController.setViewMethod =
function(entry) {
	entry.load("name", entry.name);
	if(!this._UICreated) {
		this._createUI();
	} 
//	ZaApp.getInstance().pushView(ZaZimbraAdmin._ZIMLET_VIEW);
	ZaApp.getInstance().pushView(this.getContentViewId());
	this._view.setDirty(false);
    entry[ZaModel.currentTab] = "1";
    this._view.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view
	this._currentObject = entry;
}
ZaController.setViewMethods["ZaZimletViewController"].push(ZaZimletViewController.setViewMethod);

/**
* @method _createUI
**/
ZaZimletViewController.prototype._createUI =
function () {
	this._contentView = this._view = new this.tabConstructor(this._container);

    this._initToolbar();
    this._toolbarOrder.push(ZaOperation.CLOSE);
    this._toolbarOrder.push(ZaOperation.NONE);
    this._toolbarOrder.push(ZaOperation.HELP);

    this._toolbarOperations[ZaOperation.CLOSE]=new ZaOperation(ZaOperation.CLOSE,ZaMsg.TBB_Close, ZaMsg.DTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener));
    //always add Help button at the end of the toolbar
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));							
	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,  this._toolbarOrder, null, null, ZaId.VIEW_ZIMLET);		


    var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
    //ZaApp.getInstance().createView(ZaZimbraAdmin._ZIMLET_VIEW, elements);
	var tabParams = {
		openInNewTab: true,
		tabId: this.getContentViewId()
	}
	ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
	this._UICreated = true;
	ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
}
