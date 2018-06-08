/*
 * 
 */

/*
 * Created on Nov 11, 2004
 *
 */
package com.zimbra.cs.filter.jsieve;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.tests.AbstractTest;

import com.zimbra.cs.filter.ZimbraMailAdapter;
import com.zimbra.cs.mime.ParsedMessage;

public class AttachmentTest extends AbstractTest {

    @Override
    protected boolean executeBasic(MailAdapter mail, Arguments arguments, SieveContext context) {
        if (!(mail instanceof ZimbraMailAdapter))
            return false;
        ParsedMessage pm = ((ZimbraMailAdapter) mail).getParsedMessage();
        if (pm == null) {
            return false;
        } else {
            return pm.hasAttachments();
        }
    }
}
