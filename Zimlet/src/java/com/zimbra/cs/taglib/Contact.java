/*
 * 
 */
package com.zimbra.cs.taglib;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OperationContext;

public class Contact extends ZimbraTag {
    private static final long serialVersionUID = 4310265594871660074L;

    private String mContactId;
    private String mField;

    public void setId(String val) {
        mContactId = val;
    }

    public String getId() {
        return mContactId;
    }

    public void setField(String val) {
        mField = val;
    }

    public String getField() {
        return mField;
    }

    public String getContentStart(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mContactId == null) {
            throw ZimbraTagException.MISSING_ATTR("id");
        }
        if (mField == null) {
            throw ZimbraTagException.MISSING_ATTR("field");
        }
        int cid = Integer.parseInt(mContactId);
        String id = acct.getId();
        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(id);
        com.zimbra.cs.mailbox.Contact con = mbox.getContactById(octxt, cid);
        Map fields = con.getFields();
        String val = (String)fields.get(mField);
        if (val == null) {
        	return "";
        }
        return val;
    }
}
