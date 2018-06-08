/*
 * 
 */
package com.zimbra.cs.taglib.tag.contact;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class FieldTag extends ZimbraSimpleTag {

    private String mName;
    private String mValue;

    public void setValue(String value) { mValue = value; }
    public void setName(String name) { mName = name; }

    public void doTag() throws JspException {
        ContactOpTag op = (ContactOpTag) findAncestorWithClass(this, ContactOpTag.class);
        if (op == null)
                throw new JspTagException("The field tag must be used within a create/modify contact tag");
        op.addAttr(mName, mValue);
    }

}
