/*
 * 
 */
package com.zimbra.cs.mailbox;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mime.ParsedDocument;

public class WikiItem extends Document {
	
	WikiItem(Mailbox mbox, UnderlyingData data) throws ServiceException {
		super(mbox, data);
	}
	
	public String getWikiWord() {
		return getName();
	}

    public static final String WIKI_CONTENT_TYPE = "text/html; charset=utf-8";
	
    static WikiItem create(int id, Folder folder, String wikiword, ParsedDocument pd, CustomMetadata custom)
    throws ServiceException {
        Metadata meta = new Metadata();
        UnderlyingData data = prepareCreate(TYPE_WIKI, id, folder, wikiword, WIKI_CONTENT_TYPE, pd, meta, custom);

		Mailbox mbox = folder.getMailbox();
		data.contentChanged(mbox);
        ZimbraLog.mailop.info("Adding WikiItem %s: id=%d, folderId=%d, folderName=%s.",
            wikiword, data.id, folder.getId(), folder.getName());
        DbMailItem.create(mbox, data, null);

        WikiItem wiki = new WikiItem(mbox, data);
        wiki.finishCreation(null);
        pd.setVersion(wiki.getVersion());
        return wiki;
    }
}
