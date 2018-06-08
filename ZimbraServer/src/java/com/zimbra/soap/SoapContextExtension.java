/*
 * 
 */
package com.zimbra.soap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ZimbraLog;

public abstract class SoapContextExtension {
	
	
	private static List<SoapContextExtension> sExtensions = Collections.synchronizedList(new ArrayList<SoapContextExtension>());
	
	public static void register(String name, SoapContextExtension sce) {
		synchronized (sExtensions) {
			ZimbraLog.soap.info("Adding context extension: " + name);
			sExtensions.add(sce);
		}
	}
	
	public static void addExtensionHeaders(Element context, ZimbraSoapContext zsc, String requestedAccountId) throws ServiceException {
		SoapContextExtension[] exts = null;
		synchronized (sExtensions) {
			exts = new SoapContextExtension[sExtensions.size()];
			sExtensions.toArray(exts); //make a copy so that we keep lock on addExtensionHeader calls
		}
		for (SoapContextExtension sce : exts) {
			sce.addExtensionHeader(context, zsc, requestedAccountId);
		}
	}

	public abstract void addExtensionHeader(Element context, ZimbraSoapContext zsc,  String requestedAccountId) throws ServiceException;
}
