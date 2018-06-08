/*
 * 
 */

/*
 * Created on Jan 7, 2005
 */
package com.zimbra.cs.service.mail;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.filter.RuleManager;
import com.zimbra.soap.ZimbraSoapContext;

public class SaveRules extends MailDocumentHandler {

    public Element handle(Element document, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account account = getRequestedAccount(zsc);

        if (!canModifyOptions(zsc, account))
            throw ServiceException.PERM_DENIED("can not modify options");

        Element rulesElem = document.getElement(MailConstants.E_RULES);
        RuleManager.setIncomingXMLRules(account, rulesElem);
        
        Element response = zsc.createElement(MailConstants.SAVE_RULES_RESPONSE);
        return response;
    }
}
