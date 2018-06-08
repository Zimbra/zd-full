/*
 * 
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZAppointmentHit;

public class ZApptCellLayoutBean {
    private boolean mIsFirst;
    private ZAppointmentHit mAppt;
    private long mRowSpan;
    private long mColSpan;
    private long mDaySpan;
    private long mWidth;
    private ZApptDayLayoutBean mDay;

    public ZApptCellLayoutBean(ZApptDayLayoutBean day) {
        mDay = day;
    }


    public ZApptDayLayoutBean getDay() {
        return mDay;
    }
    
    public boolean isIsFirst() {
        return mIsFirst;
    }

    public void setIsFirst(boolean isFirst) {
        mIsFirst = isFirst;
    }

    public ZAppointmentHit getAppt() {
        return mAppt;
    }

    public void setAppt(ZAppointmentHit appt) {
        mAppt = appt;
    }

    public long getRowSpan() {
        return mRowSpan;
    }

    public void setRowSpan(long rowSpan) {
        mRowSpan = rowSpan;
    }

    public long getColSpan() {
        return mColSpan;
    }

    public void setColSpan(long colSpan) {
        mColSpan = colSpan;
    }

    public long getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public void setDaySpan(long daySpan){
        mDaySpan = daySpan;
    }

    public long getDaySpan(){
        return mDaySpan;
    }

}
