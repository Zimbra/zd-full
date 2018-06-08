/*
 * 
 */

function MemLeakTests(appCtxt, domain) {
/*
	ZmController.call(this, appCtxt);

	ZmCsfeCommand.setServerUri(location.protocol + "//" + domain + appCtxt.get(ZmSetting.CSFE_SERVER_URI));
	appCtxt.setAppController(this);
	appCtxt.setClientCmdHdlr(new ZmClientCmdHandler(appCtxt));
*/
	this._shell = appCtxt.getShell();

	this.startup();
};

//MemLeakTests.prototype = new ZmController;
//MemLeakTests.prototype.constructor = MemLeakTests;


MemLeakTests.run =
function(domain) {
	// Create the global app context
	var appCtxt = new ZmAppCtxt();

	//appCtxt.setIsPublicComputer(false);

	// Create the shell
	//var settings = appCtxt.getSettings();
	var shell = new DwtShell();

	appCtxt.setShell(shell);
	//appCtxt.setItemCache(new AjxCache());
	//appCtxt.setUploadManager(new AjxPost(appCtxt.getUploadFrameId()));

	// Go!
	new MemLeakTests(appCtxt, domain);
};


// Public methods

MemLeakTests.prototype.toString = 
function() {
	return "MemLeakTests";
};

MemLeakTests.prototype.startup =
function() {
	this._shell.getHtmlElement().innerHTML = "hello world.";
};
