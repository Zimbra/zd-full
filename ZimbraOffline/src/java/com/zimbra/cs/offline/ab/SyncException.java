/*
 * 
 */
package com.zimbra.cs.offline.ab;

import com.zimbra.common.service.ServiceException;

public class SyncException extends ServiceException {
    public SyncException(String msg, Throwable cause) {
        super(msg, FAILURE, RECEIVERS_FAULT, cause);
    }

    public SyncException(String msg) {
        this(msg, null);
    }
}
