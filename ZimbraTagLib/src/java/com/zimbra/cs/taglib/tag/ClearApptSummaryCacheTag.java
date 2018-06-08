/*
 * 
 */

package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspException;
import java.io.IOException;

public class ClearApptSummaryCacheTag extends ZimbraSimpleTag {

    public void doTag() throws JspException, IOException {
        ZMailbox mbox = getMailbox();
        mbox.clearApptSummaryCache();
    }
}
