//@line 2 "c:\mozilla\workdir\mozilla\192src\prism\chrome\content\webrunner.js"
/*
//@line 41 "c:\mozilla\workdir\mozilla\192src\prism\chrome\content\webrunner.js"
*/

/*
# -*- Mode: Java; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*-
# 
*/

/*
* Portions Copyright (c) VMware, Inc. [1998 - 2011]. All Rights Reserved.
*/

const Cc = Components.classes;
const Ci = Components.interfaces;

Components.utils.import("resource://prism/modules/WebAppProperties.jsm");

window.addEventListener("load", function() { WebRunner.startup(); }, false);

/**
 * Main application code.
 */
var WebRunner = {
  _ios : null,
  _tld : null,
  _uri : null,
  _xulWindow : null,
  _currentDomain : null,
  _windowCreator : null,
  _minimizedState : 0,
  _zoomLevel : 1,
  _loadError : false,
  _firstLoad : true,

  _getBrowser : function() {
    return document.getElementById("browser_content");
  },

  _saveSettings : function() {
//@line 76 "c:\mozilla\workdir\mozilla\192src\prism\chrome\content\webrunner.js"

      var settings = {};
      settings.version = "1";

      // Pull out the window state
      settings.window = {};

      // save current fullscreen state and unfullscreen it for proper store of
      // our window unfullscreen'ed
      settings.window.fullscreen = window.fullScreen;
      window.fullScreen = false;

      settings.window.state = window.windowState;
      if (window.windowState == window.STATE_NORMAL) {
        settings.window.screenX = window.screenX;
        settings.window.screenY = window.screenY;
        settings.window.width = window.outerWidth;
        settings.window.height = window.outerHeight;
      }

      settings.sidebar = {};
      settings.sidebar.visible = (document.getElementById("splitter_sidebar").getAttribute("state") == "open");
      settings.sidebar.width = document.getElementById("box_sidebar").width;

      // Save using JSON format
      if (WebAppProperties.hasOwnProperty("id")) {
        var nativeJSON = Cc["@mozilla.org/dom/json;1"].createInstance(Ci.nsIJSON);
        var json = nativeJSON.encode(settings);
        var dirSvc = Cc["@mozilla.org/file/directory_service;1"].getService(Ci.nsIProperties);
        var file = dirSvc.get("ProfD", Ci.nsIFile);
        file.append("localstore.json");
        FileIO.stringToFile(json, file);
      }
  },

  _loadSettings : function() {
    // Load using JSON format
    var settings;
    if (WebAppProperties.hasOwnProperty("id")) {
      var dirSvc = Cc["@mozilla.org/file/directory_service;1"].getService(Ci.nsIProperties);
      var file = dirSvc.get("ProfD", Ci.nsIFile);
      file.append("localstore.json");
      if (file.exists()) {
        var json = FileIO.fileToString(file);
        var nativeJSON = Cc["@mozilla.org/dom/json;1"].createInstance(Ci.nsIJSON);
        settings = nativeJSON.decode(json);

        if (settings.window) {
          switch (settings.window.state) {
            case window.STATE_MAXIMIZED:
              window.maximize();
              break;
            case window.STATE_MINIMIZED:
              // Do nothing if window was closed minimized
              break;
            case window.STATE_NORMAL:
              window.moveTo(settings.window.screenX, settings.window.screenY);
              window.resizeTo(settings.window.width, settings.window.height);
              break;
          }
          // if webapp was closed in fullscreen mode, it should relaunch as such.
          window.fullScreen = settings.window.fullscreen;
        }

        if (settings.sidebar) {
          document.getElementById("splitter_sidebar").setAttribute("state", settings.sidebar.visible ? "open" : "collapsed");
          document.getElementById("box_sidebar").width = settings.sidebar.width;
        }
      }
      else if (WebAppProperties.maximize) {
        window.maximize();
      }
    }
  },

  _delayedStartup : function() {
    this._prepareWebAppScript();

    if (WebAppProperties.uri) {
      // Give the user script the chance to do additional processing before
      // the page loads
      if (WebAppProperties.script.preload) {
        if (!WebAppProperties.script.preload())
          // Preload failed so don't load the web app URI
          return;
      }

      // Show tray icon, if any, and default behavior to hide on minimize
      if (WebAppProperties.trayicon && ("@mozilla.org/desktop-environment;1" in Cc)) {
        var desktop = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIDesktopEnvironment);
        var icon = desktop.getApplicationIcon(this._getBrowser().contentWindow);

        if (icon) {
          this.showTrayIcon();
          icon.behavior = Ci.nsIApplicationIcon.HIDE_ON_MINIMIZE;
        }
      }

      // Setup the resource:// substitution for the app's root directory
      var resourceProtocol = this._ios.getProtocolHandler("resource").QueryInterface(Ci.nsIResProtocolHandler);
      var appRootURI = this._ios.newFileURI(WebAppProperties.getAppRoot());
      resourceProtocol.setSubstitution("webapp", appRootURI);

      // Call the script's load() function once the page has finished loading
      if (WebAppProperties.script.load) {
        this._getBrowser().addEventListener("DOMContentLoaded", this._contentLoaded, true);
      }

      this._getBrowser().loadURI(WebAppProperties.uri, null, null);

      if (WebAppProperties.refresh && WebAppProperties.refresh > 0) {
        this._autoRefresh(false);
      }
    }

    this._loadSettings();
  },

  _contentLoaded : function(event) {
    var browser = WebRunner._getBrowser();
    // Don't fire for iframes
    if (event.target == browser.contentDocument) {
      WebAppProperties.script["window"] = browser.contentWindow.wrappedJSObject;

      if (WebRunner._firstLoad) {
        WebRunner._firstLoad = false;
        if (!WebRunner._loadError) {
          WebAppProperties.script.load();
        }
        else if (WebAppProperties.script.error) {
          WebAppProperties.script.error();
        }
      }
    }
  },

  _autoRefresh : function(refreshNow) {
    if (refreshNow) {
      WebRunner._getBrowser().reload();
    }
    setTimeout(function() { WebRunner._autoRefresh(true); }, WebAppProperties.refresh * 1000);
  },

  _processConfig : function() {
    // Process commandline parameters
    document.documentElement.setAttribute("id", WebAppProperties.icon);
    document.getElementById("toolbar_main").hidden = !WebAppProperties.location;
    document.getElementById("box_sidebar").hidden = !WebAppProperties.sidebar;
    document.getElementById("splitter_sidebar").hidden = !WebAppProperties.sidebar;
    document.getElementById("statusbar").setAttribute("collapsed", !WebAppProperties.status);

    if (!WebAppProperties.navigation) {
      // Remove navigation key from the document
      var keys = document.getElementsByTagName("key");
      for (var i=keys.length - 1; i>=0; i--)
        if (keys[i].className == "nav")
          keys[i].parentNode.removeChild(keys[i]);
    }
  },

  _handleWindowClose : function(event) {
//@line 245 "c:\mozilla\workdir\mozilla\192src\prism\chrome\content\webrunner.js"

    // Handler for clicking on the 'x' to close the window
    if (!this.shutdownQuery()) {
      return false;
    }
    
    return true;
  },

  _handleContentCommand: function(event) {
    // Don't trust synthetic events
    if (!event.isTrusted)
      return;

    var ot = event.originalTarget;
    var errorDoc = ot.ownerDocument;

    // If the event came from an ssl error page, it is probably either the "Add
    // ExceptionÖ" or "Get me out of here!" button
    if (/^about:neterror\?e=nssBadCert/.test(errorDoc.documentURI)) {
      if (ot == errorDoc.getElementById('exceptionDialogButton')) {
        var params = { exceptionAdded : false };

        var prefs = Cc["@mozilla.org/preferences-service;1"].getService(Ci.nsIPrefService);

        try {
          switch (prefs.getIntPref("browser.ssl_override_behavior")) {
            case 2 : // Pre-fetch & pre-populate
              params.prefetchCert = true;
            case 1 : // Pre-populate
              params.location = errorDoc.location.href;
          }
        } catch (e) {
          Components.utils.reportError("Couldn't get ssl_override pref: " + e);
        }

        window.openDialog("chrome://pippki/content/exceptionDialog.xul", "","chrome,centerscreen,modal", params);

        // If the user added the exception cert, attempt to reload the page
        if (params.exceptionAdded)
          errorDoc.location.reload();
      }
      else if (ot == errorDoc.getElementById('getMeOutOfHereButton')) {
        // Get the start page from the *default* pref branch, not the user's
        var defaultPrefs = Cc["@mozilla.org/preferences-service;1"].getService(Ci.nsIPrefService).getDefaultBranch(null);
        var url = "about:blank";
        try {
          url = defaultPrefs.getCharPref("browser.startup.homepage");
          // If url is a pipe-delimited set of pages, just take the first one.
          if (url.indexOf("|") != -1)
            url = url.split("|")[0];
        } catch (e) { /* Fall back on about blank */ }

        this._getBrowser().loadURI(url, null, null, false);
      }
    }
  },

  _popupShowing : function(aEvent) {
    var cut = document.getElementById("cmd_cut");
    var copy = document.getElementById("cmd_copy");
    var paste = document.getElementById("cmd_paste");
    var del = document.getElementById("cmd_delete");

    var isContentSelected = !document.commandDispatcher.focusedWindow.getSelection().isCollapsed;

    var target = document.popupNode;

    // if the document is editable, show context menu like in text inputs
    var win = target.ownerDocument.defaultView;
    if (win) {
      var isEditable = false;
      try {
        var editingSession = new XPCNativeWrapper(win).QueryInterface(Ci.nsIInterfaceRequestor)
                                .getInterface(Ci.nsIWebNavigation)
                                .QueryInterface(Ci.nsIInterfaceRequestor)
                                .getInterface(Ci.nsIEditingSession);
        isEditable = editingSession.windowIsEditable(win);
      }
      catch(ex) {
        // If someone built with composer disabled, we can't get an editing session.
      }
    }

    var isTextField = target instanceof HTMLTextAreaElement;
    if (target instanceof HTMLInputElement && (target.type == "text" || target.type == "password"))
      isTextField = true;

    var isTextSelectied= (isTextField && target.selectionStart != target.selectionEnd);

    cut.setAttribute("disabled", (((!isTextField && !isEditable) || !isTextSelectied) ? "true" : "false"));
    copy.setAttribute("disabled", ((((!isTextField && !isEditable) || !isTextSelectied) && !isContentSelected) ? "true" : "false"));
    paste.setAttribute("disabled", ((!isTextField && !isEditable) ? "true" : "false"));
    del.setAttribute("disabled", ((!isTextField && !isEditable) ? "true" : "false"));

    var copylink = document.getElementById("menuitem_copyLink");
    var copylinkSep = document.getElementById("menusep_copyLink");
    var foundLink = false;
    var elem = target;
    while (elem) {
      if (elem instanceof HTMLAnchorElement && elem.href) {
        foundLink = true;
        break;
      }
      elem = elem.parentNode;
    }
    
    var saveImage = document.getElementById("menuitem_saveImage");
    var foundImage = false;
    if (target instanceof Ci.nsIImageLoadingContent && new XPCNativeWrapper(target.QueryInterface(Ci.nsIImageLoadingContent)).currentURI) {
      // Make sure image is loaded
      let request = new XPCNativeWrapper(target.QueryInterface(Ci.nsIImageLoadingContent)).getRequest(Ci.nsIImageLoadingContent.CURRENT_REQUEST);
      if (request && (request.imageStatus & request.STATUS_SIZE_AVAILABLE)) {
        foundImage = true;
      }
    }

    
    copylink.hidden = !foundLink;
    saveImage.hidden = !foundImage;
    copylinkSep.hidden = !foundLink && !foundImage;

    InlineSpellCheckerUI.clearSuggestionsFromMenu();
    InlineSpellCheckerUI.uninit();

    var separator = document.getElementById("menusep_spellcheck");
    separator.hidden = true;
    var addToDictionary = document.getElementById("menuitem_addToDictionary");
    addToDictionary.hidden = true;
    var noSuggestions = document.getElementById("menuitem_noSuggestions");
    noSuggestions.hidden = true;

    var editor = null;
    if (isTextField && !target.readOnly)
      editor = new XPCNativeWrapper(target.QueryInterface(Ci.nsIDOMNSEditableElement)).editor;

    if (isEditable)
      editor = editingSession.getEditorForWindow(win);

    if (editor) {
      InlineSpellCheckerUI.init(editor);
      InlineSpellCheckerUI.initFromEvent(document.popupRangeParent, document.popupRangeOffset);

      var onMisspelling = InlineSpellCheckerUI.overMisspelling;
      if (onMisspelling) {
        separator.hidden = false;
        addToDictionary.hidden = false;
        var menu = document.getElementById("popup_content");
        var suggestions = InlineSpellCheckerUI.addSuggestionsToMenu(menu, addToDictionary, 5);
        noSuggestions.hidden = (suggestions > 0);
      }
    }
  },

  _tooltipShowing : function(aEvent) {
    var tooltipNode = document.tooltipNode;
    var canShow = false;
    if (tooltipNode.namespaceURI != "http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul") {
      const XLinkNS = "http://www.w3.org/1999/xlink";

      var titleText = null;
      var XLinkTitleText = null;
      var direction = tooltipNode.ownerDocument.dir;
      var defView = tooltipNode.ownerDocument.defaultView;

      while (defView && !titleText && !XLinkTitleText && tooltipNode) {
        if (tooltipNode.nodeType == Node.ELEMENT_NODE) {
          titleText = tooltipNode.getAttribute("title");
          XLinkTitleText = tooltipNode.getAttributeNS(XLinkNS, "title");
          direction = defView.getComputedStyle(tooltipNode, "").getPropertyValue("direction");
        }
        tooltipNode = tooltipNode.parentNode;
      }

      var tooltip = document.getElementById("tooltip_content");
      tooltip.style.direction = direction;

      for each (var text in [titleText, XLinkTitleText]) {
        if (text && /\S/.test(text)) {
          // Per HTML 4.01 6.2 (CDATA section), literal CRs and tabs should be
          // replaced with spaces, and LFs should be removed entirely.
          text = text.replace(/[\r\t]/g, ' ');
          text = text.replace(/\n/g, '');

          tooltip.setAttribute("label", text);
          canShow = true;
        }
      }
    }

    if (!canShow)
      aEvent.preventDefault();
  },

  _domTitleChanged : function(aEvent) {
    if (aEvent.target != this._getBrowser().contentDocument)
      return;

    document.title = aEvent.target.title;
  },

  // Converts a pattern in this programs simple notation to a regular expression.
  // thanks Greasemonkey! http://www.mozdev.org/source/browse/greasemonkey/src/
  // thanks AdBlock (via Greasemonkey)! http://www.mozdev.org/source/browse/adblock/adblock/
  _convert2RegExp : function(pattern) {
    s = new String(pattern);
    res = new String("^");

    for (var i = 0 ; i < s.length ; i++) {
      switch(s[i]) {
        case '*' :
          res += ".*";
          break;

        case '.' :
        case '?' :
        case '^' :
        case '$' :
        case '+' :
        case '{' :
        case '[' :
        case '|' :
        case '(' :
        case ')' :
        case ']' :
          res += "\\" + s[i];
          break;

        case '\\' :
          res += "\\\\";
          break;

        case ' ' :
          // Remove spaces from URLs.
          break;

        default :
          res += s[i];
          break;
      }
    }

    var tldRegExp = new RegExp("^(\\^(?:[^/]*)(?://)?(?:[^/]*))(\\\\\\.tld)((?:/.*)?)$")
    var tldRes = res.match(tldRegExp);
    if (tldRes) {
      // build the mighty TLD RegExp
      var tldStr = "\.(?:demon\\.co\\.uk|esc\\.edu\\.ar|(?:c[oi]\\.)?[^\\.]\\.(?:vt|ne|ks|il|hi|sc|nh|ia|wy|or|ma|vi|tn|in|az|id|nc|co|dc|nd|me|al|ak|de|wv|nm|mo|pr|nj|sd|md|va|ri|ut|ct|pa|ok|ky|mt|ga|la|oh|ms|wi|wa|gu|mi|tx|fl|ca|ar|mn|ny|nv)\\.us|[^\\.]\\.(?:(?:pvt\\.)?k12|cc|tec|lib|state|gen)\\.(?:vt|ne|ks|il|hi|sc|nh|ia|wy|or|ma|vi|tn|in|az|id|nc|co|dc|nd|me|al|ak|de|wv|nm|mo|pr|nj|sd|md|va|ri|ut|ct|pa|ok|ky|mt|ga|la|oh|ms|wi|wa|gu|mi|tx|fl|ca|ar|mn|ny|nv)\\.us|[^\\.]\\.vt|ne|ks|il|hi|sc|nh|ia|wy|or|ma|vi|tn|in|az|id|nc|co|dc|nd|me|al|ak|de|wv|nm|mo|pr|nj|sd|md|va|ri|ut|ct|pa|ok|ky|mt|ga|la|oh|ms|wi|wa|gu|mi|tx|fl|ca|ar|mn|ny|nvus|ne|gg|tr|mm|ki|biz|sj|my|hn|gl|ro|tn|co|br|coop|cy|bo|ck|tc|bv|ke|aero|cs|dm|km|bf|af|mv|ls|tm|jm|pg|ky|ga|pn|sv|mq|hu|za|se|uy|iq|ai|com|ve|na|ba|ph|xxx|no|lv|tf|kz|ma|in|id|si|re|om|by|fi|gs|ir|li|tz|td|cg|pa|am|tv|jo|bi|ee|cd|pk|mn|gd|nz|as|lc|ae|cn|ag|mx|sy|cx|cr|vi|sg|bm|kh|nr|bz|vu|kw|gf|al|uz|eh|int|ht|mw|gm|bg|gu|info|aw|gy|ac|ca|museum|sk|ax|es|kp|bb|sa|et|ie|tl|org|tj|cf|im|mk|de|pro|md|fm|cl|jp|bn|vn|gp|sm|ar|dj|bd|mc|ug|nu|ci|dk|nc|rw|aq|name|st|hm|mo|gq|ps|ge|ao|gr|va|is|mt|gi|la|bh|ms|bt|gb|it|wf|sb|ly|ng|gt|lu|il|pt|mh|eg|kg|pf|um|fr|sr|vg|fj|py|pm|sn|sd|au|sl|gh|us|mr|dz|ye|kn|cm|arpa|bw|lk|mg|tk|su|sc|ru|travel|az|ec|mz|lb|ml|bj|edu|pr|fk|lr|nf|np|do|mp|bs|to|cu|ch|yu|eu|mu|ni|pw|pl|gov|pe|an|ua|uk|gw|tp|kr|je|tt|net|fo|jobs|yt|cc|sh|io|zm|hk|th|so|er|cz|lt|mil|hr|gn|be|qa|cv|vc|tw|ws|ad|sz|at|tg|zw|nl|info\\.tn|org\\.sd|med\\.sd|com\\.hk|org\\.ai|edu\\.sg|at\\.tt|mail\\.pl|net\\.ni|pol\\.dz|hiroshima\\.jp|org\\.bh|edu\\.vu|net\\.im|ernet\\.in|nic\\.tt|com\\.tn|go\\.cr|jersey\\.je|bc\\.ca|com\\.la|go\\.jp|com\\.uy|tourism\\.tn|com\\.ec|conf\\.au|dk\\.org|shizuoka\\.jp|ac\\.vn|matsuyama\\.jp|agro\\.pl|yamaguchi\\.jp|edu\\.vn|yamanashi\\.jp|mil\\.in|sos\\.pl|bj\\.cn|net\\.au|ac\\.ae|psi\\.br|sch\\.ng|org\\.mt|edu\\.ai|edu\\.ck|ac\\.yu|org\\.ws|org\\.ng|rel\\.pl|uk\\.tt|com\\.py|aomori\\.jp|co\\.ug|video\\.hu|net\\.gg|org\\.pk|id\\.au|gov\\.zw|mil\\.tr|net\\.tn|org\\.ly|re\\.kr|mil\\.ye|mil\\.do|com\\.bb|net\\.vi|edu\\.na|co\\.za|asso\\.re|nom\\.pe|edu\\.tw|name\\.et|jl\\.cn|gov\\.ye|ehime\\.jp|miyazaki\\.jp|kanagawa\\.jp|gov\\.au|nm\\.cn|he\\.cn|edu\\.sd|mod\\.om|web\\.ve|edu\\.hk|medecin\\.fr|org\\.cu|info\\.au|edu\\.ve|nx\\.cn|alderney\\.gg|net\\.cu|org\\.za|mb\\.ca|com\\.ye|edu\\.pa|fed\\.us|ac\\.pa|alt\\.na|mil\\.lv|fukuoka\\.jp|gen\\.in|gr\\.jp|gov\\.br|gov\\.ac|id\\.fj|fukui\\.jp|hu\\.com|org\\.gu|net\\.ae|mil\\.ph|ltd\\.je|alt\\.za|gov\\.np|edu\\.jo|net\\.gu|g12\\.br|org\\.tn|store\\.co|fin\\.tn|ac\\.nz|gouv\\.fr|gov\\.il|org\\.ua|org\\.do|org\\.fj|sci\\.eg|gov\\.tt|cci\\.fr|tokyo\\.jp|net\\.lv|gov\\.lc|ind\\.br|ca\\.tt|gos\\.pk|hi\\.cn|net\\.do|co\\.tv|web\\.co|com\\.pa|com\\.ng|ac\\.ma|gov\\.bh|org\\.zw|csiro\\.au|lakas\\.hu|gob\\.ni|gov\\.fk|org\\.sy|gov\\.lb|gov\\.je|ed\\.cr|nb\\.ca|net\\.uy|com\\.ua|media\\.hu|com\\.lb|nom\\.pl|org\\.br|hk\\.cn|co\\.hu|org\\.my|gov\\.dz|sld\\.pa|gob\\.pk|net\\.uk|guernsey\\.gg|nara\\.jp|telememo\\.au|k12\\.tr|org\\.nz|pub\\.sa|edu\\.ac|com\\.dz|edu\\.lv|edu\\.pk|com\\.ph|net\\.na|net\\.et|id\\.lv|au\\.com|ac\\.ng|com\\.my|net\\.cy|unam\\.na|nom\\.za|net\\.np|info\\.pl|priv\\.hu|rec\\.ve|ac\\.uk|edu\\.mm|go\\.ug|ac\\.ug|co\\.dk|net\\.tt|oita\\.jp|fi\\.cr|org\\.ac|aichi\\.jp|org\\.tt|edu\\.bh|us\\.com|ac\\.kr|js\\.cn|edu\\.ni|com\\.mt|fam\\.pk|experts-comptables\\.fr|or\\.kr|org\\.au|web\\.pk|mil\\.jo|biz\\.pl|org\\.np|city\\.hu|org\\.uy|auto\\.pl|aid\\.pl|bib\\.ve|mo\\.cn|br\\.com|dns\\.be|sh\\.cn|org\\.mo|com\\.sg|me\\.uk|gov\\.kw|eun\\.eg|kagoshima\\.jp|ln\\.cn|seoul\\.kr|school\\.fj|com\\.mk|e164\\.arpa|rnu\\.tn|pro\\.ae|org\\.om|gov\\.my|net\\.ye|gov\\.do|co\\.im|org\\.lb|plc\\.co\\.im|net\\.jp|go\\.id|net\\.tw|gov\\.ai|tlf\\.nr|ac\\.im|com\\.do|net\\.py|tozsde\\.hu|com\\.na|tottori\\.jp|net\\.ge|gov\\.cn|org\\.bb|net\\.bs|ac\\.za|rns\\.tn|biz\\.pk|gov\\.ge|org\\.uk|org\\.fk|nhs\\.uk|net\\.bh|tm\\.za|co\\.nz|gov\\.jp|jogasz\\.hu|shop\\.pl|media\\.pl|chiba\\.jp|city\\.za|org\\.ck|net\\.id|com\\.ar|gon\\.pk|gov\\.om|idf\\.il|net\\.cn|prd\\.fr|co\\.in|or\\.ug|red\\.sv|edu\\.lb|k12\\.ec|gx\\.cn|net\\.nz|info\\.hu|ac\\.zw|info\\.tt|com\\.ws|org\\.gg|com\\.et|ac\\.jp|ac\\.at|avocat\\.fr|org\\.ph|sark\\.gg|org\\.ve|tm\\.pl|net\\.pg|gov\\.co|com\\.lc|film\\.hu|ishikawa\\.jp|hotel\\.hu|hl\\.cn|edu\\.ge|com\\.bm|ac\\.om|tec\\.ve|edu\\.tr|cq\\.cn|com\\.pk|firm\\.in|inf\\.br|gunma\\.jp|gov\\.tn|oz\\.au|nf\\.ca|akita\\.jp|net\\.sd|tourism\\.pl|net\\.bb|or\\.at|idv\\.tw|dni\\.us|org\\.mx|conf\\.lv|net\\.jo|nic\\.in|info\\.vn|pe\\.kr|tw\\.cn|org\\.eg|ad\\.jp|hb\\.cn|kyonggi\\.kr|bourse\\.za|org\\.sb|gov\\.gg|net\\.br|mil\\.pe|kobe\\.jp|net\\.sa|edu\\.mt|org\\.vn|yokohama\\.jp|net\\.il|ac\\.cr|edu\\.sb|nagano\\.jp|travel\\.pl|gov\\.tr|com\\.sv|co\\.il|rec\\.br|biz\\.om|com\\.mm|com\\.az|org\\.vu|edu\\.ng|com\\.mx|info\\.co|realestate\\.pl|mil\\.sh|yamagata\\.jp|or\\.id|org\\.ae|greta\\.fr|k12\\.il|com\\.tw|gov\\.ve|arts\\.ve|cul\\.na|gov\\.kh|org\\.bm|etc\\.br|or\\.th|ch\\.vu|de\\.tt|ind\\.je|org\\.tw|nom\\.fr|co\\.tt|net\\.lc|intl\\.tn|shiga\\.jp|pvt\\.ge|gov\\.ua|org\\.pe|net\\.kh|co\\.vi|iwi\\.nz|biz\\.vn|gov\\.ck|edu\\.eg|zj\\.cn|press\\.ma|ac\\.in|eu\\.tt|art\\.do|med\\.ec|bbs\\.tr|gov\\.uk|edu\\.ua|eu\\.com|web\\.do|szex\\.hu|mil\\.kh|gen\\.nz|okinawa\\.jp|mob\\.nr|edu\\.ws|edu\\.sv|xj\\.cn|net\\.ru|dk\\.tt|erotika\\.hu|com\\.sh|cn\\.com|edu\\.pl|com\\.nc|org\\.il|arts\\.co|chirurgiens-dentistes\\.fr|net\\.pa|takamatsu\\.jp|net\\.ng|org\\.hu|net\\.in|net\\.vu|gen\\.tr|shop\\.hu|com\\.ae|tokushima\\.jp|za\\.com|gov\\.eg|co\\.jp|uba\\.ar|net\\.my|biz\\.et|art\\.br|ac\\.fk|gob\\.pe|com\\.bs|co\\.ae|de\\.net|net\\.eg|hyogo\\.jp|edunet\\.tn|museum\\.om|nom\\.ve|rnrt\\.tn|hn\\.cn|com\\.fk|edu\\.dz|ne\\.kr|co\\.je|sch\\.uk|priv\\.pl|sp\\.br|net\\.hk|name\\.vn|com\\.sa|edu\\.bm|qc\\.ca|bolt\\.hu|per\\.kh|sn\\.cn|mil\\.id|kagawa\\.jp|utsunomiya\\.jp|erotica\\.hu|gd\\.cn|net\\.tr|edu\\.np|asn\\.au|com\\.gu|ind\\.tn|mil\\.br|net\\.lb|nom\\.co|org\\.la|mil\\.pl|ac\\.il|gov\\.jo|com\\.kw|edu\\.sh|otc\\.au|gmina\\.pl|per\\.sg|gov\\.mo|int\\.ve|news\\.hu|sec\\.ps|ac\\.pg|health\\.vn|sex\\.pl|net\\.nc|qc\\.com|idv\\.hk|org\\.hk|gok\\.pk|com\\.ac|tochigi\\.jp|gsm\\.pl|law\\.za|pro\\.vn|edu\\.pe|info\\.et|sch\\.gg|com\\.vn|gov\\.bm|com\\.cn|mod\\.uk|gov\\.ps|toyama\\.jp|gv\\.at|yk\\.ca|org\\.et|suli\\.hu|edu\\.my|org\\.mm|co\\.yu|int\\.ar|pe\\.ca|tm\\.hu|net\\.sb|org\\.yu|com\\.ru|com\\.pe|edu\\.kh|edu\\.kw|org\\.qa|med\\.om|net\\.ws|org\\.in|turystyka\\.pl|store\\.ve|org\\.bs|mil\\.uy|net\\.ar|iwate\\.jp|org\\.nc|us\\.tt|gov\\.sh|nom\\.fk|go\\.th|gov\\.ec|com\\.br|edu\\.do|gov\\.ng|pro\\.tt|sapporo\\.jp|net\\.ua|tm\\.fr|com\\.lv|com\\.mo|edu\\.uk|fin\\.ec|edu\\.ps|ru\\.com|edu\\.ec|ac\\.fj|net\\.mm|veterinaire\\.fr|nom\\.re|ingatlan\\.hu|fr\\.vu|ne\\.jp|int\\.co|gov\\.cy|org\\.lv|de\\.com|nagasaki\\.jp|com\\.sb|gov\\.za|org\\.lc|com\\.fj|ind\\.in|or\\.cr|sc\\.cn|chambagri\\.fr|or\\.jp|forum\\.hu|tmp\\.br|reklam\\.hu|gob\\.sv|com\\.pl|saitama\\.jp|name\\.tt|niigata\\.jp|sklep\\.pl|nom\\.ni|co\\.ma|net\\.la|co\\.om|pharmacien\\.fr|port\\.fr|mil\\.gu|au\\.tt|edu\\.gu|ngo\\.ph|com\\.ve|ac\\.th|gov\\.fj|barreau\\.fr|net\\.ac|ac\\.je|org\\.kw|sport\\.hu|ac\\.cn|net\\.bm|ibaraki\\.jp|tel\\.no|org\\.cy|edu\\.mo|gb\\.net|kyoto\\.jp|sch\\.sa|com\\.au|edu\\.lc|fax\\.nr|gov\\.mm|it\\.tt|org\\.jo|nat\\.tn|mil\\.ve|be\\.tt|org\\.az|rec\\.co|co\\.ve|gifu\\.jp|net\\.th|hokkaido\\.jp|ac\\.gg|go\\.kr|edu\\.ye|qh\\.cn|ab\\.ca|org\\.cn|no\\.com|co\\.uk|gov\\.gu|de\\.vu|miasta\\.pl|kawasaki\\.jp|co\\.cr|miyagi\\.jp|org\\.jp|osaka\\.jp|web\\.za|net\\.za|gov\\.pk|gov\\.vn|agrar\\.hu|asn\\.lv|org\\.sv|net\\.sh|org\\.sa|org\\.dz|assedic\\.fr|com\\.sy|net\\.ph|mil\\.ge|es\\.tt|mobile\\.nr|co\\.kr|ltd\\.uk|ac\\.be|fgov\\.be|geek\\.nz|ind\\.gg|net\\.mt|maori\\.nz|ens\\.tn|edu\\.py|gov\\.sd|gov\\.qa|nt\\.ca|com\\.pg|org\\.kh|pc\\.pl|com\\.eg|net\\.ly|se\\.com|gb\\.com|edu\\.ar|sch\\.je|mil\\.ac|mil\\.ar|okayama\\.jp|gov\\.sg|ac\\.id|co\\.id|com\\.ly|huissier-justice\\.fr|nic\\.im|gov\\.lv|nu\\.ca|org\\.sg|com\\.kh|org\\.vi|sa\\.cr|lg\\.jp|ns\\.ca|edu\\.co|gov\\.im|edu\\.om|net\\.dz|org\\.pl|pp\\.ru|tm\\.mt|org\\.ar|co\\.gg|org\\.im|edu\\.qa|org\\.py|edu\\.uy|targi\\.pl|com\\.ge|gub\\.uy|gov\\.ar|ltd\\.gg|fr\\.tt|net\\.qa|com\\.np|ass\\.dz|se\\.tt|com\\.ai|org\\.ma|plo\\.ps|co\\.at|med\\.sa|net\\.sg|kanazawa\\.jp|com\\.fr|school\\.za|net\\.pl|ngo\\.za|net\\.sy|ed\\.jp|org\\.na|net\\.ma|asso\\.fr|police\\.uk|powiat\\.pl|govt\\.nz|sk\\.ca|tj\\.cn|mil\\.ec|com\\.jo|net\\.mo|notaires\\.fr|avoues\\.fr|aeroport\\.fr|yn\\.cn|gov\\.et|gov\\.sa|gov\\.ae|com\\.tt|art\\.dz|firm\\.ve|com\\.sd|school\\.nz|edu\\.et|gob\\.pa|telecom\\.na|ac\\.cy|gz\\.cn|net\\.kw|mobil\\.nr|nic\\.uk|co\\.th|com\\.vu|com\\.re|belgie\\.be|nl\\.ca|uk\\.com|com\\.om|utazas\\.hu|presse\\.fr|co\\.ck|xz\\.cn|org\\.tr|mil\\.co|edu\\.cn|net\\.ec|on\\.ca|konyvelo\\.hu|gop\\.pk|net\\.om|info\\.ve|com\\.ni|sa\\.com|com\\.tr|sch\\.sd|fukushima\\.jp|tel\\.nr|atm\\.pl|kitakyushu\\.jp|com\\.qa|firm\\.co|edu\\.tt|games\\.hu|mil\\.nz|cri\\.nz|net\\.az|org\\.ge|mie\\.jp|net\\.mx|sch\\.ae|nieruchomosci\\.pl|int\\.vn|edu\\.za|com\\.cy|wakayama\\.jp|gov\\.hk|org\\.pa|edu\\.au|gov\\.in|pro\\.om|2000\\.hu|szkola\\.pl|shimane\\.jp|co\\.zw|gove\\.tw|com\\.co|net\\.ck|net\\.pk|net\\.ve|org\\.ru|uk\\.net|org\\.co|uu\\.mt|com\\.cu|mil\\.za|plc\\.uk|lkd\\.co\\.im|gs\\.cn|sex\\.hu|net\\.je|kumamoto\\.jp|mil\\.lb|edu\\.yu|gov\\.ws|sendai\\.jp|eu\\.org|ah\\.cn|net\\.vn|gov\\.sb|net\\.pe|nagoya\\.jp|geometre-expert\\.fr|net\\.fk|biz\\.tt|org\\.sh|edu\\.sa|saga\\.jp|sx\\.cn|org\\.je|org\\.ye|muni\\.il|kochi\\.jp|com\\.bh|org\\.ec|priv\\.at|gov\\.sy|org\\.ni|casino\\.hu|res\\.in|uy\\.com)"

      // insert it
      res = tldRes[1] + tldStr + tldRes[3];
    }
    return new RegExp(res + '$', "i");
  },

  _getBaseDomain : function(aUri) {
    try {
      return this._tld.getBaseDomain(aUri.QueryInterface(Ci.nsIURL));
    }
    catch(e) {
      // Just use the host
      return aUri.host;
    }
  },

  _isLinkExternal : function(aLink) {
    var isExternal;
    if ((aLink instanceof HTMLAnchorElement) && (aLink.target == "_self" || aLink.target == "_top")) {
      isExternal = false;
    }
    else {
      isExternal = this._isURIExternal(this._ios.newURI(aLink.href, null, null));
    }
    return isExternal;
  },

  _isURIExternal : function(aURI) {
    if (aURI.scheme == "javascript") {
      return false;
    }
    if (aURI.scheme != "http" && aURI.scheme != "https") {
      return true;
    }
    // Links from our host are always internal
    if (aURI.host == this._uri.host)
      return false;

    // Check whether URI is explicitly included
    if (WebAppProperties.include) {
      var includes = WebAppProperties.include.split(",");
      if (includes.some(function(pattern) { return this._convert2RegExp(pattern).test(aURI.host); }, this)) {
        return false;
      }
    }

    // Check whether URI is explicitly excluded
    if (WebAppProperties.exclude) {
      var excludes = WebAppProperties.exclude.split(",");
      if (excludes.some(function(pattern) { return this._convert2RegExp(pattern).test(aURI.host); }, this)) {
        return true;
      }
    }

    var linkDomain = this._getBaseDomain(aURI);
    // Can't use browser.currentURI since it causes reentrancy into the docshell.
    if (linkDomain == this._currentDomain)
      return false;
    else
      return true;
  },

  _dragOver : function(aEvent)
  {
    var dragService = Cc["@mozilla.org/widget/dragservice;1"].getService(Ci.nsIDragService);
    var dragSession = dragService.getCurrentSession();

    var supported = dragSession.isDataFlavorSupported("text/x-moz-url");
    if (!supported)
      supported = dragSession.isDataFlavorSupported("application/x-moz-file");

    if (supported)
      dragSession.canDrop = true;
  },

  _dragDrop : function(aEvent)
  {
    var dragService = Cc["@mozilla.org/widget/dragservice;1"].getService(Ci.nsIDragService);
    var dragSession = dragService.getCurrentSession();
    if (dragSession.sourceNode)
      return;

    var trans = Cc["@mozilla.org/widget/transferable;1"].createInstance(Ci.nsITransferable);
    trans.addDataFlavor("text/x-moz-url");
    trans.addDataFlavor("application/x-moz-file");

    var uris = [];
    for (var i=0; i<dragSession.numDropItems; i++) {
      var uri = null;

      dragSession.getData(trans, i);
      var flavor = {}, data = {}, length = {};
      trans.getAnyTransferData(flavor, data, length);
      if (data) {
        try {
          var str = data.value.QueryInterface(Ci.nsISupportsString);
        }
        catch(ex) {
        }

        if (str) {
          uri = this._ios.newURI(str.data.split("\n")[0], null, null);
        }
        else {
          var file = data.value.QueryInterface(Ci.nsIFile);
          if (file)
            uri = this._ios.newFileURI(file);
        }
      }

      if (uri)
        uris.push(uri);
    }

    if (WebAppProperties.script.dropFiles)
      WebAppProperties.script.dropFiles(uris);
  },

  _loadExternalURI : function(aURI) {
    var platform = Cc["@mozilla.org/platform-web-api;1"].createInstance(Ci.nsIPlatformGlue);
    var callback = {};
    var uriString = platform.getProtocolURI(aURI.spec, callback);
    if (callback.value) {
      callback.value.handleURI(aURI.spec);
      return;
    }
    if (uriString) {
      gBrowser.loadURI(uriString, null, null);
      return;
    }
    var extps = Cc["@mozilla.org/uriloader/external-protocol-service;1"].getService(Ci.nsIExternalProtocolService);
    extps.loadURI(aURI, null);
  },

  _domClick : function(aEvent)
  {
    var link = aEvent.target;

    if ((link.href != undefined) && WebRunner._isLinkExternal(link)) {
      aEvent.stopPropagation();
    }
  },

  _domActivate : function(aEvent)
  {
    var link = aEvent.target;

    if ((link.href != undefined) && WebRunner._isLinkExternal(link)) {
      // We don't want to open external links in this process: do so in the
      // default browser.
      var resolvedURI = WebRunner._ios.newURI(link.href, null, null);

      WebRunner._loadExternalURI(resolvedURI);

      aEvent.preventDefault();
      aEvent.stopPropagation();
    }
  },

  _prepareWebAppScript : function()
  {
    WebAppProperties.script["XMLHttpRequest"] = Components.Constructor("@mozilla.org/xmlextras/xmlhttprequest;1");
    WebAppProperties.script["window"] = this._getBrowser().contentWindow.wrappedJSObject;
    WebAppProperties.script["properties"] = WebAppProperties;
  },

  startup : function()
  {
    this._ios = Cc["@mozilla.org/network/io-service;1"].getService(Ci.nsIIOService);
    this._ios.offline = false; //force online even no network
    this._tld = Cc["@mozilla.org/network/effective-tld-service;1"].getService(Ci.nsIEffectiveTLDService);

    // Configure the window's chrome
    this._processConfig();

    var self = this;

    document.getElementById("popup_content").addEventListener("popupshowing", self._popupShowing, false);
    document.getElementById("tooltip_content").addEventListener("popupshowing", self._tooltipShowing, false);

    // Let osx make its app menu, then hide the window menu
    var mainMenu = document.getElementById("menu_main");
    if (mainMenu) {
      mainMenu.hidden = true;

      // Needed for linux or the menubar doesn't hide
      document.getElementById("menu_file").hidden = true;
    }

    // Remember the base domain of the web app
    if (WebAppProperties.uri) {
      var uriFixup = Cc["@mozilla.org/docshell/urifixup;1"].getService(Ci.nsIURIFixup);
      this._uri = uriFixup.createFixupURI(WebAppProperties.uri, Ci.nsIURIFixup.FIXUP_FLAG_NONE);
      try {
        this._currentDomain = this._getBaseDomain(this._uri);
      }
      catch(e) {
        // Doesn't have a domain (e.g. IP address)
        this._currentDomain = "";
      }
    }

    var browser = this._getBrowser();
    browser.addEventListener("DOMTitleChanged", function(aEvent) { self._domTitleChanged(aEvent); }, true);
    browser.webProgress.addProgressListener(this, Ci.nsIWebProgress.NOTIFY_ALL);

    if (!window.arguments || !window.arguments[0] || !(window.arguments[0] instanceof Ci.nsICommandLine)) {
      // Not the main window, so we're done
      return;
    }

    // Default the name of the window to the webapp name
    document.title = WebAppProperties.name;

    // Add handlers for the main page
    window.addEventListener("unload", function() { WebRunner.shutdown(); }, false);
//@line 692 "c:\mozilla\workdir\mozilla\192src\prism\chrome\content\webrunner.js"
    window.addEventListener("minimizing", function(event) { WebRunner.onMinimizing(event); }, false);
    window.addEventListener("closing", function(event) { WebRunner.onClosing(event); }, false);
    window.addEventListener("DOMActivate", function(event) { WebRunner.onActivate(event); }, false);
//@line 696 "c:\mozilla\workdir\mozilla\192src\prism\chrome\content\webrunner.js"

    var install = false;

    install = window.arguments[0].handleFlag("install-webapp", false);
    if (!install)
      install = (WebAppProperties.uri == null || WebAppProperties.name == null);

    // Hack to get the mime handler initialized correctly so the content handler dialog doesn't appear
    var hs = Cc["@mozilla.org/uriloader/handler-service;1"].getService(Ci.nsIHandlerService);
    var extps = Cc["@mozilla.org/uriloader/external-protocol-service;1"].getService(Ci.nsIExternalProtocolService);

    // Ensure login manager is up and running.
    Cc["@mozilla.org/login-manager;1"].getService(Ci.nsILoginManager);

    this._xulWindow = window.QueryInterface(Ci.nsIInterfaceRequestor)
        .getInterface(Ci.nsIWebNavigation)
        .QueryInterface(Ci.nsIDocShellTreeItem)
        .treeOwner
        .QueryInterface(Ci.nsIInterfaceRequestor)
        .getInterface(Ci.nsIXULWindow);

    // Do we need to handle making a web application?
    if (install) {
      // If the install is successful, launch the webapp
      window.openDialog("chrome://newapp/content/install-shortcut.xul", "install", "dialog=no,centerscreen", WebAppProperties,
        function(install, id, shortcut) { install.restart(id, shortcut); } );

      // Hide the main window so it doesn't flash on the screen before closing
      this._xulWindow.QueryInterface(Ci.nsIBaseWindow).visibility = false;

      // Since we are installing, we need to close the application
      window.close();
    }

//@line 731 "c:\mozilla\workdir\mozilla\192src\prism\chrome\content\webrunner.js"
    if (WebAppProperties.iconic && WebAppProperties.trayicon) {
      // Run as an icon. Right now we only support Windows system tray.
      this._xulWindow.QueryInterface(Ci.nsIBaseWindow).visibility = false;
    } 
//@line 736 "c:\mozilla\workdir\mozilla\192src\prism\chrome\content\webrunner.js"

    // Hookup the browser window callbacks
    this._xulWindow.XULBrowserWindow = this;
    window.QueryInterface(Ci.nsIDOMChromeWindow).browserDOMWindow =
      new nsBrowserAccess(this._getBrowser());

    window.addEventListener("close", function(event) {
      if (!self._handleWindowClose(event)) {
        event.preventDefault();
      }
    }, false);

    browser.addEventListener("dragover", function(aEvent) { self._dragOver(aEvent); }, true);
    browser.addEventListener("dragdrop", function(aEvent) { self._dragDrop(aEvent); }, true);
    browser.addEventListener("command", function(aEvent) { self._handleContentCommand(aEvent); }, false);

    // Register ourselves as the default window creator so we can control handling of external links
    this._windowCreator = Cc["@mozilla.org/toolkit/app-startup;1"].getService(Ci.nsIWindowCreator);
    var windowWatcher = Cc["@mozilla.org/embedcomp/window-watcher;1"].getService(Ci.nsIWindowWatcher);
    windowWatcher.setWindowCreator(this);

    // Register observer for quit-application-requested so we can handle shutdown (needed for OS X
    // dock Quit menu item, for example).
    var observerService = Cc["@mozilla.org/observer-service;1"].getService(Ci.nsIObserverService);
    observerService.addObserver(this, "quit-application-requested", false);
    observerService.addObserver(this, "session-save", false);

    setTimeout(function() { self._delayedStartup(); }, 0);
  },

  showTrayIcon : function() {
    try {
      var appIcon = WebAppProperties.getAppRoot();
      appIcon.append("icons");
      appIcon.append("default");
      appIcon.append(WebAppProperties.icon + ".ico");

      var ioService = Cc["@mozilla.org/network/io-service;1"].getService(Ci.nsIIOService);
      var iconUri = ioService.newFileURI(appIcon);

      var desktop = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIDesktopEnvironment);
      var icon = desktop.getApplicationIcon(this._getBrowser().contentWindow);
      icon.title = document.title;
      icon.imageSpec = iconUri.spec;
      icon.show();
    } catch (e) {
      Components.utils.reportError("unable to show tray icon: " + e);
    } 
  },

  showSplashScreen : function() {
    // Display the splash screen, if any
    if (WebAppProperties.splashscreen) {
      var ioService = Cc["@mozilla.org/network/io-service;1"].getService(Ci.nsIIOService);
      var splashFile = WebAppProperties.getAppRoot();
      splashFile.append(WebAppProperties.splashscreen);
      var splashUri = ioService.newFileURI(splashFile);
      document.getElementById("browser_content").setAttribute("src", splashUri.spec);
    }
  },

  shutdownQuery : function() {
    var platform = Cc["@mozilla.org/platform-web-api;1"].createInstance(Ci.nsIPlatformGlue);
    if (!platform.invokeShutdownCallback())
      return false;

    if (WebAppProperties.script.shutdown && !WebAppProperties.script.shutdown()) {
      return false;
    }
    
    this._saveSettings();

    return true;
  },

  shutdown : function()
  {
    if (WebAppProperties.trayicon) {
      var desktop = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIDesktopEnvironment);
      var icon = desktop.getApplicationIcon(this._getBrowser().contentWindow);
      icon.hide();
    }
  },

  tryClose : function()
  {
    var contentViewer = this._xulWindow.docShell.contentViewer;
    if (contentViewer && !contentViewer.permitUnload()) {
      return false;
    }
  },

  onMinimizing : function(event)
  {
    var desktop = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIDesktopEnvironment);
    var icon = desktop.getApplicationIcon(this._getBrowser().contentWindow);
    if (icon.behavior & Ci.nsIApplicationIcon.HIDE_ON_MINIMIZE) {
      this._xulWindow.QueryInterface(Ci.nsIBaseWindow).visibility = false;
    }
    this._minimizedState = window.windowState;
  },

  onClosing : function(event)
  {
    var desktop = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIDesktopEnvironment);
    var icon = desktop.getApplicationIcon(this._getBrowser().contentWindow);
    if (icon.behavior & Ci.nsIApplicationIcon.HIDE_ON_CLOSE) {
      this._xulWindow.QueryInterface(Ci.nsIBaseWindow).visibility = false;
      event.preventDefault();
    }
  },

  onActivate : function(event)
  {
    this._xulWindow.QueryInterface(Ci.nsIBaseWindow).visibility = true;

    var chromeWindow = window.QueryInterface(Ci.nsIDOMChromeWindow);
    if (chromeWindow.windowState == chromeWindow.STATE_MINIMIZED) {
      if (this._minimizedState == chromeWindow.STATE_MAXIMIZED) {
        chromeWindow.maximize();
      }
      else {
        chromeWindow.restore();
      }

      this._minimizedState = 0;
    }

    var desktop = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIDesktopEnvironment);
    desktop.setZLevel(window, Ci.nsIDesktopEnvironment.zLevelTop);
  },
  toggleStatusbar : function()
  {
    var statusbar = document.getElementById("statusbar");
    var collapsed = statusbar.getAttribute("collapsed") == "true";
    statusbar.setAttribute("collapsed", collapsed ? "false" : "true");
  },

  clearCache : function()
  {
     var cacheService = Cc["@mozilla.org/network/cache-service;1"].getService(Ci.nsICacheService);
     try {
       cacheService.evictEntries(Ci.nsICache.STORE_ANYWHERE);
     } catch(ex) {}
  },

  doCommand : function(aCmd) {
    switch (aCmd) {
      case "cmd_cut":
      case "cmd_copy":
      case "cmd_paste":
      case "cmd_delete":
      case "cmd_selectAll":
      case "cmd_copyLink":
        goDoCommand(aCmd);
        break;
      case "cmd_saveImage":
        var image = new XPCNativeWrapper(document.popupNode.QueryInterface(Ci.nsIImageLoadingContent));
        saveImageURL(image.currentURI.spec, null, "SaveImageTitle", false, false, document.documentURIObject);
        break;
      case "cmd_prefs":
        window.openDialog("chrome://webrunner/content/preferences/preferences.xul", "preferences", "chrome,titlebar,toolbar,centerscreen,dialog", "paneApplications");
        break;
      case "cmd_print":
        PrintUtils.print();
        break;
      case "cmd_pageSetup":
        PrintUtils.showPageSetup();
        break;
      case "cmd_about":
        window.openDialog("chrome://webrunner/content/about.xul", "about", "centerscreen,modal", WebAppProperties);
        break;
      case "cmd_back":
        this._getBrowser().goBack();
        break;
      case "cmd_forward":
        this._getBrowser().goForward();
        break;
      case "cmd_home":
        this._getBrowser().loadURI(WebAppProperties.uri, null, null);
        break;
      case "cmd_reload":
        this._getBrowser().reload();
        break;
      case "cmd_close":
        // If there is no XUL window set then this is a child window and we don't have to call handleWindowClose()
        if (!this._xulWindow || this._handleWindowClose())
          close();
        break;
      case "cmd_quit":
        goQuitApplication();
        break;
      case "cmd_console":
        window.open("chrome://global/content/console.xul", "_blank", "chrome,extrachrome,dependent,menubar,resizable,scrollbars,status,toolbar");
        break;
      case "cmd_install":
        window.openDialog("chrome://newapp/content/install-shortcut.xul", "install", "centerscreen,modal", WebAppProperties);
        break;
      case "cmd_sb":
        this.toggleStatusbar();
        break;
      case "cmd_clearcache":
        this.clearCache();
        this._getBrowser().reload();
        break;
      case "cmd_addons":
        const EMTYPE = "Extension:Manager";
        var wm = Cc["@mozilla.org/appshell/window-mediator;1"].getService(Ci.nsIWindowMediator);
        var theEM = wm.getMostRecentWindow(EMTYPE);
        if (theEM) {
          theEM.focus();
          return;
        }

        const EMURL = "chrome://mozapps/content/extensions/extensions.xul";
        const EMFEATURES = "chrome,menubar,extra-chrome,toolbar,dialog=no,resizable";
        window.openDialog(EMURL, "", EMFEATURES);
        break;
      case "cmd_fullScreen":
        var prefs = Cc["@mozilla.org/preferences-service;1"].getService(Ci.nsIPrefBranch);
        if (!prefs.getBoolPref("prism.shortcut.fullScreen.disabled")) {
          window.fullScreen = !window.fullScreen;
        }
        break;
      case "cmd_zoomIn":
        const max = 2.0;
        var tmp = this._zoomLevel;
        tmp += 0.3;

        if (tmp > max)
          tmp = max;

        this._zoomLevel = tmp;
        var markupDocumentViewer = this._getBrowser().markupDocumentViewer;
        markupDocumentViewer.fullZoom = this._zoomLevel;
        break;
      case "cmd_zoomOut":
        const min = .2;
        var tmp = this._zoomLevel;
        tmp -= .3;

        if (tmp < min)
          tmp = min;

        this._zoomLevel = tmp
        var markupDocumentViewer = this._getBrowser().markupDocumentViewer;
        markupDocumentViewer.fullZoom = this._zoomLevel;
        break;
      case "cmd_zoomReset":
        this._zoomLevel = 1;
        var markupDocumentViewer = this._getBrowser().markupDocumentViewer;
        markupDocumentViewer.fullZoom = this._zoomLevel;
        break;
      case "cmd_find":
        document.getElementById("findbar").onFindCommand();
        break;
      case "cmd_findNext":
        document.getElementById("findbar").onFindAgainCommand(false);
        break;
      case "cmd_findPrevious":
        document.getElementById("findbar").onFindAgainCommand(true);
        break;
      case "cmd_aboutConfig":
        var prefs = Cc["@mozilla.org/preferences-service;1"].getService(Ci.nsIPrefBranch);
        if (prefs.getBoolPref("prism.shortcut.aboutConfig.enabled")) {
          if (this._getBrowser().contentWindow.location.href != "about:config") {
            this._getBrowser().loadURI("about:config", null, null);
          }
          else {
            this._getBrowser().loadURI(WebAppProperties.uri, null, null);
          }
        }
        break;
    }
  },

  attachDocument : function(aDocument) {
    var self = this;
    
    try {
      // Remove handlers in case we already added them to this document
      aDocument.removeEventListener("click", self._domClick, true);
      aDocument.removeEventListener("DOMActivate", self._domActivate, true);
    }
    catch(e) {
      // Just ignore if we can't remove the event listeners since that probably means the document has just been created
    }
    
    aDocument.addEventListener("click", self._domClick, true);
    aDocument.addEventListener("DOMActivate", self._domActivate, true);
  },

  // nsIXULBrowserWindow implementation to display link destinations in the statusbar
  setJSStatus: function() { },
  setJSDefaultStatus: function() { },
  setOverLink: function(aStatusText, aLink) {
    var statusbar = document.getElementById("status");
    statusbar.label = aStatusText;
  },

  // nsIWebProgressListener implementation to monitor activity in the browser.
  _requestsStarted: 0,
  _requestsFinished: 0,

  // This method is called to indicate state changes.
  onStateChange: function(aWebProgress, aRequest, aStateFlags, aStatus) {
    if (aStateFlags & Ci.nsIWebProgressListener.STATE_IS_REQUEST) {
      if (aStateFlags & Ci.nsIWebProgressListener.STATE_START) {
        this._requestsStarted++;
      }
      else if (aStateFlags & Ci.nsIWebProgressListener.STATE_STOP) {
        this._requestsFinished++;
      }

      if (WebAppProperties.status && this._requestsStarted > 1) {
        var value = (100 * this._requestsFinished) / this._requestsStarted;
        var progress = document.getElementById("progress");
        progress.setAttribute("mode", "determined");
        progress.setAttribute("value", value);
      }
    }

    if (WebAppProperties.status && (aStateFlags & Ci.nsIWebProgressListener.STATE_IS_NETWORK)) {
      var progress = document.getElementById("progress");
      if (aStateFlags & Ci.nsIWebProgressListener.STATE_START) {
        progress.hidden = false;
      }
      else if (aStateFlags & Ci.nsIWebProgressListener.STATE_STOP) {
        progress.hidden = true;
        this.onStatusChange(aWebProgress, aRequest, 0, "Done");
        this._requestsStarted = this._requestsFinished = 0;
      }
    }

    if (aStateFlags & Ci.nsIWebProgressListener.STATE_IS_DOCUMENT) {
      if (aStateFlags & Ci.nsIWebProgressListener.STATE_START) {
        this._loadError = false;
      }
      else if (aStateFlags & Ci.nsIWebProgressListener.STATE_STOP) {
        var domDocument = aWebProgress.DOMWindow.document;
        this.attachDocument(domDocument);
        
        if (aWebProgress.DOMWindow == this._getBrowser().contentWindow) {
          if (aStatus != Components.results.NS_OK) {
            this._loadError = true;
          }
        }
      }
    }
  },

  // This method is called to indicate progress changes for the currently
  // loading page.
  onProgressChange: function(aWebProgress, aRequest, aCurSelf, aMaxSelf, aCurTotal, aMaxTotal) {
    if (WebAppProperties.status && this._requestsStarted == 1) {
      var progress = document.getElementById("progress");
      if (aMaxSelf == -1) {
        progress.setAttribute("mode", "undetermined");
      }
      else {
        var value = ((100 * aCurSelf) / aMaxSelf);
        progress.setAttribute("mode", "determined");
        progress.setAttribute("value", value);
      }
    }
  },

  // This method is called to indicate a change to the current location.
  onLocationChange: function(aWebProgress, aRequest, aLocation) {
    var urlbar = document.getElementById("urlbar");
    urlbar.value = aLocation.spec;

    var browser = this._getBrowser();
    var back = document.getElementById("cmd_back");
    var forward = document.getElementById("cmd_forward");

    try {
      var canGoBack = browser.canGoBack;
      var canGoForward = browser.canGoForward;
    }
    catch(e) {
      canGoBack = false;
      canGoForward = false;
    }

    back.setAttribute("disabled", !canGoBack);
    forward.setAttribute("disabled", !canGoForward);
  },

  // This method is called to indicate a status changes for the currently
  // loading page.  The message is already formatted for display.
  onStatusChange: function(aWebProgress, aRequest, aStatus, aMessage) {
    if (WebAppProperties.status) {
      var statusbar = document.getElementById("status");
      statusbar.setAttribute("label", aMessage);
    }
  },

  // This method is called when the security state of the browser changes.
  onSecurityChange: function(aWebProgress, aRequest, aState) {
    var security = document.getElementById("security");
    var browser = this._getBrowser();

    security.removeAttribute("label");
    switch (aState) {
      case Ci.nsIWebProgressListener.STATE_IS_SECURE | Ci.nsIWebProgressListener.STATE_SECURE_HIGH:
        security.setAttribute("level", "high");
        security.setAttribute("label", browser.contentWindow.location.host);
        break;
      case Ci.nsIWebProgressListener.STATE_IS_SECURE | Ci.nsIWebProgressListener.STATE_SECURE_MED:
        security.setAttribute("level", "med");
        security.setAttribute("label", browser.contentWindow.location.host);
        break;
      case Ci.nsIWebProgressListener.STATE_IS_SECURE | Ci.nsIWebProgressListener.STATE_SECURE_LOW:
        security.setAttribute("level", "low");
        security.setAttribute("label", browser.contentWindow.location.host);
        break;
      case Ci.nsIWebProgressListener.STATE_IS_BROKEN:
        security.setAttribute("level", "broken");
        break;
      case Ci.nsIWebProgressListener.STATE_IS_INSECURE:
      default:
        security.removeAttribute("level");
        break;
    }
  },

  createChromeWindow : function(parent, chromeFlags) {
    // Always use the app runner implementation
    return this._windowCreator.createChromeWindow(parent, chromeFlags);
  },

  createChromeWindow2 : function(parent, chromeFlags, contextFlags, uri, cancel) {
    if (uri && (uri.scheme != "chrome") && this._isURIExternal(uri)) {
      // Use default app to open external URIs
      this._loadExternalURI(uri);
      cancel.value = true;
    }
    else {
      return this._windowCreator.QueryInterface(Ci.nsIWindowCreator2).
        createChromeWindow2(parent, chromeFlags, contextFlags, uri, cancel);
    }
  },

  observe : function(aSubject, aTopic, aData) {
    if (aTopic == "quit-application-requested") {
      if (!this.shutdownQuery()) {
        aSubject.QueryInterface(Ci.nsISupportsPRBool).data = true;
        return;
      }

      var observerService = Cc["@mozilla.org/observer-service;1"].getService(Ci.nsIObserverService);
      observerService.removeObserver(this, "quit-application-requested");
    }
    else if (aTopic == "session-save") {
      aSubject.QueryInterface(Ci.nsISupportsPRBool).data = this.shutdownQuery();
    }
  },

  // We need to advertize that we support weak references.  This is done simply
  // by saying that we QI to nsISupportsWeakReference.  XPConnect will take
  // care of actually implementing that interface on our behalf.
  QueryInterface: function(aIID) {
    if (aIID.equals(Ci.nsIWebProgressListener) ||
        aIID.equals(Ci.nsISupportsWeakReference) ||
        aIID.equals(Ci.nsIXULBrowserWindow) ||
        aIID.equals(Ci.nsIWindowCreator) ||
        aIID.equals(Ci.nsIWindowCreator2) ||
        aIID.equals(Ci.nsIObserver) ||
        aIID.equals(Ci.nsISupports))
      return this;

    throw Components.results.NS_ERROR_NO_INTERFACE;
  }
};

function nsBrowserAccess(browser)
{
  this._browser = browser;
  this._platform = Cc["@mozilla.org/platform-web-api;1"].createInstance(Ci.nsIPlatformGlue);
}

nsBrowserAccess.prototype =
{
  QueryInterface : function(aIID)
  {
    if (aIID.equals(Ci.nsIBrowserDOMWindow) ||
        aIID.equals(Ci.nsISupports))
      return this;
    throw Components.results.NS_NOINTERFACE;
  },

  openURI : function(aURI, aOpener, aWhere, aContext)
  {
    // Check whether we have a JS callback for this URI
    var callback = {};
    var uriString = this._platform.getProtocolURI(aURI.spec, callback);
    if (callback.value) {
      callback.value.handleURI(aURI.spec);
      // Return a window to abort the load
      return this._browser.contentWindow;
    }

    // Drop through to default implementation
    return null;
  },

  isTabContentWindow : function(aWindow)
  {
    // Shouldn't ever get called
    throw Components.results.NS_ERROR_UNEXPECTED;
  }
};

function getNotificationBox(aWindow) {
  return document.getElementById("notifications");
}

// Needed for Venkman support
function toOpenWindowByType(inType, uri)
{
  var windowManager = Cc['@mozilla.org/appshell/window-mediator;1'].getService();
  var windowManagerInterface = windowManager.QueryInterface(Ci.nsIWindowMediator);

  var topWindow = windowManagerInterface.getMostRecentWindow(inType);
  if (topWindow)
    topWindow.focus();
  else
    window.open(uri, "_blank", "chrome,extrachrome,menubar,resizable,scrollbars,status,toolbar");
}
