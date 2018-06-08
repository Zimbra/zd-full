/*
 * 
 */

package com.zimbra.cs.service.mail;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.soap.ZimbraSoapContext;

public class GenerateUUID extends MailDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Element response = zsc.createElement(MailConstants.GENERATE_UUID_RESPONSE);
        response.setText(LdapUtil.generateUUID());
        return response;
    }
}
