/*
 * 
 */

package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import java.io.IOException;

public class ClearSearchCacheTag extends ZimbraSimpleTag {

    private String mType;

    public void setType(String type) { this.mType = type; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        ZMailbox mbox = getMailbox();
        mbox.clearSearchCache(mType);
    }
}
