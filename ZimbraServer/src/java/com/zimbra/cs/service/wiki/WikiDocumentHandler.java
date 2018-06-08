/*
 * 
 */
package com.zimbra.cs.service.wiki;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.service.mail.MailDocumentHandler;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.soap.ZimbraSoapContext;

public abstract class WikiDocumentHandler extends MailDocumentHandler {
    private static final String[] TARGET_ID_PATH = new String[] { MailConstants.E_WIKIWORD, MailConstants.A_ID };
    private static final String[] TARGET_DOC_ID_PATH = new String[] { MailConstants.E_DOC, MailConstants.A_ID };
    private static final String[] TARGET_FOLDER_PATH = new String[] { MailConstants.E_WIKIWORD, MailConstants.A_FOLDER };
    private static final String[] TARGET_DOC_FOLDER_PATH = new String[] { MailConstants.E_DOC, MailConstants.A_FOLDER };
    protected String[] getProxiedIdPath(Element request)     {
        String[] path = TARGET_ID_PATH;
    	String id = getXPath(request, path);
    	if (id == null) {
    	    path = TARGET_DOC_ID_PATH;
            id = getXPath(request, path);
    	}
    	if (id == null) {
            path = TARGET_FOLDER_PATH;
            id = getXPath(request, path);
    	}
        if (id == null) {
            path = TARGET_DOC_FOLDER_PATH;
            id = getXPath(request, path);
        }
    	return path; 
    }
    protected boolean checkMountpointProxy(Element request)  { return true; }

	protected String getAuthor(ZimbraSoapContext zsc) throws ServiceException {
		return getAuthenticatedAccount(zsc).getName();
	}
	
	protected ItemId getRequestedFolder(Element request, ZimbraSoapContext zsc) throws ServiceException {
		for (Element elem : request.listElements()) {
	        String fid = elem.getAttribute(MailConstants.A_FOLDER, null);
	        if (fid != null) {
	        	return new ItemId(fid, zsc);
	        }
		}
		return null;
	}
	
	protected void checkNotebookEnabled(ZimbraSoapContext zsc) throws ServiceException {
		/*
		Account requestedAccount = getRequestedAccount(zsc);
		if (!requestedAccount.getBooleanAttr(Provisioning.A_zimbraFeatureNotebookEnabled, false))
			throw WikiServiceException.NOT_ENABLED();
		 */
	}
	
	protected void checkBriefcaseEnabled(ZimbraSoapContext zsc) throws ServiceException {
		/*
		Account requestedAccount = getRequestedAccount(zsc);
		if (!requestedAccount.getBooleanAttr(Provisioning.A_zimbraFeatureBriefcasesEnabled, false))
			throw WikiServiceException.BRIEFCASES_NOT_ENABLED();
		 */
	}
}
