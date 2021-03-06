/*
 * 
 */
package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.mailbox.MigrateToDocuments;
import com.zimbra.soap.ZimbraSoapContext;

public class MigrateAccount extends AdminDocumentHandler {

    private static final String[] TARGET_ACCOUNT_PATH = new String[] { AdminConstants.E_MIGRATE, AdminConstants.A_ID };
    protected String[] getProxiedAccountPath()  { return TARGET_ACCOUNT_PATH; }

    @Override
    public boolean domainAuthSufficient(Map<String, Object> context) {
        return false;
    }
    
    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Element migrate = request.getElement(AdminConstants.E_MIGRATE);
        String action = migrate.getAttribute(AdminConstants.A_ACTION);
        String id = migrate.getAttribute(AdminConstants.A_ID);
        
        Provisioning prov = Provisioning.getInstance();
        Account account = prov.getAccountById(id);
        if (account == null)
            throw AccountServiceException.NO_SUCH_ACCOUNT(id);
        
        // perhaps create new right for migrateAccount action
        checkAdminLoginAsRight(zsc, prov, account);

        if (action.equals("wiki")) {
            MigrateToDocuments toDoc = new MigrateToDocuments();
            toDoc.handleAccount(account);
        }
        return zsc.createElement(AdminConstants.MIGRATE_ACCOUNT_RESPONSE);
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_adminLoginAs);
    }

}
