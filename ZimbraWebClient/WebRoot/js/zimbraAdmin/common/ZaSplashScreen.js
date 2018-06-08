/*
 * 
 */

ZaSplashScreen =function(parent) {
    var className = "LoginScreen";
    DwtComposite.call(this, {parent:parent, className:className, posStyle:DwtControl.ABSOLUTE_STYLE});
    this._origClassName = className;
    this._xparentClassName = className + "-Transparent";
    this.setBounds(0, 0, "100%", "100%");
    var htmlElement = this.getHtmlElement();
    htmlElement.style.zIndex = Dwt.Z_SPLASH;
    htmlElement.className = className;
    this.setVisible(false);
    
	var params = ZLoginFactory.copyDefaultParams(ZaMsg);
	params.showPanelBorder = true;
	params.showForm = false;
	params.showUserField = false;
	params.showPasswordField = false;
	params.showRememberMeCheckbox = false;
	params.showLogOff = false;
	params.showButton = false;
    params.companyURL = ZaAppCtxt.getLogoURI () ;
    var html = ZLoginFactory.getLoginDialogHTML(params);
	this.setContent(html);
}

ZaSplashScreen.prototype = new DwtComposite;
ZaSplashScreen.prototype.constructor = ZaSplashScreen;
ZaSplashScreen.prototype.toString = 
function() {
	return "ZaSplashScreen";
}