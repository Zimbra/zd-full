/*
 * 
 */


package com.zimbra.kabuki.tools.img;

import java.io.IOException;

public class ImageMergeException extends IOException {

    public ImageMergeException(String msg, 
                               Throwable cause) 
    {
        super(msg);
        setStackTrace(cause.getStackTrace());
    }

    public ImageMergeException(String msg) {
        super(msg);
    }
}