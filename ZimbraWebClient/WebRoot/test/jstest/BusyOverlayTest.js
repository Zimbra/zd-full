/*
 * 
 */

function BusyOverlayTest() {
}


BusyOverlayTest.run =
function() {
	var shell = new DwtShell("MainShell", false, null, null, false);
	shell.setBusyDialogTitle("Searching...");
	shell.setBusyDialogText("Your search is in progress<br>Please Wait...");
	shell.setBusy(true, null, true, 0, new AjxCallback(null, BusyOverlayTest.cancelCallback));
}

BusyOverlayTest.cancelCallback =
function(ev) {
	alert("Cancel Clicked");
}

