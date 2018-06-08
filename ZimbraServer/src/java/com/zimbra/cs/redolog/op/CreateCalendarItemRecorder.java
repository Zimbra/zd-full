/*
 * 
 */

package com.zimbra.cs.redolog.op;

public interface CreateCalendarItemRecorder {
    public void setCalendarItemAttrs(int id, int folderId);
    public void setCalendarItemPartStat(String partStat);
}
