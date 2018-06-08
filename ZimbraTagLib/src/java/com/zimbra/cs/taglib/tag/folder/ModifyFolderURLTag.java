/*
 * 
 */
package com.zimbra.cs.taglib.tag.folder;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class ModifyFolderURLTag extends ZimbraSimpleTag {

    private String mId;
    private String mURL;

    public void setId(String id) { mId = id; }
    public void setUrl(String url) { mURL = url; }


    public void doTag() throws JspException, IOException {
        try {
            if (mURL != null && mURL.length() > 0)  getMailbox().modifyFolderURL(mId, mURL);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
