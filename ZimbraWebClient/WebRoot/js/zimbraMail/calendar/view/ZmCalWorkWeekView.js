/*
 * 
 */

ZmCalWorkWeekView = function(parent, posStyle, controller, dropTgt) {
    var workingDays = ZmCalBaseView.parseWorkingHours(ZmCalBaseView.getWorkingHours()),
        numOfWorkingDays = 0,
        i;
    for(i=0; i<workingDays.length; i++) {
        if(workingDays[i].isWorkingDay) {
            numOfWorkingDays++;    
        }
    }
	ZmCalColView.call(this, parent, posStyle, controller, dropTgt, ZmId.VIEW_CAL_WORK_WEEK, numOfWorkingDays, false);
}

ZmCalWorkWeekView.prototype = new ZmCalColView;
ZmCalWorkWeekView.prototype.constructor = ZmCalWorkWeekView;

ZmCalWorkWeekView.prototype.toString = 
function() {
	return "ZmCalWorkWeekView";
}


