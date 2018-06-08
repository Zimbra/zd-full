/*
 * 
 */
package com.zimbra.cs.offline.util.yc;

import com.zimbra.common.service.ServiceException;

public class YContactException extends ServiceException {

    private static final long serialVersionUID = -3854087648082207330L;
    
    public YContactException(String message, String code, boolean isReceiversFault, Throwable cause,
            Argument[] arguments) {
        super(message, code == null ? "YContact.FAILURE" : code, isReceiversFault, cause, arguments);
    }
}
