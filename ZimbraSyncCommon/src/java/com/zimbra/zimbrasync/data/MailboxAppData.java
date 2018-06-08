/*
 * 
 */
package com.zimbra.zimbrasync.data;

import java.io.IOException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.Tag;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public abstract class MailboxAppData {
    abstract AppData getAppData();
    
    public void parseCategories(BinaryParser parser, String nameSpace, String categoryName) 
            throws BinaryCodecException, IOException {
        getAppData().parseCategories(parser, nameSpace, categoryName);
    }

    public void encodeCategories(BinarySerializer serializer, String nameSpace, String categoriesName, String categoryName)
            throws BinaryCodecException, IOException {
        getAppData().encodeCategories(serializer, nameSpace, categoriesName, categoryName);
    }
    
    public void addCategories(OperationContext octxt, Mailbox mbox, MailItem item) throws ServiceException {
        String tagStr = item.getTagString();
        if (tagStr != null && tagStr.length() > 0) {
            String[] tagIds = tagStr.split(",");
            for (String tagId : tagIds) {
                getAppData().addCategory(mbox.getTagById(octxt, Integer.parseInt(tagId)).getName());
            }
        }
    }
    
    public void saveMailItemCategories(OperationContext octxt, Mailbox mbox, int id, byte type) throws ServiceException {
        if (getAppData().getCategories() != null) {
            StringBuffer tagStr = new StringBuffer();
            for (String category : getAppData().getCategories()) {
                Tag tag = null;
                try {
                    tag = mbox.getTagByName(category);
                } catch (NoSuchItemException x) {
                    tag = mbox.createTag(octxt, category, MailItem.DEFAULT_COLOR);
                }
                if (tagStr.length() == 0) {
                    tagStr.append(tag.getId());
                } else {
                    tagStr.append(',').append(tag.getId());
                }
            }
            mbox.setTags(octxt, id, type, null, tagStr.toString(), null);
        }
    }
}
