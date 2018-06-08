/*
 * 
 */

/*
 * Created on Nov 10, 2004
 */
package com.zimbra.cs.service.mail;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.filter.RuleManager;
import com.zimbra.soap.ZimbraSoapContext;

public class GetRules extends MailDocumentHandler {

    public Element handle(Element document, Map<String, Object> context) throws ServiceException {
		ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account account = getRequestedAccount(zsc);

        if (!canAccessAccount(zsc, account))
            throw ServiceException.PERM_DENIED("can not access account");

        Element response = zsc.createElement(MailConstants.GET_RULES_RESPONSE);
        Element rules = RuleManager.getIncomingRulesAsXML(response.getFactory(), account);
        response.addUniqueElement(rules);
        return response;
    }

}
