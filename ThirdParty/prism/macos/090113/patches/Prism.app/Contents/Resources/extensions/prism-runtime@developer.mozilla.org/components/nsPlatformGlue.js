/*
//@line 40 "/home/matt/macpro/Development/mozilla/prism/runtime/components/src/nsPlatformGlue.js"
*/

/* Development of this Contribution was supported by Yahoo! Inc. */

const Cc = Components.classes;
const Ci = Components.interfaces;

Components.utils.import("resource://gre/modules/XPCOMUtils.jsm");

const PRISM_PROTOCOL_PREFIX = "prism.protocol.";
const PROTOCOL_HANDLER_CLASSNAME = "WebRunner protocol handler";
const PROTOCOL_HANDLER_CID = Components.ID("{2033eb27-55cf-4e06-80ae-134b59ed5437}");

function PlatformGlueSound() {
  //Constructor
}

PlatformGlueSound.prototype = {
  classDescription: "Platform sound API",
  classID:          Components.ID("{eb7e36e0-ec6d-11dc-95ff-0800200c9a66}"),
  contractID:       "@mozilla.org/platform-sound-api;1",

  QueryInterface: XPCOMUtils.generateQI(
    [Ci.nsIPlatformGlueSound,
     Ci.nsISecurityCheckedComponent,
     Ci.nsIClassInfo]),

  // nsIClassInfo
  implementationLanguage: Ci.nsIProgrammingLanguage.JAVASCRIPT,
  flags: Ci.nsIClassInfo.DOM_OBJECT,

  getInterfaces: function getInterfaces(aCount) {
    var interfaces = [Ci.nsIPlatformGlueSound,
                      Ci.nsISecurityCheckedComponent,
                      Ci.nsIClassInfo];
    aCount.value = interfaces.length;
    return interfaces;
  },

  getHelperForLanguage: function getHelperForLanguage(aLanguage) {
    return null;
  },

  //nsISecurityCheckedComponent
  canCallMethod: function canCallMethod(iid, methodName) {
    Components.utils.reportError(methodName);
    return "AllAccess";
  },

  canCreateWrapper: function canCreateWrapper(iid) {
    return "AllAccess";
  },

  canGetProperty: function canGetProperty(iid, propertyName) {
    Components.utils.reportError(propertyName);
    return "AllAccess";
  },

  canSetProperty: function canSetProperty(iid, propertyName) {
    Components.utils.reportError(propertyName);
    return "NoAccess";
  },

  //nsIPlatformGlueSound
  beep: function beep() {
    var sound = Cc["@mozilla.org/sound;1"].createInstance(Ci.nsISound);
    sound.beep();
  },

  playSound: function playSound(aSoundURI) {
    var sound = Cc["@mozilla.org/sound;1"].createInstance(Ci.nsISound);
    if (aSoundURI.indexOf("://") == -1) {
      sound.playSystemSound(aSoundURI);
    }
    else
    {
      var ioService = Components.classes["@mozilla.org/network/io-service;1"].getService(Ci.nsIIOService);
      sound.play(ioService.newURI(aSoundURI, null, null));
    }
  }
}

function MakeProtocolHandlerFactory(contractid) {
  var factory = {
    QueryInterface: function (aIID) {
      if (!aIID.equals(Components.interfaces.nsISupports) &&
        !aIID.equals(Components.interfaces.nsIFactory))
        throw Components.results.NS_ERROR_NO_INTERFACE;
      return this;
    },
    createInstance: function (outer, iid) {
      if (outer != null)
        throw Components.results.NS_ERROR_NO_AGGREGATION;
      return (new PlatformProtocolHandler(contractid)).QueryInterface(iid);
    }
  };

  return factory;
}

function PlatformProtocolHandler(contractid) {
  this._ioService = Cc["@mozilla.org/network/io-service;1"].getService(Ci.nsIIOService);
}

PlatformProtocolHandler.prototype = {
  QueryInterface: XPCOMUtils.generateQI(
    [Ci.nsIProtocolHandler,
    Ci.nsIClassInfo]),

  // nsIClassInfo
  implementationLanguage: Ci.nsIProgrammingLanguage.JAVASCRIPT,
  flags: Ci.nsIClassInfo.DOM_OBJECT,

  getInterfaces: function getInterfaces(aCount) {
    var interfaces = [Ci.nsIProtocolHandler,
                      Ci.nsIClassInfo];
    aCount.value = interfaces.length;
    return interfaces;
  },

  getHelperForLanguage: function getHelperForLanguage(aLanguage) {
    return null;
  },

  get defaultPort() {
    return 80;
  },

  get protocolFlags() {
    return 0;
  },

  newURI: function newURI(aSpec, anOriginalCharset, aBaseURI) {
    var platformGlue = Cc["@mozilla.org/platform-web-api;1"].createInstance(Ci.nsIPlatformGlue);
    var callback = {};
    var uriString = platformGlue.getProtocolURI(aSpec, callback);
    if (!callback.value) {
      return this._ioService.newURI(uriString, "", null);
    }
    else {
      // Use the original URI so we can invoke the callback and cancel
      var uri = Cc["@mozilla.org/network/simple-uri;1"].createInstance(Ci.nsIURI);
      uri.spec = aSpec;
      return uri;
    }
  },
  
  newChannel: function newChannel(aUri) {
    // We never create a channel for the protocol since it redirects to the protocol URI in newURI
    throw Components.results.NS_ERROR_UNEXPECTED;
  },
  
  allowPort: function allowPort(aPort, aScheme) {
    // We are not overriding any special ports
    return false;
  }
}

//=================================================
// Factory - Treat PlatformGlue as a singleton
// XXX This is required, because we're registered for the 'JavaScript global
// privileged property' category, whose handler always calls createInstance.
// See bug 386535.
var gSingleton = null;
var PlatformGlueFactory = {
  createInstance: function af_ci(aOuter, aIID) {
    if (aOuter != null)
      throw Components.results.NS_ERROR_NO_AGGREGATION;

    if (gSingleton == null) {
      gSingleton = new PlatformGlue();
    }

    return gSingleton.QueryInterface(aIID);
  }
};

function PlatformGlue() {
  // WebProgressListener for getting notification of new doc loads.
  var progress = Cc["@mozilla.org/docloaderservice;1"].getService(Ci.nsIWebProgress);
  progress.addProgressListener(this, Ci.nsIWebProgress.NOTIFY_STATE_DOCUMENT);
}

PlatformGlue.prototype = {
  classDescription: "Platform web API",
  classID:          Components.ID("{3960e4b8-89d1-4c20-ae24-4d10d0900c4d}"),
  contractID:       "@mozilla.org/platform-web-api;1",

  _xpcom_factory : PlatformGlueFactory,

  _xpcom_categories : [{
    category: "JavaScript global property",
    entry: "platform"
  }],

  _prefs : Cc["@mozilla.org/preferences-service;1"].getService(Ci.nsIPrefBranch),
  _window : null,
  _icon : null,
  _protocolCallbacks : {},

  QueryInterface: XPCOMUtils.generateQI(
    [Ci.nsIPlatformGlue,
     Ci.nsISecurityCheckedComponent,
     Ci.nsISupportsWeakReference,
     Ci.nsIWebProgressListener,
     Ci.nsIClassInfo]),

  // nsIClassInfo
  implementationLanguage: Ci.nsIProgrammingLanguage.JAVASCRIPT,
  flags: Ci.nsIClassInfo.DOM_OBJECT,

  getInterfaces: function getInterfaces(aCount) {
    var interfaces = [Ci.nsIPlatformGlue,
                      Ci.nsISecurityCheckedComponent,
                      Ci.nsISupportsWeakReference,
                      Ci.nsIWebProgressListener,
                      Ci.nsIClassInfo];
    aCount.value = interfaces.length;
    return interfaces;
  },

  getHelperForLanguage: function getHelperForLanguage(aLanguage) {
    return null;
  },

  //nsISecurityCheckedComponent
  canCallMethod: function canCallMethod(iid, methodName) {
    Components.utils.reportError(methodName);
    return "AllAccess";
  },

  canCreateWrapper: function canCreateWrapper(iid) {
    return "AllAccess";
  },

  canGetProperty: function canGetProperty(iid, propertyName) {
    Components.utils.reportError(propertyName);
    return "AllAccess";
  },

  canSetProperty: function canSetProperty(iid, propertyName) {
    Components.utils.reportError(propertyName);
    return "NoAccess";
  },

  // nsIWebProgressListener
  onStateChange: function(aWebProgress, aRequest, aStateFlags, aStatus) {
    if (aStateFlags & Ci.nsIWebProgressListener.STATE_TRANSFERRING && !(aWebProgress.DOMWindow instanceof Ci.nsIDOMChromeWindow)) {
      var windowMediator = Cc["@mozilla.org/appshell/window-mediator;1"].getService(Ci.nsIWindowMediator);
      var win = windowMediator.getMostRecentWindow("navigator:browser");
      var browser = win.document.getElementById("browser_content");
    
      if (aWebProgress.DOMWindow == browser.contentWindow) {
        this._window = aWebProgress.DOMWindow;
      }
    }
  },

  onProgressChange: function(aWebProgress, aRequest, aCurSelf, aMaxSelf, aCurTotal, aMaxTotal) {
  },

  onLocationChange: function(aWebProgress, aRequest, aLocation) {
  },

  onStatusChange: function(aWebProgress, aRequest, aStatus, aMessage) {
  },

  onSecurityChange: function(aWebProgress, aRequest, aState) {
  },
  
  //nsIPlatformGlue
  showNotification: function showNotification(aTitle, aText, aImageURI) {
    var alerts = Cc["@mozilla.org/alerts-service;1"].getService(Ci.nsIAlertsService);
    alerts.showAlertNotification(aImageURI, aTitle, aText, false, "", null);
  },

  postStatus: function postStatus(aName, aValue) {
    if (this._icon)
      this._icon.setBadgeText(aValue);
  },
  
  openURI : function openURI(aURISpec) {
    var ioService = Components.classes["@mozilla.org/network/io-service;1"].getService(Ci.nsIIOService);
    var extps = Cc["@mozilla.org/uriloader/external-protocol-service;1"].getService(Ci.nsIExternalProtocolService);
    extps.loadURI(ioService.newURI(aURISpec, null, null), null);
  },
  
  canQuitApplication : function canQuitApplication() {
    var os = Cc["@mozilla.org/observer-service;1"].getService(Ci.nsIObserverService);
    if (!os) return true;
    
    try {
      var cancelQuit = Cc["@mozilla.org/supports-PRBool;1"].createInstance(Ci.nsISupportsPRBool);
      os.notifyObservers(cancelQuit, "quit-application-requested", null);
      
      // Something aborted the quit process. 
      if (cancelQuit.data)
        return false;
    }
    catch (ex) { }
    return true;
  },

  quit : function quit() {
    if (!this.canQuitApplication())
      return false;

    var appStartup = Cc["@mozilla.org/toolkit/app-startup;1"].getService(Ci.nsIAppStartup);
    appStartup.quit(Ci.nsIAppStartup.eAttemptQuit);
    return true;
  },

 
  sound: function sound() {
    if (!this._sound)
      this._sound = new PlatformGlueSound();
    return this._sound;
  },

  icon: function icon() {
    if (!this._window) {
      // Not initialized yet
      throw Components.results.NS_ERROR_NOT_INITIALIZED;
    }
    
    if (!this._icon) {
      var desktop = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIDesktopEnvironment);
      this._icon = desktop.getApplicationIcon(this._window);
    }
    return this._icon;
  },
  
  registerProtocolHandler: function registerProtocol(uriScheme, uriString, callback) {
    // First register the protocol with the shell
    var shellService = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIShellService);
    shellService.registerProtocol(uriScheme, null, null);
    
    // Register with the component registrar
    var registrar = Components.manager.QueryInterface(Ci.nsIComponentRegistrar);
    var contractId = "@mozilla.org/network/protocol;1?name=" + uriScheme;
    registrar.registerFactory(PROTOCOL_HANDLER_CID, PROTOCOL_HANDLER_CLASSNAME, contractId,
      MakeProtocolHandlerFactory(contractId));
    
    // Then store a pref so we remember the URI string we want to load
    this._prefs.setCharPref(PRISM_PROTOCOL_PREFIX + uriScheme, uriString);
	
    // Register the callback, if any
    if (callback) {
      this._protocolCallbacks[uriScheme] = callback;
    }
  },
  
  unregisterProtocolHandler: function unregisterProtocol(uriScheme) {
    // Unregister the protocol with the shell so the URI scheme no longer invokes the application
    var shellService = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIShellService);
    shellService.unregisterProtocol(uriScheme);
    
    // Unregister with the component registrar
    var registrar = Components.manager.QueryInterface(Ci.nsIComponentRegistrar);
    var contractId = "@mozilla.org/network/protocol;1?name=" + uriScheme;
    // Now what?

    // And remove the pref
    this._prefs.clearUserPref(PRISM_PROTOCOL_PREFIX + uriScheme);
    
    // Remove the callback, if any
    if (uriScheme in this._protocolCallbacks) {
      delete this._protocolCallbacks[uriScheme];
    }
  },
  
  getProtocolURI : function getProtocolURI(uriSpec, callback) {
    var uriScheme = uriSpec.replace(/(.*):.*/, "$1");
    if (callback && uriScheme in this._protocolCallbacks) {
      callback.value = this._protocolCallbacks[uriScheme];
    }
    try {
      var uriString = this._prefs.getCharPref(PRISM_PROTOCOL_PREFIX + uriScheme);
      return uriString.replace(/%s/, escape(uriSpec.replace(/.*:(.*)/, "$1")));
    }
    catch (e) {
      return "";
    }
  },
  
  isRegisteredProtocolHandler : function isRegisteredProtocolHandler(uriScheme) {
    var shellService = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIShellService);
    var appInfo = Cc["@mozilla.org/xre/app-info;1"].getService(Ci.nsIXULAppInfo);
    try {
      return (shellService.getDefaultApplicationForURIScheme(uriScheme) == appInfo.name);
    }
    catch(e) {
      return false;
    }
  }
}

var components = [PlatformGlue];

function NSGetModule(compMgr, fileSpec) {
  return XPCOMUtils.generateModule(components);
}
