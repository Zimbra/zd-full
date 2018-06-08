/*
 * 
 */
package com.zimbra.cs.service.offline;

import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineResetTwoFactorCode extends DocumentHandler {
    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        String accountId = request.getAttribute(AccountConstants.E_ID);
        Element twoFactorCode = request.getElement(AccountConstants.E_TWO_FACTOR_CODE);
        String newTotp = twoFactorCode.getText();
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();

        Account acct = prov.getAccount(accountId);
        if (acct == null) {
            throw ServiceException.INVALID_REQUEST("no account found with ID " + accountId, null);
        }

        Map<String, Object> attrs = new HashMap<String, Object>();
        String status = "fail";
        try {
            if (prov.isZcsAccount(acct)) {
                attrs.put(OfflineConstants.A_offlineAccountSetup, Provisioning.TRUE);
                attrs.put(OfflineConstants.A_twofactorAuthCode, newTotp);
                prov.modifyAttrs(acct, attrs);
                status = "success";
            }
        } catch (ServiceException se) {
            if (AccountServiceException.TWO_FACTOR_AUTH_FAILED.equals(se.getCode())) {
                if (acct instanceof OfflineAccount && ((OfflineAccount) acct).isDebugTraceEnabled()) {
                    OfflineLog.offline.debug("unable to save new two factor auth code due to exception", se);
                }

            } else {
                throw se;
            }
        }
        Element response = getResponseElement(zsc);
        response.addAttribute(AccountConstants.A_STATUS, status);
        return response;
    }
}
