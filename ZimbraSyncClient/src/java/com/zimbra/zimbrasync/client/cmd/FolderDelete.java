/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class FolderDelete extends FolderCommand {

	private String serverId;
	
	public FolderDelete(String clientSyncKey, String serverId) {
		super(clientSyncKey);
		this.serverId = serverId;
	}
	
	@Override
	protected void encodeRequest(BinarySerializer bs) throws BinaryCodecException, IOException {
		bs.openTag(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_FOLDERDELETE);
		bs.textElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SYNCKEY, clientSyncKey);
		bs.textElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SERVERID, serverId);
		bs.closeTag();
	}

	@Override
	public void parseResponse(BinaryParser bp) throws BinaryCodecException, IOException {
		bp.openTag(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_FOLDERDELETE);
		status = bp.nextIntegerElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_STATUS);
		if (status == 1)
			serverSyncKey = bp.nextTextElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SYNCKEY);
		bp.closeTag();
	}
}
