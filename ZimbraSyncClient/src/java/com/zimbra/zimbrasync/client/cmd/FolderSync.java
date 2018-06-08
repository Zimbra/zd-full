/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

/**
 * @author JJ Zhuang
 */
public class FolderSync extends FolderCommand {
	
	public static class FolderSyncAdd {
		public String serverId;
		public String parentId;
		public String displayName;
		public int type;
		
		public FolderSyncAdd(String serverId, String parentId, String displayName, int type) {
			this.serverId = serverId;
			this.parentId = parentId;
			this.displayName = displayName;
			this.type = type;
		}
	};
	
	public static class FolderSyncUpdate {
		public String serverId;
		public String parentId;
		public String displayName;
		public int type;
		
		public FolderSyncUpdate(String serverId, String parentId, String displayName, int type) {
			this.serverId = serverId;
			this.parentId = parentId;
			this.displayName = displayName;
			this.type = type;
		}
	};
	
	public static class FolderSyncDelete {
		public String serverId;
		
		public FolderSyncDelete(String serverId) {
			this.serverId = serverId;
		}
	};

	private List<FolderSyncAdd> adds = new ArrayList<FolderSyncAdd>();
	private List<FolderSyncUpdate> updates = new ArrayList<FolderSyncUpdate>();
	private List<FolderSyncDelete> deletes = new ArrayList<FolderSyncDelete>();
	
	
	public FolderSync(String clientSyncKey) {
		super(clientSyncKey);
	}
	
	public List<FolderSyncAdd> getAdds() {
		return adds;
	}
	
	public List<FolderSyncUpdate> getUpdates() {
		return updates;
	}
	
	public List<FolderSyncDelete> getDeletes() {
		return deletes;
	}
	
	@Override
	protected void encodeRequest(BinarySerializer bs) throws BinaryCodecException, IOException {
		bs.openTag(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_FOLDERSYNC);
		bs.textElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SYNCKEY, clientSyncKey);
		bs.closeTag();
	}

//	<FolderSync xmlns="FolderHierarchy">
//	    <Status>1</Status>
//	    <SyncKey>{60BA9403-5CFD-45FD-9CFE-4AE0E7D23791}1</SyncKey>
//	    <Changes>
//	        <Count>3</Count>
//	        <Add>
//	            <ServerId>0286243fce792f4a82f3686ac614bc47-2736</ServerId>
//	            <ParentId>0</ParentId>
//	            <DisplayName>Calendar</DisplayName>
//	            <Type>8</Type>
//          </Add>
//		    <Add>
//			    <ServerId>0286243fce792f4a82f3686ac614bc47-2737</ServerId>
//			    <ParentId>0</ParentId>
//			    <DisplayName>Contacts</DisplayName>
//		        <Type>9</Type>
//			</Add>
//		    <Add>
//			    <ServerId>0286243fce792f4a82f3686ac614bc47-281d</ServerId>
//			    <ParentId>0</ParentId>
//			    <DisplayName>Inbox</DisplayName>
//			    <Type>2</Type>
//		    </Add>
//	    </Changes>
//	</FolderSync>
	@Override
	public void parseResponse(BinaryParser bp) throws BinaryCodecException, IOException {
		bp.openTag(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_FOLDERSYNC);
		status = bp.nextIntegerElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_STATUS);
		if (status == 1) {
			serverSyncKey = bp.nextTextElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SYNCKEY);
			bp.openTag(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_CHANGES);
			int count = bp.nextIntegerElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_COUNT);
			for (int i = 0; i < count; ++i) {
				bp.next();
				if (bp.getName().equals(FOLDERHIERARCHY_ADD)) {
					String serverId = bp.nextTextElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SERVERID);
					String parentId = bp.nextTextElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_PARENTID);
					String displayName = bp.nextTextElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_DISPLAYNAME);
					int type = bp.nextIntegerElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_TYPE);
					adds.add(new FolderSyncAdd(serverId, parentId, displayName, type));
				} else if (bp.getName().equals(FOLDERHIERARCHY_UPDATE)) {
					String serverId = bp.nextTextElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SERVERID);
					String parentId = bp.nextTextElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_PARENTID);
					String displayName = bp.nextTextElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_DISPLAYNAME);
					int type = bp.nextIntegerElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_TYPE);
					updates.add(new FolderSyncUpdate(serverId, parentId, displayName, type));
				} else if (bp.getName().equals(FOLDERHIERARCHY_DELETE)) {
					String serverId = bp.nextTextElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SERVERID);
					deletes.add(new FolderSyncDelete(serverId));
				}
				bp.closeTag(); //Add, Update or Delete
			}
			bp.closeTag(); //Changes
		}
		bp.closeTag(); //FolderSync
	}
}
