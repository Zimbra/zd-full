/*
 * 
 */
package com.zimbra.cs.service.formatter;

import java.io.IOException;

import javax.servlet.ServletException;

import com.zimbra.cs.index.MailboxIndex;
import com.zimbra.cs.service.UserServletContext;
import com.zimbra.cs.service.UserServletException;
import com.zimbra.cs.service.formatter.FormatterFactory.FormatType;
import com.zimbra.common.service.ServiceException;

public class FreeBusyFormatter extends Formatter {

    private static final String ATTR_FREEBUSY = "zimbra_freebusy";

    public FormatType getType() {
        return FormatType.FREE_BUSY;
    }

    public boolean requiresAuth() {
        return true;
    }
    
    public String getDefaultSearchTypes() {
        return MailboxIndex.SEARCH_FOR_APPOINTMENTS;
    }

    public void formatCallback(UserServletContext context)
    throws IOException, ServiceException, UserServletException, ServletException {
        context.req.setAttribute(ATTR_FREEBUSY, "true");
        HtmlFormatter.dispatchJspRest(context.getServlet(), context);
    }

}
