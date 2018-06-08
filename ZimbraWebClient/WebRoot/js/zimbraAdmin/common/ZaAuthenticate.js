/*
 * 
 */

ZaAuthenticate = function(appCtxt) {
	if (arguments.length == 0) return;
	this._appCtxt = appCtxt;
	this.uname = "";
}


ZaAuthenticate.processResponseMethods = new Array();

ZaAuthenticate.prototype.toString = 
function() {
	return "ZaAuthenticate";
}

ZaAuthenticate.prototype.changePassword = 
function (uname,oldPass,newPass,callback) {
    var soapDoc = AjxSoapDoc.create("ChangePasswordRequest", "urn:zimbraAccount");
    var el = soapDoc.set("account", uname);
    el.setAttribute("by", "name");
    soapDoc.set("oldPassword", oldPass);
    soapDoc.set("password", newPass);

	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = true;
	params.noAuthToken=true;
	params.callback = callback;
	command.invoke(params);	
}

ZaAuthenticate.prototype.execute =
function (uname, pword, callback) {
	var soapDoc = AjxSoapDoc.create("AuthRequest", ZaZimbraAdmin.URN, null);
	this.uname = uname;
	soapDoc.set("name", uname);
	soapDoc.set("password", pword);
	soapDoc.set("virtualHost", location.hostname);
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = true;
	params.noAuthToken=true;
    params.ignoreAuthToken = true;
	params.callback = callback;
	command.invoke(params);	
}

ZaAuthenticate.prototype._processResponse =
function(resp) {
	var els = resp.childNodes;
	var len = els.length;
	var el, sessionId;
	AjxCookie.setCookie(document, ZaSettings.ADMIN_NAME_COOKIE, this.uname, null, "/");			    	
	for (var i = 0; i < len; i++) {
		el = els[i];
/*		if (el.nodeName == "authToken")
			authToken = el.firstChild.nodeValue;
  		else if (el.nodeName == "lifetime")
			lifetime = el.firstChild.nodeValue;*/
		if (el.nodeName=="session")
			sessionId = el.firstChild.nodeValue;
	}
	ZmCsfeCommand.noAuth = false;

	//Instrumentation code start
	if(ZaAuthenticate.processResponseMethods) {
		var cnt = ZaAuthenticate.processResponseMethods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(ZaAuthenticate.processResponseMethods[i]) == "function") {
				ZaAuthenticate.processResponseMethods[i].call(this,resp);
			}
		}
	}	
	//Instrumentation code end		
}
