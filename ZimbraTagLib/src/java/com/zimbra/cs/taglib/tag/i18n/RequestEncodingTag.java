/*
 * 
 */

package com.zimbra.cs.taglib.tag.i18n;

import java.io.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

public class RequestEncodingTag extends SimpleTagSupport  {

	//
	// Data
	//

	protected String value;

	//
	// Public methods
	//

	public void setValue(String value) {
		this.value = value;
	}

	//
	// SimpleTag methods
	//

	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();

		String encoding = this.value;
		if (encoding == null) encoding = pageContext.getResponse().getCharacterEncoding();
		pageContext.getRequest().setCharacterEncoding(encoding);

		// clear state
		this.value = null;
	}

} // class RequestEncodingTag