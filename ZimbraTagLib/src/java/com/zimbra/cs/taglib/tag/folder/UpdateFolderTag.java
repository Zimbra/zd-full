/*
 * 
 */
package com.zimbra.cs.taglib.tag.folder;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFolder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class UpdateFolderTag extends ZimbraSimpleTag {

    private String mId;
    private ZFolder.Color mColor;
    private String mName;
    private String mParentId;
    private String mFlags;
    private String mRgb;

    public void setId(String id) { mId = id; }
    public void setName(String name) { mName = name; }
    public void setParentid(String parentId) { mParentId = parentId; }
    public void setFlags(String flags) { mFlags = flags; }
    public void setColor(String color) throws ServiceException { mColor = ZFolder.Color.fromString(color); }
    public void setRgb(String rgb) { mRgb = rgb; }

    public void doTag() throws JspException, IOException {
        try {
            getMailbox().updateFolder(
                    mId,
                    StringUtil.isNullOrEmpty(mName) ? null : mName,
                    StringUtil.isNullOrEmpty(mParentId) ? null : mParentId,
                    mColor,
                    mRgb,
                    mFlags == null ? null : mFlags,
                    null);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
