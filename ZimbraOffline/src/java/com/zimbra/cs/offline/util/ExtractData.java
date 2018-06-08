/*
 * 
 */
package com.zimbra.cs.offline.util;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;

public class ExtractData {

    public static String getAttributefromChildren(Element elt, String key, String defaultValue) throws ServiceException {
        for (Element eField : elt.listElements()) {
            String result;
            if ((result = (String) eField.getAttribute(key, defaultValue)) != null)
                return result;
            for (Element subChild : eField.listElements()) {
                if (!subChild.getName().equals(MailConstants.E_METADATA)) {
                    if (subChild.getAttribute(Element.XMLElement.A_ATTR_NAME).equals(key))
                        return subChild.getText();
                }
            }
        }
        return defaultValue;
    }
}
