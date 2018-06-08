/*
 * 
 */

ZmCalWeekView = function(parent, posStyle, controller, dropTgt) {
	ZmCalColView.call(this, parent, posStyle, controller, dropTgt, ZmId.VIEW_CAL_WEEK, 7, false);
}

ZmCalWeekView.prototype = new ZmCalColView;
ZmCalWeekView.prototype.constructor = ZmCalWeekView;

ZmCalWeekView.prototype.toString = 
function() {
	return "ZmCalWeekView";
}
