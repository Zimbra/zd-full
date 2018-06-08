/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class FolderUpdate extends FolderCommand {

	private String serverId;
	private String parentId;
	private String displayName;
	
	public FolderUpdate(String clientSyncKey, String serverId, String parentId, String displayName) {
		super(clientSyncKey);
		this.serverId = serverId;
		this.parentId = parentId;
		this.displayName = displayName;
	}
	

	@Override
	protected void encodeRequest(BinarySerializer bs) throws BinaryCodecException, IOException {
		bs.openTag(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_FOLDERUPDATE);
		bs.textElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SYNCKEY, clientSyncKey);
		bs.textElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SERVERID, serverId);
		bs.textElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_PARENTID, parentId);
		bs.textElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_DISPLAYNAME, displayName);
		bs.closeTag();
	}

	@Override
	public void parseResponse(BinaryParser bp) throws BinaryCodecException, IOException {
		bp.openTag(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_FOLDERUPDATE);
		status = bp.nextIntegerElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_STATUS);
		if (status == 1)
			serverSyncKey = bp.nextTextElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SYNCKEY);
		bp.closeTag();
	}
	
}
