/*
 * 
 */

package com.zimbra.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SystemUtil {

    public static final boolean ON_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("win");


    public static String getStackTrace() {
        return getStackTrace(new Throwable());
    }
    
    public static String getStackTrace(Throwable t) {
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
    
    /**
     * Returns the innermost exception wrapped by
     * <tt>t</tt>.  The innermost exception is found by iterating
     * the exceptions returned by {@link Throwable#getCause()}.
     * 
     * @return the innermost exception, or <tt>null</tt> if <tt>t</tt>
     * is <tt>null</tt>.
     */
    public static Throwable getInnermostException(Throwable t) {
        if (t == null) {
            return null;
        }
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }
    
    /**
     * Returns the first non-null value in the given list.
     */
    public static <E> E coalesce(E ... values) {
        if (values != null) {
            for (E value : values) {
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }
}
