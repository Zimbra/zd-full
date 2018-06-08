/*
 * 
 */
package com.zimbra.cs.service.account;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.gal.GalSearchControl;
import com.zimbra.cs.gal.GalSearchParams;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * @since May 26, 2004
 * @author schemers
 */
public class AutoCompleteGal extends GalDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account account = getRequestedAccount(getZimbraSoapContext(context));

        if (!canAccessAccount(zsc, account))
            throw ServiceException.PERM_DENIED("can not access account");
        
        String name = request.getAttribute(AccountConstants.E_NAME);
        String typeStr = request.getAttribute(AccountConstants.A_TYPE, "account");
        Provisioning.GalSearchType type = Provisioning.GalSearchType.fromString(typeStr);

        boolean needCanExpand = request.getAttributeBool(AccountConstants.A_NEED_EXP, false);

        String galAcctId = request.getAttribute(AccountConstants.A_GAL_ACCOUNT_ID, null);
        
        GalSearchParams params = new GalSearchParams(account, zsc);
        params.setType(type);
        params.setRequest(request);
        params.setQuery(name);
        params.setLimit(account.getContactAutoCompleteMaxResults());
        params.setNeedCanExpand(needCanExpand);
        params.setResponseName(AccountConstants.AUTO_COMPLETE_GAL_RESPONSE);
        if (galAcctId != null)
            params.setGalSyncAccount(Provisioning.getInstance().getAccountById(galAcctId));
        GalSearchControl gal = new GalSearchControl(params);
        gal.autocomplete();
        return params.getResultCallback().getResponse();
    }

    @Override
    public boolean needsAuth(Map<String, Object> context) {
        return true;
    }
}
