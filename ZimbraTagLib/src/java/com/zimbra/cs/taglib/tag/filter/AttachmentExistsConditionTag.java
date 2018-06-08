/*
 * 
 */
package com.zimbra.cs.taglib.tag.filter;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFilterCondition.ZAttachmentExistsCondition;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class AttachmentExistsConditionTag extends ZimbraSimpleTag {

    private boolean mExists;

    public void setOp(String op) throws ServiceException { mExists = op.equalsIgnoreCase("EXISTS"); }

    public void doTag() throws JspException {
        FilterRuleTag rule = (FilterRuleTag) findAncestorWithClass(this, FilterRuleTag.class);
        if (rule == null)
                throw new JspTagException("The attachmentExistsCondition tag must be used within a filterRule tag");
        rule.addCondition(new ZAttachmentExistsCondition(mExists));
    }

}
