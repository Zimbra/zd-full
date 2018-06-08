/*
 * 
 */
package com.zimbra.cs.taglib.tag.contact;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZTagLibException;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class CreateContactTag extends ContactOpTag {

    private String mFolderid;
    private String mVar;
    private String mTagids;

    public void setFolderid(String folderid) { mFolderid = folderid; }
    public void setVar(String var) { mVar = var; }
    public void setTags(String tagids) { mTagids = tagids; }

    public void doTag() throws JspException, IOException {
        try {
            getJspBody().invoke(null);
            
            if (mAttrs.isEmpty() || allFieldsEmpty())
                throw ZTagLibException.EMPTY_CONTACT("can't create an empty contact", null);

            String id = getMailbox().createContact(mFolderid, mTagids, mAttrs).getId();
            getJspContext().setAttribute(mVar, id, PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
