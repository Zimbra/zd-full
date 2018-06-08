/*
 * 
 */
package com.zimbra.cs.taglib.tag.filter;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFilterAction.MarkOp;
import com.zimbra.cs.zclient.ZFilterAction.ZMarkAction;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class MarkActionTag extends ZimbraSimpleTag {

    private MarkOp mOp;

    public void setOp(String op) throws ServiceException { mOp = MarkOp.fromProtoString(op); }

    public void doTag() throws JspException {
        FilterRuleTag rule = (FilterRuleTag) findAncestorWithClass(this, FilterRuleTag.class);
        if (rule == null)
                throw new JspTagException("The markAction tag must be used within a filterRule tag");
        rule.addAction(new ZMarkAction(mOp));
    }

}
