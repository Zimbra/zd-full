/*
 * 
 */
package com.zimbra.cs.service.formatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.Part;
import javax.servlet.http.HttpServletResponse;

import com.zimbra.cs.index.MailboxIndex;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.service.UserServlet;
import com.zimbra.cs.service.UserServletContext;
import com.zimbra.cs.service.UserServletException;
import com.zimbra.cs.service.formatter.FormatterFactory.FormatType;
import com.zimbra.cs.service.mail.ImportContacts;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.HttpUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.mime.MimeConstants;

public class CsvFormatter extends Formatter {

    @Override
    public FormatType getType() {
        return FormatType.CSV;
    }

    @Override
    public String[] getDefaultMimeTypes() {
        return new String[] { "text/csv", "text/comma-separated-values", MimeConstants.CT_TEXT_PLAIN };
    }

    @Override
    public String getDefaultSearchTypes() {
        return MailboxIndex.SEARCH_FOR_CONTACTS;
    }

    @Override
    public void formatCallback(UserServletContext context) throws IOException, ServiceException {
        Iterator<? extends MailItem> iterator = null;
        StringBuilder sb = new StringBuilder();
        try {
            iterator = getMailItems(context, -1, -1, Integer.MAX_VALUE);
            String format = context.req.getParameter(UserServlet.QP_CSVFORMAT);
            String locale = context.req.getParameter(UserServlet.QP_CSVLOCALE);
            String separator = context.req.getParameter(UserServlet.QP_CSVSEPARATOR);
            Character sepChar = null;
            if ((separator != null) && (separator.length() > 0))
                    sepChar = separator.charAt(0);
            if (locale == null) {
                locale = context.getLocale().toString();
            }
            ContactCSV contactCSV = new ContactCSV();
            contactCSV.toCSV(format, locale, sepChar, iterator, sb);
        } catch (ContactCSV.ParseException e) {
            throw MailServiceException.UNABLE_TO_IMPORT_CONTACTS("could not generate CSV", e);
        } finally {
            if (iterator instanceof QueryResultIterator)
                ((QueryResultIterator) iterator).finished();
        }

        // todo: get from folder name
        String filename = context.itemPath;
        if (filename == null || filename.length() == 0)
            filename = "contacts";
        String cd = Part.ATTACHMENT + "; filename=" + HttpUtil.encodeFilename(context.req, filename + ".csv");
        context.resp.addHeader("Content-Disposition", cd);
        context.resp.setCharacterEncoding(context.getCharset().name());
        context.resp.setContentType("text/csv");
        context.resp.getWriter().print(sb.toString());
    }

    @Override
    public boolean supportsSave() {
        return true;
    }

    @Override
    public void saveCallback(UserServletContext context, String contentType, Folder folder, String filename)
    throws UserServletException, ServiceException, IOException {
        InputStreamReader isr = new InputStreamReader(
                context.getRequestInputStream(), context.getCharset());
        BufferedReader reader = new BufferedReader(isr);

        try {
            String format = context.params.get(UserServlet.QP_CSVFORMAT);
            String locale = context.req.getParameter(UserServlet.QP_CSVLOCALE);
            if (locale == null) {
                locale = context.getLocale().toString();
            }
            List<Map<String, String>> contacts = ContactCSV.getContacts(reader, format, locale);
            ItemId iidFolder = new ItemId(folder);

            ImportContacts.ImportCsvContacts(context.opContext, context.targetMailbox, iidFolder, contacts);
        } catch (ContactCSV.ParseException e) {
            ZimbraLog.misc.debug("ContactCSV - ParseException thrown", e);
            throw new UserServletException(HttpServletResponse.SC_BAD_REQUEST,
                    "Could not parse csv file - Reason : " + e.getMessage());
        } finally {
            reader.close();
        }
    }

}
