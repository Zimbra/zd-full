/*
 * 
 */

package com.zimbra.cs.mailbox;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.store.MockStoreManager;

/**
 * Mock implementation of {@link Mailbox} for testing.
 *
 * @author ysasaki
 */
public class MockMailbox extends Mailbox {

    private Account account;
    private Map<String, Metadata> metadata = new HashMap<String, Metadata>();
    private static TagUtils tagUtils = new TagUtils();

    MockMailbox(Account account) {
        super(new MailboxData());
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
    public Account getAccount() {
        return account;
    }

    @Override
    public String getAccountId() {
        return account.getId();
    }

    @Override
    public Folder getFolderById(OperationContext octxt, int id)
        throws ServiceException {

        return getFolderById(id);
    }

    @Override
    public Folder getFolderById(int id) throws ServiceException {
        MailItem.UnderlyingData data = new MailItem.UnderlyingData();
        data.type = MailItem.TYPE_FOLDER;
        data.id = id;
        data.name = String.valueOf(id);
        return new Folder(this, data);
    }

    @Override
    public synchronized Tag createTag(OperationContext octxt, String name,
            byte color) throws ServiceException {
        return tagUtils.createTag(this, name, color);
    }

@Override
    public Tag getTagByName(String name) throws ServiceException {
        return tagUtils.getTagByName(name);
    }

    @Override
    Tag getTagById(int id) throws ServiceException {
        return tagUtils.getTagById(id);
    }

    @Override
    public synchronized List<Tag> getTagList(OperationContext octxt)
            throws ServiceException {
        return tagUtils.getTags();
    }

    @Override
    public Document getDocumentById(OperationContext octxt, int id) throws ServiceException {
        if (itemMap != null) {
            return (Document)itemMap.get(Integer.valueOf(id));
        }
        return null;
    }
    
    private HashMap<Integer,MailItem> itemMap;
    
    public void addMailItem(MailItem item) {
        if (itemMap == null) {
            itemMap = new HashMap<Integer,MailItem>();
        }
        itemMap.put(Integer.valueOf(item.getId()), item);
    }
    
    public void addDocument(int id, String name, String contentType, String content) throws ServiceException {
        MailItem.UnderlyingData data = new MailItem.UnderlyingData();
        data.id = id;
        data.type = MailItem.TYPE_DOCUMENT;
        data.name = name;
        data.subject = name;
        data.flags = Flag.BITMASK_UNCACHED;
        data.setBlobDigest("foo");
        data.metadata = new Metadata().put(Metadata.FN_MIME_TYPE, contentType).toString();
        Document doc = new Document(this, data);
        MockStoreManager.setBlob(doc, content);
        addMailItem(doc);
    }
    
    @Override
    public boolean dumpsterEnabled() {
        return false;
    }
}
