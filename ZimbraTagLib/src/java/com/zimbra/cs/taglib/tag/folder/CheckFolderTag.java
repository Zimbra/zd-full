/*
 * 
 */
package com.zimbra.cs.taglib.tag.folder;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class CheckFolderTag extends ZimbraSimpleTag {

    private String mId;
    private boolean mChecked;

    public void setId(String id) { mId = id; }
    public void setChecked(boolean checked) { mChecked = checked; }


    public void doTag() throws JspException, IOException {
        try {
            getMailbox().modifyFolderChecked(mId, mChecked);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
