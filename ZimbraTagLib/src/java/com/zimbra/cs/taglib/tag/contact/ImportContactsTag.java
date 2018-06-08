/*
 * 
 */
package com.zimbra.cs.taglib.tag.contact;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZFileUploaderBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZMailbox.ZImportContactsResult;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class ImportContactsTag extends ZimbraSimpleTag {

    private String mVar;
    private String mFolderId;
    private String mType = ZMailbox.CONTACT_IMPORT_TYPE_CSV;
    private ZFileUploaderBean mUploader;

    public void setUploader(ZFileUploaderBean uploader) { mUploader = uploader; }

    public void setVar(String var) { mVar = var; }
    public void setFolderid(String folderid) { mFolderId = folderid; }
    public void setType(String type) { mType = type; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();
            String attachmentId = mUploader.getUploadId(mbox);
            if (attachmentId != null) {
                ZImportContactsResult result = mbox.importContacts(mFolderId, mType, attachmentId);
                jctxt.setAttribute(mVar, result, PageContext.PAGE_SCOPE);
            }
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
