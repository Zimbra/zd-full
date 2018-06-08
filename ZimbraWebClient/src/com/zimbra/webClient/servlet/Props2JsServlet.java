/*
 * 
 */

package com.zimbra.webClient.servlet;

import com.zimbra.common.util.ZimbraLog;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Props2JsServlet extends com.zimbra.kabuki.servlets.Props2JsServlet {

	//
	// Constants
	//

	protected static final String P_SKIN = "skin";
	protected static final String A_SKIN = P_SKIN;

	//
	// Protected methods
	//

	protected String getSkin(HttpServletRequest req) {
		String skin = (String)req.getAttribute(A_SKIN);
		if (skin == null) {
			skin = req.getParameter(P_SKIN);
		}
		return skin;
	}

	//
	// com.zimbra.kabuki.servlets.Props2JsServlet methods
	//

	protected String getRequestURI(HttpServletRequest req) {
		return this.getSkin(req) + super.getRequestURI(req);
	}

	protected List<String> getBasenamePatternsList(HttpServletRequest req) {
		List<String> list = super.getBasenamePatternsList(req);
		String skin = this.getSkin(req);
		String patterns = "skins/"+skin+"/messages/${name},skins/"+skin+"/keys/${name}";
		list.add(patterns);
		return list;
	};

	//
	// com.zimbra.kabuki.servlets.Props2JsServlet methods
	//

	protected boolean isWarnEnabled() {
		return ZimbraLog.webclient.isWarnEnabled();
	}
	protected boolean isErrorEnabled() {
		return ZimbraLog.webclient.isErrorEnabled();
	}
	protected boolean isDebugEnabled() {
		return ZimbraLog.webclient.isDebugEnabled();
	}

	protected void warn(String message) {
		ZimbraLog.webclient.warn(message);
	}
	protected void error(String message) {
		ZimbraLog.webclient.error(message);
	}
	protected void debug(String message) {
		ZimbraLog.webclient.debug(message);
	}

} // class Props2JsServlet