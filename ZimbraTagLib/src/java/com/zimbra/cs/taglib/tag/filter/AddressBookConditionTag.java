/*
 * 
 */
package com.zimbra.cs.taglib.tag.filter;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFilterCondition.AddressBookOp;
import com.zimbra.cs.zclient.ZFilterCondition.ZAddressBookCondition;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class AddressBookConditionTag extends ZimbraSimpleTag {

    private AddressBookOp mOp;
    private String mHeader;


    public void setHeader(String header) { mHeader = header; }
    public void setOp(String op) throws ServiceException { mOp = AddressBookOp.fromString(op); }

    public void doTag() throws JspException {
        FilterRuleTag rule = (FilterRuleTag) findAncestorWithClass(this, FilterRuleTag.class);
        if (rule == null)
                throw new JspTagException("The addressBookCondition tag must be used within a filterRule tag");
        rule.addCondition(new ZAddressBookCondition(mOp, mHeader));
    }

}
