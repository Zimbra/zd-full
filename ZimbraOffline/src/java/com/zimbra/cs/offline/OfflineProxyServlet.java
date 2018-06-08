/*
 * 
 */

package com.zimbra.cs.offline;

import javax.servlet.http.HttpServletRequest;
import com.zimbra.cs.zimlet.ProxyServlet;

@SuppressWarnings("serial")
public class OfflineProxyServlet extends ProxyServlet {

    @Override
    protected boolean isAdminRequest(HttpServletRequest req) {
        return false;
    }
    
}
