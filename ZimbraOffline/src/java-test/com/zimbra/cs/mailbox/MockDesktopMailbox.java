/*
 * 
 */

package com.zimbra.cs.mailbox;

import java.util.List;

import com.zimbra.common.service.ServiceException;

public class MockDesktopMailbox extends DesktopMailbox {
    private static TagUtils tagUtils = new TagUtils();
    
    public MockDesktopMailbox() throws ServiceException {
        super(new MailboxData());
    }

    public MockDesktopMailbox(MailboxData data) throws ServiceException {
        super(data);
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
    public synchronized Tag createTag(OperationContext octxt, String name,
            byte color) throws ServiceException {
        return tagUtils.createTag(this, name, color);
    }
}
