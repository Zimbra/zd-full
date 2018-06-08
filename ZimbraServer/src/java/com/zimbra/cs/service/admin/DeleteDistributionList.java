/*
 * 
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

public class DeleteDistributionList extends AdminDocumentHandler {

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

        DistributionList distributionList = prov.get(DistributionListBy.id, id);
        if (distributionList == null)
            throw AccountServiceException.NO_SUCH_DISTRIBUTION_LIST(id);

        checkDistributionListRight(zsc, distributionList, Admin.R_deleteDistributionList);        

        prov.deleteDistributionList(distributionList.getId());

        ZimbraLog.security.info(ZimbraLog.encodeAttrs(
                                                      new String[] {"cmd", "DeleteDistributionList","name", distributionList.getName(), "id", distributionList.getId()}));

        Element response = zsc.createElement(AdminConstants.DELETE_DISTRIBUTION_LIST_RESPONSE);
        return response;
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_deleteDistributionList);
    }
}
