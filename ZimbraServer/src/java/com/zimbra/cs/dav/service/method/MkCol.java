/*
 * 
 */
package com.zimbra.cs.dav.service.method;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.zimbra.cs.dav.DavContext;
import com.zimbra.cs.dav.DavException;
import com.zimbra.cs.dav.resource.Collection;
import com.zimbra.cs.dav.resource.UrlNamespace;
import com.zimbra.cs.dav.service.DavMethod;

public class MkCol extends DavMethod {
	public static final String MKCOL  = "MKCOL";
	public String getName() {
		return MKCOL;
	}
	public void handle(DavContext ctxt) throws DavException, IOException {
		String user = ctxt.getUser();
		String name = ctxt.getItem();
		
		if (user == null || name == null)
			throw new DavException("invalid uri", HttpServletResponse.SC_NOT_ACCEPTABLE, null);
		
		Collection col = UrlNamespace.getCollectionAtUrl(ctxt, ctxt.getPath());
		col.mkCol(ctxt, name);
		ctxt.setStatus(HttpServletResponse.SC_CREATED);
	}
}
