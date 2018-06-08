/*
 * 
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.common.service.ServiceException;

public class ZTagLibException extends ServiceException {

    public static final String TAG_EXCEPTION       = "ztaglib.TAG_EXCEPTION";

    public static final String UPLOAD_SIZE_LIMIT_EXCEEDED = "ztaglib.UPLOAD_SIZE_LIMIT_EXCEEDED";

    public static final String UPLOAD_FAILED = "ztaglib.TAG_UPLOAD_FAILED";

    public static final String EMPTY_CONTACT = "ztaglib.EMPTY_CONTACT";

    public static final String INVALID_FILTER_DATE = "ztaglib.INVALID_FILTER_DATE";

    public static final String INVALID_APPT_DATE = "ztaglib.INVALID_APPT_DATE";

    public static final String FILTER_EXISTS = "ztaglib.FILTER_EXISTS";

    public static final String NO_SUCH_FILTER_EXISTS = "ztaglib.NO_SUCH_FILTER_EXISTS";

    public static final String SERVER_REDIRECT = "ztaglib.SERVER_REDIRECT";

    public static final String INVALID_CRUMB = "ztaglib.INVALID_CRUMB";

    public ZTagLibException(String message, String code) {
        super(message, code, true);
    }

    private ZTagLibException(String message, String code, boolean isReceiversFault) {
        super(message, code, isReceiversFault);
    }

    private ZTagLibException(String message, String code, boolean isReceiversFault, Throwable cause) {
        super(message, code, isReceiversFault, cause);
    }

    public static ZTagLibException INVALID_CRUMB(String msg, Throwable cause) {
        return new ZTagLibException(msg, INVALID_CRUMB, SENDERS_FAULT, cause);
    }

    public static ZTagLibException TAG_EXCEPTION(String msg, Throwable cause) {
        return new ZTagLibException(msg, TAG_EXCEPTION, SENDERS_FAULT, cause);
    }

    public static ZTagLibException UPLOAD_SIZE_LIMIT_EXCEEDED(String msg, Throwable cause) {
        return new ZTagLibException(msg, UPLOAD_SIZE_LIMIT_EXCEEDED, SENDERS_FAULT, cause);
    }

    public static ZTagLibException UPLOAD_FAILED(String msg, Throwable cause) {
        return new ZTagLibException(msg, UPLOAD_FAILED, SENDERS_FAULT, cause);
    }

    public static ZTagLibException EMPTY_CONTACT(String msg, Throwable cause) {
        return new ZTagLibException(msg, EMPTY_CONTACT, SENDERS_FAULT, cause);
    }

    public static ZTagLibException INVALID_FILTER_DATE(String msg, Throwable cause) {
        return new ZTagLibException(msg, INVALID_FILTER_DATE, SENDERS_FAULT, cause);
    }

    public static ZTagLibException INVALID_APPT_DATE(String msg, Throwable cause) {
        return new ZTagLibException(msg, INVALID_APPT_DATE, SENDERS_FAULT, cause);
    }

    public static ZTagLibException FILTER_EXISTS(String msg, Throwable cause) {
        return new ZTagLibException(msg, FILTER_EXISTS, SENDERS_FAULT, cause);
    }

    public static ZTagLibException NO_SUCH_FILTER_EXISTS(String msg, Throwable cause) {
        return new ZTagLibException(msg, NO_SUCH_FILTER_EXISTS, SENDERS_FAULT, cause);
    }

    public static ZTagLibException SERVER_REDIRECT(String msg, Throwable cause) {
        return new ZTagLibException(msg, SERVER_REDIRECT, SENDERS_FAULT, cause);
    }
}
