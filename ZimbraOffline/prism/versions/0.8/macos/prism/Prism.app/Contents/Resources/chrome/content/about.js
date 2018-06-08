/*  */

const Cc = Components.classes;
const Ci = Components.interfaces;

addEventListener("load", onload, false);

function onload(aEvent)
{
  if (aEvent.target != document)
    return;

  var bundle = Cc["@mozilla.org/intl/stringbundle;1"].getService(Ci.nsIStringBundleService);
  bundle = bundle.createBundle("chrome://webrunner/locale/brand/brand.properties");

  var appInfo = Cc["@mozilla.org/xre/app-info;1"].getService(Ci.nsIXULAppInfo);

  var version = document.getElementById("version");
  version.value = bundle.GetStringFromName("brandFullName") + " " + appInfo.version;

  var userAgent = document.getElementById("useragent");
  userAgent.value = navigator.userAgent;

  document.documentElement.getButton("accept").focus();
}
