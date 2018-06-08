/*
 * 
 */

package com.zimbra.cs.taglib.tag.calendar;

import com.zimbra.cs.taglib.bean.ZApptSummariesBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZAppointmentHit;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;

public class ForEachAppointmentTag extends ZimbraSimpleTag {

    private String mVar;
    private long mStart = -1;
    private long mEnd = -1;
    private ZApptSummariesBean mAppointments;

    public void setVar(String var) { this.mVar = var; }
    public void setStart(long start) { this.mStart = start; }
    public void setEnd(long end) { this.mEnd = end; }
    public void setAppointments(ZApptSummariesBean appts) { this.mAppointments = appts; }

    public void doTag() throws JspException, IOException {
        JspFragment body = getJspBody();
        if (body == null || mAppointments == null) return;
        JspContext jctxt = getJspContext();
        for (ZAppointmentHit appt : mAppointments.getAppointments()) {
            if (mStart == -1 || mEnd ==-1 || appt.isInRange(mStart, mEnd)) {
                jctxt.setAttribute(mVar, appt);
                body.invoke(null);
            }
        }
    }
}
