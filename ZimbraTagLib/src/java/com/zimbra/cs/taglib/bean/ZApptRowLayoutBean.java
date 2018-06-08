/*
 * 
 */
package com.zimbra.cs.taglib.bean;

import java.util.List;
import java.util.Date;

public class ZApptRowLayoutBean {

    private List<ZApptCellLayoutBean> mCells;
    private int mRowNum;
    private long mTime;

    public ZApptRowLayoutBean(List<ZApptCellLayoutBean> cells, int rowNum, long time) {
        mCells = cells;
        mRowNum = rowNum;
        mTime = time;
    }

    public List<ZApptCellLayoutBean> getCells() {
        return mCells;
    }

    public int getRowNum() {
        return mRowNum;
    }

    public long getTime() {
        return mTime;
    }

    public Date getDate() {
        return new Date(mTime);
    }

    public long getScheduleOverlapCount() {
        int overlap = 0;
        ZApptDayLayoutBean day = null;
        for ( ZApptCellLayoutBean cell : mCells) {
            if (cell.getAppt() != null && cell.getDay() != day) {
                overlap++;
                day = cell.getDay();
            }
        }
        return overlap;
    }
}
