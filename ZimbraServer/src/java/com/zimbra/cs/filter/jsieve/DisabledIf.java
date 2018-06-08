/*
 * 
 */

/*
 * Created on Apr 11, 2005
 *
 */
package com.zimbra.cs.filter.jsieve;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.Block;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.commands.AbstractConditionalCommand;
import org.apache.jsieve.mail.MailAdapter;

public class DisabledIf extends AbstractConditionalCommand {

    @Override
    protected Object executeBasic(MailAdapter mail, Arguments arguments,
                                  Block block, SieveContext context) {
        return null;
    }
    
    @Override
    protected void validateArguments(Arguments arguments, SieveContext context) {
        // No validation
    }
}
