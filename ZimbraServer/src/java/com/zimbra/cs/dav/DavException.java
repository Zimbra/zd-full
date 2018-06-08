/*
 * 
 */
package com.zimbra.cs.dav;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;

@SuppressWarnings("serial")
public class DavException extends Exception {
	protected boolean mStatusIsSet;
	protected int mStatus;
	protected Document mErrMsg;
	
	public DavException(String msg, int status) {
		super(msg);
		mStatus = status;
		mStatusIsSet = true;
	}
	
	public DavException(String msg, Throwable cause) {
		super(msg, cause);
		mStatusIsSet = false;
	}
	
	public DavException(String msg, int status, Throwable cause) {
		super(msg, cause);
		mStatus = status;
		mStatusIsSet = true;
	}

	public boolean isStatusSet() {
		return mStatusIsSet;
	}
	
	public int getStatus() {
		return mStatus;
	}
	
	public boolean hasErrorMessage() {
		return (mErrMsg != null);
	}
	
	public Element getErrorMessage() {
		if (mErrMsg == null)
			return null;
		return mErrMsg.getRootElement();
	}
	public void writeErrorMsg(OutputStream out) throws IOException {
		DomUtil.writeDocumentToStream(mErrMsg, out);
	}
	
	protected static class DavExceptionWithErrorMessage extends DavException {
		protected DavExceptionWithErrorMessage(String msg, int status) {
			super(msg, status);
			mErrMsg = org.dom4j.DocumentHelper.createDocument();
			mErrMsg.addElement(DavElements.E_ERROR);
		}
		protected void setError(QName error) {
			mErrMsg.getRootElement().addElement(error);
		}
	}
	public static class CannotModifyProtectedProperty extends DavExceptionWithErrorMessage {
		public CannotModifyProtectedProperty(QName prop) {
			super("property "+prop.getName()+" is protected", HttpServletResponse.SC_FORBIDDEN);
			setError(DavElements.E_CANNOT_MODIFY_PROTECTED_PROPERTY);
		}
	}
	public static class PropFindInfiniteDepthForbidden extends DavExceptionWithErrorMessage {
	    public PropFindInfiniteDepthForbidden() {
	        super("PROPFIND with infinite depth forbidden", HttpServletResponse.SC_FORBIDDEN);
	        setError(DavElements.E_PROPFIND_FINITE_DEPTH);
	    }
	}
}
