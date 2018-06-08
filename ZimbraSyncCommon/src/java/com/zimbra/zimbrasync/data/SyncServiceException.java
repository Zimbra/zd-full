/*
 * 
 */
package com.zimbra.zimbrasync.data;

import com.zimbra.common.service.ServiceException;

@SuppressWarnings("serial")
public class SyncServiceException extends ServiceException {
    
    public static final String CANNOT_PERMIT = "sync.CANNOT_PERMIT";
    public static final String CANNOT_CONTAIN = "sync.CANNOT_CONTAIN";
    public static final String OUT_OF_RANGE = "sync.OUT_OF_RANGE";
    public static final String UNEXPECTED_DATA = "sync.UNEXPECTED_DATA";

    private SyncServiceException(String message, String code, boolean isReceiversFault, Argument... args) {
        super(message, code, isReceiversFault, args);
    }

    SyncServiceException(String message, String code, boolean isReceiversFault, Throwable cause, Argument... args) {
        super(message, code, isReceiversFault, cause, args);
    }

    public static SyncServiceException CANNOT_PERMIT() {
        return new SyncServiceException("cannot permit the item to be synced", CANNOT_PERMIT, SENDERS_FAULT);
    }
    
    public static SyncServiceException CANNOT_CONTAIN() {
        return new SyncServiceException("cannot place item in folder", CANNOT_CONTAIN, SENDERS_FAULT);
    }
    
    public static SyncServiceException OUT_OF_RANGE() {
        return new SyncServiceException("item out of range", OUT_OF_RANGE, SENDERS_FAULT);
    }

    public static SyncServiceException UNEXPECTED_DATA(String msg) {
        return new SyncServiceException(msg, UNEXPECTED_DATA, SENDERS_FAULT);
    }
}
