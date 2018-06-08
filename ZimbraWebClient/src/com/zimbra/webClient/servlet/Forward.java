/*
 * 
 */

package com.zimbra.webClient.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zimbra.common.util.ZimbraLog;

@SuppressWarnings("serial")
public class Forward extends ZCServlet
{
    public static final String DEFAULT_FORWARD_URL = 
	"/public/login.jsp";
    private static final String PARAM_FORWARD_URL = "fu";
    
    public void doGet (HttpServletRequest req,
		       HttpServletResponse resp) {

	try {
	    String url = getReqParameter(req, PARAM_FORWARD_URL,
                                         DEFAULT_FORWARD_URL);
	    String qs = req.getQueryString();
	    if (qs != null && !qs.equals("")){
		url = url + "?" + qs;
	    }
	    ServletContext sc = getServletConfig().getServletContext();
	    sc.getRequestDispatcher(url).forward(req, resp);
	} catch (Exception ex) {
		ZimbraLog.webclient.warn("exception forwarding", ex);
		if (!resp.isCommitted())
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
    }    
}
