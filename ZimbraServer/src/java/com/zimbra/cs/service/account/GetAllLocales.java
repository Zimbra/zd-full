/*
 * 
 */

package com.zimbra.cs.service.account;

import java.util.Locale;
import java.util.Map;

import com.zimbra.common.util.L10nUtil;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.common.soap.AccountConstants;

public class GetAllLocales extends AccountDocumentHandler {

    public Element handle(Element request, Map<String, Object> context) {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);

        Locale locales[] = L10nUtil.getAllLocalesSorted();
        Element response = zsc.createElement(AccountConstants.GET_ALL_LOCALES_RESPONSE);
        for (Locale locale : locales)
            ToXML.encodeLocale(response, locale, Locale.US);
        return response;
    }
}
