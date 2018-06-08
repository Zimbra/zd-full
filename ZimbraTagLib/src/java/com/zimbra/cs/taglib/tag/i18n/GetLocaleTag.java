/*
 * 
 */

package com.zimbra.cs.taglib.tag.i18n;

import com.zimbra.cs.taglib.tag.i18n.I18nUtil;

import java.io.*;
import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class GetLocaleTag extends SimpleTagSupport  {

	//
	// Data
	//

	protected String var;
	protected int scope = I18nUtil.DEFAULT_SCOPE_VALUE;

	//
	// Public methods
	//

	public void setVar(String var) {
		this.var = var;
	}

	public void setScope(String scope) {
		this.scope = I18nUtil.getScope(scope);
	}

	//
	// SimpleTag methods
	//

	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		Locale locale = I18nUtil.findLocale(pageContext);
		if (this.var == null) {
			pageContext.getOut().print(String.valueOf(locale));
		}
		else {
			pageContext.setAttribute(this.var, locale, this.scope);
		}
	}

} // class GetLocaleTag