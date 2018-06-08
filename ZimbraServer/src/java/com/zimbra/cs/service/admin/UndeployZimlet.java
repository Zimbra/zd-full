/*
 * 
 */
package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.zimlet.ZimletUtil;
import com.zimbra.soap.ZimbraSoapContext;

public class UndeployZimlet extends AdminDocumentHandler {

	private static class UndeployThread implements Runnable {
		String name;
		ZAuthToken auth;
		public UndeployThread(String na, ZAuthToken au) {
			name = na;
			auth = au;
		}
		public void run() {
			try {
				ZimletUtil.uninstallZimlet(name, auth);
			} catch (Exception e) {
				ZimbraLog.zimlet.info("undeploy", e);
			}
		}
	}
	
	@Override
	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
	    
	    ZimbraSoapContext zsc = getZimbraSoapContext(context);
		
		for (Server server : Provisioning.getInstance().getAllServers())
            checkRight(zsc, context, server, Admin.R_deployZimlet);
		
	    String name = request.getAttribute(AdminConstants.A_NAME);
		String action = request.getAttribute(AdminConstants.A_ACTION, null);
		ZAuthToken auth = null;
		
		if (action == null)
			auth = zsc.getRawAuthToken();
	    Element response = zsc.createElement(AdminConstants.UNDEPLOY_ZIMLET_RESPONSE);
	    new Thread(new UndeployThread(name, auth)).start();
		return response;
	}
	
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_deployZimlet);
        notes.add("Need the " + Admin.R_deployZimlet.getName() + " right on all servers.");
    }

}
