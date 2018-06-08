/*
 * 
 */

package com.zimbra.cs.taglib.tag.i18n;

import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class BundleTag extends TagSupport {

	//
	// Data
	//

	protected ResourceBundle bundle;

	protected String basename;
	protected String prefix;
	protected boolean force;

	//
	// Public methods
	//

	public ResourceBundle getBundle() {
		return this.bundle;
	}

	public String getPrefix() {
		return this.prefix;
	}

	// properties

	public void setBasename(String basename) {
		this.basename = basename;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	//
	// TagSupport methods
	//

	public int doStartTag() throws JspException {
		PageContext pageContext = super.pageContext;
		String basename = I18nUtil.makeBasename(pageContext, this.basename);
		this.bundle = I18nUtil.findBundle(pageContext, null, -1, basename);
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		this.basename = null;
		this.prefix = null;
		this.bundle = null;
		this.force = false;

		return EVAL_PAGE;
	}

} // class BundleTag