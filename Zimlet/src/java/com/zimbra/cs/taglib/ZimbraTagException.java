/*
 * 
 */
package com.zimbra.cs.taglib;

import javax.servlet.jsp.JspTagException;

public class ZimbraTagException extends JspTagException {
	public static ZimbraTagException AUTH_FAILURE(String msg) {
		return new ZimbraTagException("missing auth: "+msg);
	}
	public static ZimbraTagException MISSING_ATTR(String msg) {
		return new ZimbraTagException("missing attribute: "+msg);
	}
	public static ZimbraTagException IO_ERROR(Throwable cause) {
		return new ZimbraTagException("io error", cause);
	}
	public static ZimbraTagException SERVICE_ERROR(Throwable cause) {
		return new ZimbraTagException("service error", cause);
	}
	
	public ZimbraTagException(String msg) {
		super(msg);
	}
	public ZimbraTagException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
