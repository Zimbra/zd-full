/*
 * 
 */

/*
 * Created on Jun 17, 2004
 */
package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.DistributionList;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.DistributionListBy;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;

public class RenameDistributionList extends AdminDocumentHandler {

    /**
     * must be careful and only allow access to domain if domain admin
     */
    public boolean domainAuthSufficient(Map context) {
        return true;
    }

	public Element handle(Element request, Map<String, Object> context) throws ServiceException {

        ZimbraSoapContext zsc = getZimbraSoapContext(context);
	    Provisioning prov = Provisioning.getInstance();

	    String id = request.getAttribute(AdminConstants.E_ID);
        String newName = request.getAttribute(AdminConstants.E_NEW_NAME);

	    DistributionList dl = prov.get(DistributionListBy.id, id);
        if (dl == null)
            throw AccountServiceException.NO_SUCH_ACCOUNT(id);

        // check if the admin can rename the DL
        checkDistributionListRight(zsc, dl, Admin.R_renameDistributionList);

        // check if the admin can "create DL" in the domain (can be same or diff)
        checkDomainRightByEmail(zsc, newName, Admin.R_createDistributionList);
        
        String oldName = dl.getName();

        prov.renameDistributionList(id, newName);

        ZimbraLog.security.info(ZimbraLog.encodeAttrs(
                new String[] {"cmd", "RenameDistributionList", "name", oldName, "newName", newName})); 
        
        // get again with new name...

        dl = prov.get(DistributionListBy.id, id);
        if (dl == null)
            throw ServiceException.FAILURE("unable to get distribution list after rename: " + id, null);
	    Element response = zsc.createElement(AdminConstants.RENAME_DISTRIBUTION_LIST_RESPONSE);
	    GetDistributionList.encodeDistributionList(response, dl);
	    return response;

    }
	
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_renameDistributionList);
        relatedRights.add(Admin.R_createDistributionList);
    }
}
