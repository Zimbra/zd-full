/*
 * 
 */

package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZAppointmentHit;

import java.util.Collections;
import java.util.List;

public class ZApptSummariesBean {

    private List<ZAppointmentHit> mAppts;

    public ZApptSummariesBean(List<ZAppointmentHit> appts) {
        mAppts = appts;
        Collections.sort(mAppts, new ZAppointmentHit.SortByTimeDurationFolder());
    }

    public int getSize() { return mAppts.size(); }

    public List<ZAppointmentHit> getAppointments() {
        return mAppts;
    }
}
