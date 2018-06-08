package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineVerifyPassword extends DocumentHandler {
    @Override
    public Element handle(Element request, Map<String, Object> context)
                    throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        String accountId = request.getAttribute(AccountConstants.E_ID);
        String passwordToVerify = request.getAttribute(AccountConstants.E_PASSWORD);
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();

        Account acct = prov.getAccount(accountId);
        if (acct == null) {
            throw ServiceException.INVALID_REQUEST("no account found with ID "+accountId, null);
        }
        String existingPassword = acct.getAttr(OfflineConstants.A_offlineRemotePassword);

        String status = "fail";

        if (prov.isZcsAccount(acct) && passwordToVerify.equals(existingPassword)) {
            status = "success";
        }
        Element response = getResponseElement(zsc);
        response.addAttribute(AccountConstants.A_STATUS, status);
        return response;
    }
}
