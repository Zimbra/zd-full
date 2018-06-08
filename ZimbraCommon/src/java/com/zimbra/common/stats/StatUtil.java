/*
 * 
 */
package com.zimbra.common.stats;

import java.util.Date;


public class StatUtil {

    /**
     * Returns the current time formatted as <tt>MM/dd/yyyy hh:mm:ss</tt>.
     */
    public static String getTimestampString() {
        return String.format("%1$tm/%1$td/%1$tY %1$tH:%1$tM:%1$tS", new Date());
    }
}
