/*
 * 
 */
package com.zimbra.cs.taglib.tag.filter;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFilterAction.ZKeepAction;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class KeepActionTag extends ZimbraSimpleTag {

    public void doTag() throws JspException {
        FilterRuleTag rule = (FilterRuleTag) findAncestorWithClass(this, FilterRuleTag.class);
        if (rule == null)
                throw new JspTagException("The keepAction tag must be used within a filterRule tag");
        rule.addAction(new ZKeepAction());
    }
}
