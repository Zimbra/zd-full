/*
 * 
 */
package com.zimbra.cs.dav.service.method;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.dav.DavContext;
import com.zimbra.cs.dav.DavException;
import com.zimbra.cs.dav.resource.DavResource;
import com.zimbra.cs.dav.service.DavMethod;

public class Post extends DavMethod {
	public static final String POST  = "POST";
	public String getName() {
		return POST;
	}
	public void handle(DavContext ctxt) throws DavException, IOException, ServiceException {
		String user = ctxt.getUser();
		String name = ctxt.getItem();
		
		if (user == null || name == null)
			throw new DavException("invalid uri", HttpServletResponse.SC_NOT_FOUND);
		
		DavResource rs = ctxt.getRequestedResource();
		rs.handlePost(ctxt);
		sendResponse(ctxt);
	}
}
