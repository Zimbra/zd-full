/*
 * 
 */
package com.zimbra.cs.service.mail;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.filter.RuleManager;
import com.zimbra.soap.ZimbraSoapContext;
import org.dom4j.QName;

public class GetFilterRules extends MailDocumentHandler {

    public Element handle(Element document, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account account = getRequestedAccount(zsc);

        if (!canAccessAccount(zsc, account))
            throw ServiceException.PERM_DENIED("cannot access account");

        Element response = zsc.createElement(getResponseElementName());
        Element rules = getRulesAsXML(account, response.getFactory());
        response.addElement(rules);
        return response;
    }

    protected QName getResponseElementName() {
        return MailConstants.GET_FILTER_RULES_RESPONSE;
    }

    protected Element getRulesAsXML(Account account, Element.ElementFactory elementFactory) throws ServiceException {
        return RuleManager.getIncomingRulesAsXML(elementFactory, account, true);
    }
}
