/*
 * 
 */
package com.zimbra.cs.offline.jsp;

import javax.servlet.http.HttpServlet;

import com.zimbra.common.localconfig.LC;

public class ConfigServlet extends HttpServlet {

    private static final long serialVersionUID = 8124246834674440988L;

    private static final String LOCALHOST_URL_PREFIX = "http://127.0.0.1:";

    public static String LOCALHOST_SOAP_URL;
    public static String LOCALHOST_ADMIN_URL;

    @Override
    public void init() {
        String port = LC.zimbra_admin_service_port.value();

        //setting static variables
        LOCALHOST_SOAP_URL = LOCALHOST_URL_PREFIX + port + "/service/soap/";
        LOCALHOST_ADMIN_URL = LOCALHOST_URL_PREFIX + port + "/service/admin/soap/";
    }
}
