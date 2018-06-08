/*
 * 
 */

package com.zimbra.cs.imap;

class ImapParseException extends ImapException {
    private static final long serialVersionUID = 4675342317380797673L;

    String mTag, mCode;
    boolean mNO;

    ImapParseException() {
    }

    ImapParseException(String tag, String message) {
        super("parse error: " + message);
        mTag = tag;
    }

    ImapParseException(String tag, String message, boolean no) {
        super((no ? "" : "parse error: ") + message);
        mTag = tag;
        mNO = no;
    }

    ImapParseException(String tag, String code, String message, boolean parseError) {
        super((parseError ? "parse error: " : "") + message);
        mTag = tag;
        mCode = code;
        mNO = code != null;
    }
}
