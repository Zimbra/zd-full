/*
 * 
 */

package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.DistributionList;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.TargetType;
import com.zimbra.soap.ZimbraSoapContext;

public class CreateDistributionList extends AdminDocumentHandler {

    /**
     * must be careful and only allow access to domain if domain admin
     */
    public boolean domainAuthSufficient(Map context) {
        return true;
    }

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
	    
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Provisioning prov = Provisioning.getInstance();
	    
        String name = request.getAttribute(AdminConstants.E_NAME).toLowerCase();
        Map<String, Object> attrs = AdminService.getAttrs(request, true);

        checkDomainRightByEmail(zsc, name, Admin.R_createDistributionList);
        checkSetAttrsOnCreate(zsc, TargetType.dl, name, attrs);
        
        DistributionList dl = prov.createDistributionList(name, attrs);
        
        ZimbraLog.security.info(ZimbraLog.encodeAttrs(
                 new String[] {"cmd", "CreateDistributionList","name", name}, attrs));         

        Element response = zsc.createElement(AdminConstants.CREATE_DISTRIBUTION_LIST_RESPONSE);
        
        GetDistributionList.encodeDistributionList(response, dl);

        return response;
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_createDistributionList);
        notes.add(String.format(AdminRightCheckPoint.Notes.MODIFY_ENTRY, 
                Admin.R_modifyDistributionList.getName(), "distribution list"));
    }
}
