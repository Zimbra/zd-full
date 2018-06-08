/*
 * 
 */
package com.zimbra.cs.mailbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.MailItem.UnderlyingData;

public class MockZcsMailbox extends ZcsMailbox {
    
    private Account account;
    private Map<String, Metadata> metadata = new HashMap<String, Metadata>();
    
    private Map<String, Tag> tagsByName = new HashMap<String, Tag>();

    MockZcsMailbox(Account account, MailboxData data) throws ServiceException {
        super(data);
        this.account = account;
    }

    @Override
    public Metadata getConfig(OperationContext octxt, String section) {
        return metadata.get(section);
    }

    @Override
    public void setConfig(OperationContext octxt, String section,
            Metadata config) {
        metadata.put(section, config);
    }

    @Override
    public Account getAccount() throws ServiceException {
        if (account == null) {
            return super.getAccount();
        } else {
            return account;
        }
    }

    @Override
    public String getAccountId() {
        if (account == null) {
            return super.getAccountId();
        } else {
            return account.getId();
        }
    }

    @Override
    public synchronized List<Tag> getTagList(OperationContext octxt)
                    throws ServiceException {
        return new ArrayList<Tag>();

    }
    
    @Override
    public synchronized Tag getTagByName(String name) throws ServiceException {
        return tagsByName.get(name);
    }
    
    public void addStubTag(String name, Integer id) throws ServiceException {
        UnderlyingData data = new UnderlyingData();
        data.id          = id;
        data.type        = MailItem.TYPE_TAG;
        data.name        = name;
        data.subject     = name;
        Tag tag = new Tag(this, data);
        tagsByName.put(name, tag);
    }
}
