/*
 * 
 */


package com.zimbra.cs.mailbox;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.common.service.ServiceException;

public class TagUtils {
    private int tagId = 66;
    private List<Tag> tags = new ArrayList<Tag>();

    Tag createTag(Mailbox mbox, String name,
            byte color) throws ServiceException {
        MailItem.UnderlyingData data = new MailItem.UnderlyingData();
        data.type = MailItem.TYPE_TAG;
        data.name = name;
        data.id = tagId--;
        Tag t = new Tag(mbox, data);
        tags.add(t);
        return t;
    }

    List<Tag> getTags() {
        return tags;
    }

    Tag getTagByName(String name) throws ServiceException {
        for(Tag t : tags) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        throw MailServiceException.NO_SUCH_TAG(name);
    }

    Tag getTagById(int id) throws ServiceException {
        for(Tag t : tags) {
            if(t.getId() == id) {
                return t;
            }
        }
        return null;
    }
}
