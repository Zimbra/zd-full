/*
 * 
 */
package com.zimbra.cs.taglib.tag.folder;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class ModifyFolderFreeBusyTag extends ZimbraSimpleTag {

    private String mId;
    private boolean mExclude;

    public void setId(String id) { mId = id; }
    public void setExclude(boolean exclude) { mExclude = exclude; }


    public void doTag() throws JspException, IOException {
        try {
            getMailbox().modifyFolderExcludeFreeBusy(mId, mExclude);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
