/*
 * 
 */
package com.zimbra.cs.taglib.tag.filter;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFilterAction.ZRedirectAction;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class RedirectActionTag extends ZimbraSimpleTag {

    private String mAddress;

    public void setAddress(String address) { mAddress = address; }

    public void doTag() throws JspException {
        FilterRuleTag rule = (FilterRuleTag) findAncestorWithClass(this, FilterRuleTag.class);
        if (rule == null)
                throw new JspTagException("The redirectAction tag must be used within a filterRule tag");
        rule.addAction(new ZRedirectAction(mAddress));
    }

}
