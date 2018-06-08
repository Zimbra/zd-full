/*
 * 
 */

/*
 * Created on May 26, 2004
 */
package com.zimbra.cs.service.account;

import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.Element.KeyValuePair;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * @author schemers
 */
public class ModifyPrefs extends AccountDocumentHandler {

    public static final String PREF_PREFIX = "zimbraPref";

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account account = getRequestedAccount(zsc);

        if (!canModifyOptions(zsc, account))
            throw ServiceException.PERM_DENIED("can not modify options");

        HashMap<String, Object> prefs = new HashMap<String, Object>();
        for (KeyValuePair kvp : request.listKeyValuePairs(AccountConstants.E_PREF, AccountConstants.A_NAME)) {
            String name = kvp.getKey(), value = kvp.getValue();
            char ch = name.length() > 0 ? name.charAt(0) : 0;
            int offset = ch == '+' || ch == '-' ? 1 : 0;
            if (!name.startsWith(PREF_PREFIX, offset))
                throw ServiceException.INVALID_REQUEST("pref name must start with " + PREF_PREFIX, null);
            StringUtil.addToMultiMap(prefs, name, value);
        }

        if (prefs.containsKey(Provisioning.A_zimbraPrefMailForwardingAddress)) {
            if (!account.getBooleanAttr(Provisioning.A_zimbraFeatureMailForwardingEnabled, false))
                throw ServiceException.PERM_DENIED("forwarding not enabled");
        }

        // call modifyAttrs and pass true to checkImmutable
        Provisioning.getInstance().modifyAttrs(account, prefs, true, zsc.getAuthToken());

        Element response = zsc.createElement(AccountConstants.MODIFY_PREFS_RESPONSE);
        return response;
    }
}
