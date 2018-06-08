/*
 * 
 */

/*
 * Created on Jan 11, 2005
 *
 */
package com.zimbra.cs.filter;

import org.apache.jsieve.exception.SieveException;

@SuppressWarnings("serial")
public class ZimbraSieveException extends SieveException {
    private Throwable mCause;
    
    public ZimbraSieveException(Throwable t) {
        mCause = t;
    }
    
    public Throwable getCause() {
        return mCause;
    }
}
