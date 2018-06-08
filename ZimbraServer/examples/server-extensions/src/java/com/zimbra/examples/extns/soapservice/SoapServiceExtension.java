/*
 * 
 */
package com.zimbra.examples.extns.soapservice;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.soap.SoapServlet;

/**
 * Extension that augments the Zimbra SOAP service.
 *
 * @author vmahajan
 */
public class SoapServiceExtension implements ZimbraExtension {

    /**
     * Defines a name for the extension. It must be an identifier.
     *
     * @return extension name
     */
    public String getName() {
        return "soapServiceExtn";
    }

    /**
     * Initializes the extension. Called when the extension is loaded.
     *
     * @throws com.zimbra.common.service.ServiceException
     */
    public void init() throws ServiceException {
        SoapServlet.addService("SoapServlet", new SoapExtnService());
    }

    /**
     * Terminates the extension. Called when the server is shut down.
     */
    public void destroy() {
    }
}