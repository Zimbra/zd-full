/*
 * 
 */

package com.zimbra.cs.service.offline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.RemoteServiceException;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.DirectorySync;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.util.yauth.AuthenticationException;
import com.zimbra.cs.util.yauth.ErrorCode;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineChangePassword extends DocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context)
                    throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        String accountId = request.getAttribute(AccountConstants.E_ID);
        Element password = request.getElement(AccountConstants.E_PASSWORD);
        String newPass = password.getText();
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        
        Account acct = prov.getAccount(accountId);
        if (acct == null) {
            throw ServiceException.INVALID_REQUEST("no account found with ID "+accountId, null);
        }
        
        Map<String, Object> attrs = new HashMap<String, Object>();
        String status = "fail";
        try {
            if (prov.isZcsAccount(acct)) {
                attrs.put(OfflineConstants.A_offlineAccountSetup, Provisioning.TRUE); //flag so modify validates
                attrs.put(OfflineConstants.A_offlineRemotePassword, newPass);
                try {
                    prov.modifyAttrs(acct, attrs);
                } catch (ServiceException exception) {
                    /*
                     * If it is an exception related to two factor auth then we
                     * want to send success message for changepassword request.
                     */
                    if (!(OfflineSyncManager.isTwoFactorAuthSetupError(exception)||
                          OfflineSyncManager.isTwoFactorAuthRequiredError(exception) ||
                          OfflineSyncManager.isTwoFactorAuthExpiredError(exception))) {
                        throw exception;
                    }
                }
                status = "success";
            } else {
                List<DataSource> dataSources = acct.getAllDataSources();
                if (dataSources != null) {
                    for (DataSource ds : dataSources) {
                        boolean needModify = false;
                        if (ds.getAttr(Provisioning.A_zimbraDataSourcePassword) != null) {
                            attrs.put(Provisioning.A_zimbraDataSourcePassword, newPass);
                            needModify = true;
                        }
                        if (ds.getAttr(OfflineConstants.A_zimbraDataSourceSmtpAuthPassword) != null) {
                            attrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthPassword, newPass);
                            needModify = true;
                        }
                        if (needModify) {
                            String domain = ds.getAttr(Provisioning.A_zimbraDataSourceDomain);
                            if ("yahoo.com".equals(domain)) {
                                OfflineYAuth.removeToken(ds);
                            }
                            prov.modifyDataSource(acct, ds.getId(), attrs);
                            prov.testDataSource((OfflineDataSource) ds);
                            status = "success";
                        }
                    }
                }
            }
        } catch (ServiceException se) {
            boolean badPass = false;
            if (AccountServiceException.AUTH_FAILED.equals(se.getCode()) || RemoteServiceException.AUTH_FAILURE.equals(se.getCode())) {
                badPass = true;
            } else if (se instanceof AuthenticationException) {
                AuthenticationException ae = (AuthenticationException) se;
                if (ae.getErrorCode() == ErrorCode.INVALID_PASSWORD) {
                    badPass = true;
                }
            }
            if (!badPass) {
                throw se;
            } else {
                if (acct instanceof OfflineAccount && ((OfflineAccount) acct).isDebugTraceEnabled()) {
                    OfflineLog.offline.debug("unable to save new password due to exception",se);
                }
                //if acct debug is on dump to log before failing
            }
        }
        Element response = getResponseElement(zsc);
        response.addAttribute(AccountConstants.A_STATUS, status);
        return response;
    }
}
