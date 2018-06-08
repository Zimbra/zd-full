/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class FolderCreate extends FolderCommand {

	private String parentId;
	private String displayName;
	private int type;
	
	private String serverId;
	
	public FolderCreate(String clientSyncKey, String parentId, String displayName, int type) {
		super(clientSyncKey);
		this.parentId = parentId;
		this.displayName = displayName;
		this.type = type;
	}

	public String getServerId() {
		return serverId;
	}
	
	@Override
	protected void encodeRequest(BinarySerializer bs) throws BinaryCodecException, IOException {
		bs.openTag(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_FOLDERCREATE);
		bs.textElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SYNCKEY, clientSyncKey);
		bs.textElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_PARENTID, parentId);
		bs.textElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_DISPLAYNAME, displayName);
		bs.integerElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_TYPE, type);
		bs.closeTag();
	}

	@Override
	public void parseResponse(BinaryParser bp) throws BinaryCodecException, IOException {
		bp.openTag(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_FOLDERCREATE);
		status = bp.nextIntegerElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_STATUS);
		if (status == 1) {
			serverSyncKey = bp.nextTextElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SYNCKEY);
			serverId = bp.nextTextElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SERVERID);
		}
		bp.closeTag();
	}
}
