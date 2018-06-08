/*
 * 
 */

/*
 * Created on May 26, 2004
 */
package com.zimbra.cs.service.account;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Identity;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * @author schemers
 */
public class GetIdentities extends AccountDocumentHandler  {

	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
		ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account account = getRequestedAccount(zsc);

        if (!canAccessAccount(zsc, account))
            throw ServiceException.PERM_DENIED("can not access account");

        Element response = zsc.createElement(AccountConstants.GET_IDENTITIES_RESPONSE);
        List<Identity> identities = Provisioning.getInstance().getAllIdentities(account);
        for (Identity ident : identities)
            ToXML.encodeIdentity(response, ident);
        return response;
    }
}

