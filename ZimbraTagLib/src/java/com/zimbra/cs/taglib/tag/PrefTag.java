/*
 * 
 */
package com.zimbra.cs.taglib.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class PrefTag extends ZimbraSimpleTag {

    private String mName;
    private String mValue;

    public void setValue(String value) { mValue = value; }
    public void setName(String name) { mName = name; }

    public void doTag() throws JspException {
        ModifyPrefsTag op = (ModifyPrefsTag) findAncestorWithClass(this, ModifyPrefsTag.class);
        if (op == null)
                throw new JspTagException("The pref tag must be used within a modifyPrefs tag");
        op.addPref(mName, mValue);
    }

}
