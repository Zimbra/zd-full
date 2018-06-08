
/*
 * 
 */

ZmBuddyListPopup = function(params) {
	ZmTaskbarPopup.call(this, params);
	var overviewArgs = {
//		parentElement: parentElement,
		posStyle: Dwt.STATIC_STYLE,
		noAssistant: true,
		expanded: true,
		singleClick: true,
		noHeaderNodeCell: true
	};
	new ZmImOverview(this, overviewArgs);
};

ZmBuddyListPopup.prototype = new ZmTaskbarPopup;
ZmBuddyListPopup.prototype.constructor = ZmBuddyListPopup;

ZmBuddyListPopup.prototype.toString =
function() {
	return "ZmBuddyListPopup";
};
