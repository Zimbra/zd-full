/*
 * 
 */
package com.zimbra.cs.taglib.tag.filter;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFilterAction.ZTagAction;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class TagActionTag extends ZimbraSimpleTag {

    private String mTag;

    public void setTag(String tag) { mTag = tag; }

    public void doTag() throws JspException {
        FilterRuleTag rule = (FilterRuleTag) findAncestorWithClass(this, FilterRuleTag.class);
        if (rule == null)
                throw new JspTagException("The tagAction tag must be used within a filterRule tag");
        rule.addAction(new ZTagAction(mTag));
    }

}
