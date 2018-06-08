/*
 * 
 */
package com.zimbra.cs.taglib.tag.filter;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFilterAction.ZFileIntoAction;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class FileIntoActionTag extends ZimbraSimpleTag {

    private String mPath;

    public void setPath(String path) { mPath = path; }

    public void doTag() throws JspException {
        FilterRuleTag rule = (FilterRuleTag) findAncestorWithClass(this, FilterRuleTag.class);
        if (rule == null)
                throw new JspTagException("The fileIntoAction tag must be used within a filterRule tag");
        rule.addAction(new ZFileIntoAction(mPath));
    }

}
