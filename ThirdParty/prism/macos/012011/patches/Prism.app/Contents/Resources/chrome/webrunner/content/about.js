/*  */

const Cc = Components.classes;
const Ci = Components.interfaces;

addEventListener("load", onload, false);

function onload(aEvent)
{
  if (aEvent.target != document)
    return;

  var bundle = Cc["@mozilla.org/intl/stringbundle;1"].getService(Ci.nsIStringBundleService);
  bundle = bundle.createBundle("chrome://branding/content/brand.properties");

  var appInfo = Cc["@mozilla.org/xre/app-info;1"].getService(Ci.nsIXULAppInfo);

  var version = document.getElementById("version");
  version.value = bundle.GetStringFromName("brandFullName") + " @version@ (build @buildid@)";

  var userAgent = document.getElementById("useragent");
  userAgent.value = "Copyright (c) 2008-2011 VMware Inc. \nAll rights reserved.";

  var credits = document.getElementById("credits");
  if (window.arguments && window.arguments[0]) {
    credits.value = window.arguments[0].credits.replace("\\n", "\n", "g");
  }

  if (credits.value.length == 0) {
    document.getElementById("box_credits").hidden = true;
    document.getElementById("about").height -= 50;
  }

  document.documentElement.getButton("accept").focus();
}

function checkForUpdates()
{
  var um = Cc["@mozilla.org/updates/update-manager;1"].getService(Components.interfaces.nsIUpdateManager);
  var prompter = Cc["@mozilla.org/updates/update-prompt;1"].createInstance(Components.interfaces.nsIUpdatePrompt);
  
  // If there's an update ready to be applied, show the "Update Downloaded"
  // UI instead and let the user know they have to restart the browser for
  // the changes to be applied. 
  if (um.activeUpdate && um.activeUpdate.state == "pending")
    prompter.showUpdateDownloaded(um.activeUpdate);
  else
    prompter.checkForUpdates();
}
