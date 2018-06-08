/*
 * 
 */
package com.zimbra.cs.taglib.tag.filter;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFilterCondition.SizeOp;
import com.zimbra.cs.zclient.ZFilterCondition.ZSizeCondition;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class SizeConditionTag extends ZimbraSimpleTag {

    private SizeOp mOp;
    private String mValue;


    public void setValue(String value) { mValue = value; }
    public void setOp(String op) throws ServiceException { mOp = SizeOp.fromString(op); }

    public void doTag() throws JspException {
        FilterRuleTag rule = (FilterRuleTag) findAncestorWithClass(this, FilterRuleTag.class);
        if (rule == null)
                throw new JspTagException("The sizeCondition tag must be used within a filterRule tag");
        rule.addCondition(new ZSizeCondition(mOp, mValue));
    }

}
