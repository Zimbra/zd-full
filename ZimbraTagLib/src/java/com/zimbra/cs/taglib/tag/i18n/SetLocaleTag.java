/*
 * 
 */

package com.zimbra.cs.taglib.tag.i18n;

import com.zimbra.cs.taglib.tag.i18n.I18nUtil;

import java.io.*;
import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class SetLocaleTag extends SimpleTagSupport  {

	//
	// Data
	//

	protected String value;
	protected String variant;
	protected int scope = I18nUtil.DEFAULT_SCOPE_VALUE;

	//
	// Public methods
	//

	public void setValue(String value) {
		this.value = value;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public void setScope(String scope) {
		this.scope = I18nUtil.getScope(scope);
	}

	//
	// SimpleTag methods
	//

	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		Locale locale = I18nUtil.getLocale(this.value);
		pageContext.setAttribute(I18nUtil.DEFAULT_LOCALE_VAR, locale, this.scope);
		pageContext.getResponse().setLocale(locale);
	}

} // class SetLocaleTag