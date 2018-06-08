/*
 * 
 */

package com.zimbra.cs.mailbox;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.MockOfflineProvisioning;
import com.zimbra.cs.datasource.DataSourceDbMapping;
import com.zimbra.cs.datasource.MockDataSourceDbMapping;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.mailbox.MockOfflineMailboxManager.Type;
import com.zimbra.cs.offline.common.OfflineConstants;

public class AutoArchiveTest {
    private static String accountId,localActId;
    private static MockDataSourceDbMapping mockMapping = new MockDataSourceDbMapping();
    private static DesktopMailbox localMBox;
    private static MockMailbox mbox;

    @BeforeClass
    public static void init() throws ServiceException {
        MockOfflineProvisioning prov = new MockOfflineProvisioning();
        Provisioning.setInstance(prov);

        accountId = prov.createAccount("test@zimbra.com", "secret",
                Collections.<String, Object>singletonMap(Provisioning.A_zimbraId, "0-0-0")).getId();

        MockMailboxManager.setInstance(new MockOfflineMailboxManager(Type.ZCS));
        mbox = (MockMailbox) new MockMailboxManager().getMailboxByAccountId(accountId);

        localActId = prov.createAccount("local@local.host", "", Collections.<String, Object>singletonMap(Provisioning.A_zimbraId, OfflineConstants.LOCAL_ACCOUNT_ID)).getId();
        localMBox =  (MockDesktopMailbox) new MockOfflineMailboxManager(Type.DESKTOP).getMailboxByAccountId(localActId);

        DataSourceDbMapping.setInstance(mockMapping);
    }

    private MailItem createMailItemWithMultipleTags(int id) throws ServiceException {
        //add item to mailbox with tags
        MailItem.UnderlyingData data = new MailItem.UnderlyingData();
        data.id = 111;
        data.type = MailItem.TYPE_MESSAGE;
        data.name = "test@zimbra.com";
        data.subject = "test email";
        data.flags = Flag.BITMASK_UNCACHED;

        Tag tag1 = mbox.createTag(mbox.getOperationContext(), "Tag1", (byte)5);
        Tag tag2 = mbox.createTag(mbox.getOperationContext(), "Tag2", (byte)3);

        data.tags = Tag.tagsToBitmask(tag1.getId() + "," + tag2.getId());
        Message item = new Message(mbox, data);
        return item;
    }

    @Test
    public void checkForTagString() throws ServiceException {
        MailItem item = createMailItemWithMultipleTags(111);

        //Get tag information from primary mailbox
        List<Tag> tagList = mbox.getTagList(mbox.getOperationContext());

        //This will check tag information in local mailbox, will create tags under local mailbox on the fly.
        AutoArchiveTask.getTagStringForArchivedItem(localMBox, tagList, item);

        //Check if required tags are created under local mailbox
        List<Tag> itemTags = item.getTagList();
        boolean noSuchItemExceptionThrown = false;
        for(Tag t : itemTags) {
            try {
                localMBox.getTagByName(t.getName());
            } catch (NoSuchItemException e) {
                noSuchItemExceptionThrown = true;
            } finally {
                Assert.assertFalse(noSuchItemExceptionThrown);
            }
        }
    }
}
