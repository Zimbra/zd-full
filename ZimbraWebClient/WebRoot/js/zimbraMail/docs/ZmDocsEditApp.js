/*
 * 
 */

ZmDocsEditApp = function(){

    appCtxt.setAppController(this);

    this._init();
    this.startup();

    window.app = this;

};

ZmDocsEditApp.prototype.constructor = ZmDocsEditApp;
ZmDocsEditApp.APP_ZIMBRA_DOC = "application/x-zimbra-doc";
ZmDocsEditApp._controller = null;

ZmDocsEditApp.prototype.toString = function(){
    return "ZmDocsEditApp";
};

ZmDocsEditApp.prototype._init = function(){
    ZmDocsEditApp._controller = this._controller = new ZmDocsEditController(appCtxt.getShell());
    appCtxt.setAppController(this._controller);    
};

ZmDocsEditApp.prototype.startup = function(){
    this._controller.show();
};


ZmDocsEditApp.launch = function(){

    window.appCtxt = new ZmAppCtxt();

    appCtxt.rememberMe = false;

    // Create and initialize settings
    var settings = new ZmSettings();
    appCtxt.setSettings(settings);

    var shell = new DwtShell({className:"MainShell", userShell: document.getElementById("main_shell"), id:ZmId.SHELL});
    appCtxt.setShell(shell);

    shell.getKeyboardMgr().registerKeyMap(new DwtKeyMap(true));

    //Removing all the arguments
    var rest = location.href;
    ZmDocsEditApp.restUrl = rest.replace(/\?.*/,'');

    new ZmDocsEditApp();

};

ZmDocsEditApp.setItemInfo = function(item){
    ZmDocsEditApp.fileInfo = item;
    ZmDocsEditApp.fileInfo.loaded = true;
};

ZmDocsEditApp.setFile = function(fileId, fileName, folderId){

    if(!fileId || fileId == ""){
       fileId = null;
   }

   if(!fileName || fileName == ""){
       fileName = fileId ? null : ZmMsg.untitled
   }

   folderId = (!folderId || folderId == "") ? ZmOrganizer.ID_BRIEFCASE : folderId;

   ZmDocsEditApp.fileInfo = {
       folderId: folderId,
       contentType: ZmDocsEditApp.APP_ZIMBRA_DOC,
       name:    fileName,
       id:      fileId,
       version: 1,
       descEnabled: true
   };
};

ZmDocsEditApp._beforeUnload =
function(){
    var appCtrl = appCtxt.getAppController();
    var msg = appCtrl.checkForChanges();
    if(msg) {
        return msg;
    }
    return appCtrl.exit();
};

window.onload = function() {
    setTimeout(function() {
            ZmDocsEditApp.launch();
            window.onbeforeunload = ZmDocsEditApp._beforeUnload;
    }, 200);
};