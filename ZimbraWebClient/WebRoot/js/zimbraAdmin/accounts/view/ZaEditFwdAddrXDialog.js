/*
 * 
 */

/**
* @class ZaEditAliasXDialog
* @contructor ZaEditAliasXDialog
* @author Greg Solovyev
* @param parent
* param app
**/
ZaEditFwdAddrXDialog = function(parent,  w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent, null, title, w, h);
    this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/forwarding_mail.htm?locid="+AjxEnv.DEFAULT_LOCALE;
    this._containedObject = {};
	this.initForm(ZaAlias.myXModel,this.getMyXForm());
}

ZaEditFwdAddrXDialog.prototype = new ZaXDialog;
ZaEditFwdAddrXDialog.prototype.constructor = ZaEditFwdAddrXDialog;

ZaEditFwdAddrXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:1,
		items:[
            {type:_GROUP_,isTabGroup:true, items: [ //allows tab key iteration
                {ref:ZaAccount.A_name, type:_TEXTFIELD_, label:ZaMsg.Enter_EmailAddr,width:230,visibilityChecks:[],enableDisableChecks:[]}
                ]
            }
        ]
	};
	return xFormObject;
}
