/*
 * 
 */
package com.zimbra.zimbrasync.data;

public enum BodyType {
    
    PlainText, HTML, RTF, MIME;

    public static final BodyType getBodyType(int i) {
        switch (i) {
        case 1:
            return PlainText;
        case 2:
            return HTML;
        case 3:
            return RTF;
        case 4:
            return MIME;
        default:
            return null;
        }
    }

    public static final int getBodyType(BodyType type) {
        switch (type) {
        case PlainText:
            return 1;
        case HTML:
            return 2;
        case RTF:
            return 3;
        case MIME:
            return 4;
        default:
            return -1;
        }
    }

}
