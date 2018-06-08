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


public class ModifyFilterRules extends MailDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account account = getRequestedAccount(zsc);

        if (!canModifyOptions(zsc, account))
            throw ServiceException.PERM_DENIED("cannot modify options");

        Element rulesElem = request.getElement(MailConstants.E_FILTER_RULES);
        setXMLRules(account, rulesElem);

        Element response = zsc.createElement(getResponseElementName());
        return response;
    }

    protected QName getResponseElementName() {
        return MailConstants.MODIFY_FILTER_RULES_RESPONSE;
    }

    protected void setXMLRules(Account account, Element rulesElem) throws ServiceException {
        RuleManager.setIncomingXMLRules(account, rulesElem, true);
    }

}
