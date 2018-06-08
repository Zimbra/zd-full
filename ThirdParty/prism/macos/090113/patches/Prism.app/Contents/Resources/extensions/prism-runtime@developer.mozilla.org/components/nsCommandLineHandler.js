//@line 2 "/Users/matthewgertner/Development/mozilla/prism/runtime/components/src/nsCommandLineHandler.js"
/*  */
 
/* Development of this Contribution was supported by Yahoo! Inc. */

const Cc = Components.classes;
const Ci = Components.interfaces;

Components.utils.import("resource://gre/modules/XPCOMUtils.jsm");

var WebRunnerCloseEvent = function() {
};

WebRunnerCloseEvent.prototype = {
  run: function() {
    var appStartup = Cc["@mozilla.org/toolkit/app-startup;1"].getService(Ci.nsIAppStartup);
    appStartup.quit(appStartup.eForceQuit);
  },
  
  QueryInterface: function(iid) {
    if (iid.equals(Components.interfaces.nsIRunnable) ||
        iid.equals(Components.interfaces.nsISupports)) {
            return this;
    }
    throw Components.results.NS_ERROR_NO_INTERFACE;
  }
};

function WebRunnerCommandLineHandler() {
}

WebRunnerCommandLineHandler.prototype = {
  classDescription: "WebRunnerCommandLineHandler",
  classID: Components.ID("{8fd0bfd1-4d85-4167-804f-0911cb3224dc}"),
  contractID: "@mozilla.org/commandlinehandler/general-startup;1?type=webrunner",
  
  _xpcom_categories: [{ category: "command-line-handler", entry: "m-webrunner" }],
  
  QueryInterface: XPCOMUtils.generateQI([Ci.nsICommandLineHandler]),
     
  handle : function(aCmdLine) {
    if (!aCmdLine)
      return;
      
    Components.utils.import("resource://prism-runtime/modules/WebAppProperties.jsm");

    var file = null;

    if (aCmdLine.handleFlag("close", false)) {
      var mainThread = Components.classes["@mozilla.org/thread-manager;1"].getService().mainThread;
      mainThread.dispatch(new WebRunnerCloseEvent(), Ci.nsIEventTarget.DISPATCH_NORMAL);
      aCmdLine.preventDefault = true;
      return;
    }

    // Check for a webapp profile
    var environment = Cc["@mozilla.org/process/environment;1"].getService(Ci.nsIEnvironment);
    var webapp;
    if (environment.exists("PRISM_WEBAPP")) {
      webapp = environment.get("PRISM_WEBAPP");
    }
    else {
      webapp = aCmdLine.handleFlagWithParam("webapp", false);
    }

//@line 82 "/Users/matthewgertner/Development/mozilla/prism/runtime/components/src/nsCommandLineHandler.js"
    // On Mac, check for a webapp.ini inside the current app bundle
    if (!webapp) {
      var dirSvc = Cc["@mozilla.org/file/directory_service;1"].getService(Ci.nsIProperties);
      var resourcesRoot = dirSvc.get("resource:app", Ci.nsIFile);
      
      var iniPath = resourcesRoot.clone();
      iniPath.append("webapp.ini");

      if (iniPath.exists()) {
        webapp = resourcesRoot.path;
      }
    }
//@line 95 "/Users/matthewgertner/Development/mozilla/prism/runtime/components/src/nsCommandLineHandler.js"

    if (webapp) {
      // Check for a bundle first
      try {
        if (aCmdLine.state == aCmdLine.STATE_INITIAL_LAUNCH) {
          file = aCmdLine.resolveFile(webapp);
        }
        else {
          file = Cc["@mozilla.org/file/local;1"].createInstance(Ci.nsILocalFile);
          file.initWithPath(webapp);
        }
      }
      catch (ex) {
        // Ouch, not a file
        file = null;
      }

      // Do we have a valid file? or did it fail?
      if (!file || !file.exists()) {
        // Its not a bundle. look for an installed webapp
        var installRoot = WebAppProperties.getInstallRoot();
        var appSandbox = installRoot.clone();
        appSandbox.append(webapp);
        if (appSandbox.exists())
          file = appSandbox.clone();
      }
    }

    var protocolURI = null;
    var callback = {};

    // Check for an OSX launch
    var uriSpec = aCmdLine.handleFlagWithParam("url", false);
    if (uriSpec) {
      // Check whether we were launched as a protocol
      // If so, get the URL to load for the protocol scheme
      var platform = Cc["@mozilla.org/platform-web-api;1"].createInstance(Ci.nsIPlatformGlue);
      protocolURI = platform.getProtocolURI(uriSpec, callback);

      if (!protocolURI || protocolURI.length == 0) {
        var uri = aCmdLine.resolveURI(uriSpec);
        if (!file && uri.scheme == "file") {
          file = uri.QueryInterface(Ci.nsIFileURL).file;
        }
      }
    }
    
    if (file && file.exists()) {
      // Bundles are files and need to be installed
      if (!file.isDirectory()) {
        Components.utils.import("resource://prism/modules/WebAppInstall.jsm");
        file = WebAppInstall.install(file);
      }
      WebAppProperties.init(file);
    }

    for (var index in WebAppProperties.flags) {
      var key = WebAppProperties.flags[index];
      var value = aCmdLine.handleFlagWithParam(key, false);
      if (value != null)
        WebAppProperties.setParameter(key, value);
    }
    
    if (protocolURI && protocolURI.length > 0) {
      WebAppProperties.uri = protocolURI;
    }
    
    var win = this.activateWindow();
    
    if (callback.value) {
      // Invoke the callback and don't load a new page
      callback.value.handleURI(uriSpec);

      aCmdLine.preventDefault = true;
      return;
    }

    // Check for an existing window and reuse it if there is one
    if (win) {
      if (protocolURI) {
        win.document.getElementById("browser_content").loadURI(WebAppProperties.uri, null, null);
      }
      
      aCmdLine.preventDefault = true;
      return;
    }
    
    if (WebAppProperties.script.startup)
      WebAppProperties.script.startup();
  },
  
  activateWindow : function() {
    var windowMediator = Cc["@mozilla.org/appshell/window-mediator;1"].getService(Ci.nsIWindowMediator);
    var win = windowMediator.getMostRecentWindow("navigator:browser");

    if (win) {
      var event = win.document.QueryInterface(Ci.nsIDOMDocumentEvent).createEvent("Events");
      event.initEvent("DOMActivate", true, true);
      win.QueryInterface(Ci.nsIDOMEventTarget).dispatchEvent(event);
    }
    
    return win;
  },

  helpInfo : "",
};

function NSGetModule(compMgr, fileSpec) {
  return XPCOMUtils.generateModule([WebRunnerCommandLineHandler]);
}
