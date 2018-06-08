/*
 * 
 */
package com.zimbra.cs.offline.util.yab;

import com.zimbra.cs.util.yauth.Auth;
import com.zimbra.cs.util.yauth.Authenticator;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.common.util.Log;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * Yahoo Address book access.
 */
public class Yab {
    public static final String DTD = "http://l.yimg.com/us.yimg.com/lib/pim/r/abook/xml/2/pheasant.dtd";

    public static final Log LOG = OfflineLog.yab;
    
    public static final String BASE_URI = "https://address.yahooapis.com/v1";

    public static final String XML = "xml";
    public static final String JSON = "json";
    
    public static Session createSession(Authenticator auth) {
        return new Session(auth);
    }

    public static boolean isDebug() {
        return LOG.isDebugEnabled();
    }

    public static void debug(String fmt, Object... args) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format(fmt, args));
        }
    }
}
