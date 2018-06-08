/*
 * 
 */
package com.zimbra.cs.im.interop;

import org.xmpp.component.ComponentException;

public class AlreadyConnectedComponentException extends ComponentException {
    private static final long serialVersionUID = 1032727758287169122L;
    public AlreadyConnectedComponentException(String serviceName, String username) {
        super("Already connected to service "+serviceName+" as user "+username);
    }
}