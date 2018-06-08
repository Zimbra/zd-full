/*
 * 
 */

package com.zimbra.cs.taglib.tag.i18n;

import java.io.*;
import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class TimeZoneTag extends BodyTagSupport {

	//
	// Data
	//

	protected TimeZone value;

	//
	// Public methods
	//

	public TimeZone getTimeZone() {
		return this.value;
	}

	// properties

	public void setValue(Object value) {
		if (value instanceof String) {
			this.value = TimeZone.getTimeZone(String.valueOf(value));
		}
		else {
			this.value = (TimeZone)value;
		}
	}

	//
	// TagSupport methods
	//

	public int doStartTag() throws JspException {
		// NOTE: This is to keep compatibility with JSTL
		if (this.value == null) {
			this.value = TimeZone.getTimeZone("GMT");
		}
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		this.value = null;
		return EVAL_PAGE;
	}

} // class TimeZoneTag