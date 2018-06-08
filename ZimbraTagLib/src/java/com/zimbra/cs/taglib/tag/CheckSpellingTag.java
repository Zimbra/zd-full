/*
 * 
 */
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.List;

public class CheckSpellingTag extends ZimbraSimpleTag {

    private String mText;

    public void setText(String text) { this.mText = text; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();
			String trimmed = mText.trim().replaceAll("\\u00A0"," ").replaceAll("\\s\\s+"," ");
			ZMailbox.CheckSpellingResult result = mbox.checkSpelling(trimmed);
			JspWriter out = jctxt.getOut();
			out.print("{\"available\":");
			out.print(result.getIsAvailable() ? "true" : "false");
			out.println(",\"data\":[");
			boolean firstMisspelling = true;
			for (ZMailbox.Misspelling misspelling : result.getMisspellings()) {
				if (!firstMisspelling) {
					out.print(',');
				}
				firstMisspelling = false;
				out.print("{\"word\":\"");
				out.print(misspelling.getWord());
				out.print("\",\"suggestions\":[");
				String[] suggestions = misspelling.getSuggestions();
				for (int i = 0, count = suggestions.length; i < count && i < 5; i++) {
					if (i > 0) {
						out.print(',');
					}
					out.print('"');
					out.print(suggestions[i]);
					out.print('"');
				}
				out.println("]}");
			}
			out.println("]}");
		} catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
