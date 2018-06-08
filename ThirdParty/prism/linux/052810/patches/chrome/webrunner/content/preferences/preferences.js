
Components.utils.import("resource://prism/modules/WebAppProperties.jsm");

const builtinPanes = ["paneMain", "paneTabs", "paneContent", "paneApplications", "panePrivacy", "paneSecurity", "paneAdvanced"];

function PrefsOverlayObserver(aPrefsElement) {
  this._prefs = aPrefsElement;
}

PrefsOverlayObserver.prototype = {
  observe : function(aSubject, aTopic, aData) {
    // Add buttons for new panes
    var panes = document.getElementsByTagName("prefpane");
    for (var i=0; i<panes.length; i++) {
      var pane = panes.item(i);
      var builtin = false;
      for (var j=0; j<builtinPanes.length; j++) {
        if (builtinPanes[j] == pane.id) {
          builtin = true;
          break;
        }
      }
      
      if (!builtin) {
        this._prefs._makePaneButton(pane);
      }
    }
    
    // Remove buttons for deleted panes
    var button = this._prefs._selector.firstChild;
    while (button) {
      var next = button.nextSibling;
      if (!document.getElementById(button.getAttribute("pane"))) {
        this._prefs._selector.removeChild(button);
      }
      button = next;
    }

    // Show the current pane again since applying the overlay causes it to be displayed with no content
    var prefwindow = this._prefs;
    setTimeout(function() { prefwindow.showPane(prefwindow.currentPane); }, 0);
  }
}

var WebRunnerPrefs =
{
  init : function() {
    var prefwindow = document.getElementById("BrowserPreferences");
    var obs = new PrefsOverlayObserver(prefwindow);
    document.loadOverlay("resource://webapp/preferences/preferences.xul", obs);
  },
  
  paneLoad : function(e) {
    var paneElement = e.originalTarget;
    var paneSpec = paneElement.src;
    var parts = paneSpec.split('/');
    var filename = parts[parts.length-1];
    var overlaySpec = "resource://webapp/preferences/" + filename;

    function OverlayLoadObserver(aPrefsElement, aPaneElement, aContentHeight)
    {
      this._prefs = aPrefsElement;
      this._pane = aPaneElement;
      this._contentHeight = aContentHeight;
    }
    OverlayLoadObserver.prototype = { 
      observe: function (aSubject, aTopic, aData) 
      {
        if (this._prefs._shouldAnimate) {
          var dummyPane = document.createElement("prefpane");
          dummyPane.id = "paneDummy";
          dummyPane.contentHeight = this._contentHeight;
          document.documentElement.appendChild(dummyPane);
          this._prefs.lastSelected = "paneDummy";
          this._prefs._selectPane(this._pane);
          document.documentElement.removeChild(dummyPane);
        }
      }
    }
    
    var obs = new OverlayLoadObserver(document.getElementById("BrowserPreferences"), paneElement, paneElement.contentHeight);
    document.loadOverlay(overlaySpec, obs);
  }
};

window.addEventListener("paneload", WebRunnerPrefs.paneLoad, false); 
