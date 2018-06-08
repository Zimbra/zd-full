/*
 * 
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZAppointmentHit;

public class ZAppointmentHitBean extends ZSearchHitBean {

    private ZAppointmentHit mHit;

    public ZAppointmentHitBean(ZAppointmentHit hit) {
        super(hit, HitType.appointment);
        mHit = hit;
    }
    public ZAppointmentHit getAppointment() {
        return mHit;
    }

    public String getDocId() {
        return mHit.getId();
    }

    public String getDocSortField() {
        return mHit.getSortField();
    }
}
