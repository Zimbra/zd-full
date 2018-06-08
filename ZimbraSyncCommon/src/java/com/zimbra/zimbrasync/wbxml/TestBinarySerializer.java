/*
 * 
 */
package com.zimbra.zimbrasync.wbxml;


import java.io.ByteArrayOutputStream;


import junit.framework.TestCase;

public class TestBinarySerializer extends TestCase {
	
	//<FolderSync xmlns="FolderHierarchy">
	//	<SyncKey>0</SyncKey>
	//</FolderSync>
	private byte[] FOLDERSYNC_REQUEST = {
		0x03, 0x01, 0x6A, 0x00, 0x00, 0x07, 0x56, 0x52, 0x03, 0x30, 0x00, 0x01, 0x01
	};
	
    public void testParseFolderSyncRequest()
		throws Exception {
    	
    	ByteArrayOutputStream bao = new ByteArrayOutputStream();
    	BinarySerializer serializer = new BinarySerializer(bao, true);
    	
    	serializer.openTag(BinaryCodepages.NAMESPACE_FOLDERHIERARCHY, BinaryCodepages.FOLDERHIERARCHY_FOLDERSYNC);
    	assertTrue(serializer.getDepth() == 1);
    	serializer.openTag(BinaryCodepages.NAMESPACE_FOLDERHIERARCHY, BinaryCodepages.FOLDERHIERARCHY_SYNCKEY);
    	assertTrue(serializer.getDepth() == 2);
    	serializer.integerContent(0);
    	assertTrue(serializer.getDepth() == 2);
    	serializer.closeTag();
    	assertTrue(serializer.getDepth() == 1);
    	serializer.closeTag();
    	assertTrue(serializer.getDepth() == 0);
    	
    	assertTrue(new String(bao.toByteArray()).equals(new String(FOLDERSYNC_REQUEST)));
    }
    
	//<Sync xmlns="AirSync">
	//    <Collections>
	//        <Collection>
	//            <Class>Email</Class>
	//            <SyncKey>{F1252269-B8CE-422E-8109-8DE067B7B0BA}3</SyncKey>
	//            <CollectionId>ef23694f89d86f40bad1e46d83415b4a-281d</CollectionId>
	//            <DeletesAsMoves/>
	//            <GetChanges/>
	//            <WindowSize>100</WindowSize>
	//            <Options>
	//                <FilterType>2</FilterType>
	//                <Truncation>1</Truncation>
	//                <Conflict>1</Conflict>
	//            </Options>
	//        </Collection>
	//    </Collections>
	//</Sync>
    private static byte[] SYNC_REQUEST = {
    	0x03, 0x01, 0x6A, 0x00, 0x45, 0x5C, 0x4F, 0x50, 0x03, 0x45, 0x6D, 0x61, 0x69, 0x6C, 0x00, 0x01,
    	0x4B, 0x03, 0x7B, 0x46, 0x31, 0x32, 0x35, 0x32, 0x32, 0x36, 0x39, 0x2D, 0x42, 0x38, 0x43, 0x45,
    	0x2D, 0x34, 0x32, 0x32, 0x45, 0x2D, 0x38, 0x31, 0x30, 0x39, 0x2D, 0x38, 0x44, 0x45, 0x30, 0x36,
    	0x37, 0x42, 0x37, 0x42, 0x30, 0x42, 0x41, 0x7D, 0x33, 0x00, 0x01, 0x52, 0x03, 0x65, 0x66, 0x32,
    	0x33, 0x36, 0x39, 0x34, 0x66, 0x38, 0x39, 0x64, 0x38, 0x36, 0x66, 0x34, 0x30, 0x62, 0x61, 0x64,
    	0x31, 0x65, 0x34, 0x36, 0x64, 0x38, 0x33, 0x34, 0x31, 0x35, 0x62, 0x34, 0x61, 0x2D, 0x32, 0x38,
    	0x31, 0x64, 0x00, 0x01, 0x1E, 0x13, 0x55, 0x03, 0x31, 0x30, 0x30, 0x00, 0x01, 0x57, 0x58, 0x03,
    	0x32, 0x00, 0x01, 0x59, 0x03, 0x31, 0x00, 0x01, 0x5B, 0x03, 0x31, 0x00, 0x01, 0x01, 0x01, 0x01,
    	0x01
    };
    
    public void testParseSyncRequest() throws Exception {
    	ByteArrayOutputStream bao = new ByteArrayOutputStream();
    	BinarySerializer serializer = new BinarySerializer(bao, true);
    	
    	serializer.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SYNC);
    	serializer.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTIONS);
    	serializer.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTION);
    	serializer.textElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_CLASS, "Email");
    	serializer.textElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SYNCKEY, "{F1252269-B8CE-422E-8109-8DE067B7B0BA}3");
    	serializer.textElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTIONID, "ef23694f89d86f40bad1e46d83415b4a-281d");
    	serializer.emptyElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_DELETESASMOVES);
    	serializer.emptyElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_GETCHANGES);
    	serializer.integerElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_WINDOWSIZE, 100);
    	serializer.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_OPTIONS);
    	serializer.integerElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_FILTERTYPE, 2);
    	serializer.integerElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_TRUNCATION, 1);
    	serializer.integerElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_CONFLICT, 1);
    	serializer.closeTag();
    	serializer.closeTag();
    	serializer.closeTag();
    	serializer.closeTag();

    	assertTrue(new String(bao.toByteArray()).equals(new String(SYNC_REQUEST)));    
    }
    
	//<Sync xmlns="AirSync" xmlns:A="POOMMAIL">
	//    <Collections>
	//        <Collection>
	//            <Class>Email</Class>
	//            <SyncKey>{F1252269-B8CE-422E-8109-8DE067B7B0BA}4</SyncKey>
	//            <CollectionId>ef23694f89d86f40bad1e46d83415b4a-281d</CollectionId>
	//            <Status>1</Status>
	//            <Commands>
	//                <Change>
	//                    <ServerId>rid:ef23694f89d86f40bad1e46d83415b4a000000004962</ServerId>
	//                    <ApplicationData>
	//                        <A:Read>1</A:Read>
	//                    </ApplicationData>
	//                </Change>
	//                <Add>
	//                    <ServerId>rid:ef23694f89d86f40bad1e46d83415b4a000000004963</ServerId>
	//                    <ApplicationData>
	//                        <A:To>"J.J. Zhuang" &lt;jjzhuang@testbed.local&gt;</A:To>
	//                        <A:From>"J.J. Zhuang" &lt;jjzhuang@testbed.local&gt;</A:From>
	//                        <A:Subject>Sub2</A:Subject>
	//                        <A:DateReceived>2005-07-01T08:18:09.706Z</A:DateReceived>
	//                        <A:DisplayTo>J.J. Zhuang</A:DisplayTo>
	//                        <A:Importance>2</A:Importance>
	//                        <A:Read>0</A:Read>
	//                        <A:BodyTruncated>0</A:BodyTruncated>
	//                        <A:Body>Body2</A:Body>
	//                        <A:MessageClass>IPM.Note</A:MessageClass>
	//                    </ApplicationData>
	//                </Add>
	//            </Commands>
	//        </Collection>
	//    </Collections>
	//</Sync>
    private static byte[] SYNC_RESPONSE = {
    	0x03, 0x01, 0x6A, 0x00, 0x45, 0x5C, 0x4F, 0x50, 0x03, 0x45, 0x6D, 0x61, 0x69, 0x6C, 0x00, 0x01,
    	0x4B, 0x03, 0x7B, 0x46, 0x31, 0x32, 0x35, 0x32, 0x32, 0x36, 0x39, 0x2D, 0x42, 0x38, 0x43, 0x45,
    	0x2D, 0x34, 0x32, 0x32, 0x45, 0x2D, 0x38, 0x31, 0x30, 0x39, 0x2D, 0x38, 0x44, 0x45, 0x30, 0x36,
    	0x37, 0x42, 0x37, 0x42, 0x30, 0x42, 0x41, 0x7D, 0x34, 0x00, 0x01, 0x52, 0x03, 0x65, 0x66, 0x32,
    	0x33, 0x36, 0x39, 0x34, 0x66, 0x38, 0x39, 0x64, 0x38, 0x36, 0x66, 0x34, 0x30, 0x62, 0x61, 0x64,
    	0x31, 0x65, 0x34, 0x36, 0x64, 0x38, 0x33, 0x34, 0x31, 0x35, 0x62, 0x34, 0x61, 0x2D, 0x32, 0x38,
    	0x31, 0x64, 0x00, 0x01, 0x4E, 0x03, 0x31, 0x00, 0x01, 0x56, 0x48, 0x4D, 0x03, 0x72, 0x69, 0x64,
    	0x3A, 0x65, 0x66, 0x32, 0x33, 0x36, 0x39, 0x34, 0x66, 0x38, 0x39, 0x64, 0x38, 0x36, 0x66, 0x34,
    	0x30, 0x62, 0x61, 0x64, 0x31, 0x65, 0x34, 0x36, 0x64, 0x38, 0x33, 0x34, 0x31, 0x35, 0x62, 0x34,
    	0x61, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x34, 0x39, 0x36, 0x32, 0x00, 0x01, 0x5D,
    	0x00, 0x02, 0x55, 0x03, 0x31, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x47, 0x4D, 0x03, 0x72, 0x69,
    	0x64, 0x3A, 0x65, 0x66, 0x32, 0x33, 0x36, 0x39, 0x34, 0x66, 0x38, 0x39, 0x64, 0x38, 0x36, 0x66,
    	0x34, 0x30, 0x62, 0x61, 0x64, 0x31, 0x65, 0x34, 0x36, 0x64, 0x38, 0x33, 0x34, 0x31, 0x35, 0x62,
    	0x34, 0x61, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x34, 0x39, 0x36, 0x33, 0x00, 0x01,
    	0x5D, 0x00, 0x02, 0x56, 0x03, 0x22, 0x4A, 0x2E, 0x4A, 0x2E, 0x20, 0x5A, 0x68, 0x75, 0x61, 0x6E,
    	0x67, 0x22, 0x20, 0x3C, 0x6A, 0x6A, 0x7A, 0x68, 0x75, 0x61, 0x6E, 0x67, 0x40, 0x74, 0x65, 0x73,
    	0x74, 0x62, 0x65, 0x64, 0x2E, 0x6C, 0x6F, 0x63, 0x61, 0x6C, 0x3E, 0x00, 0x01, 0x58, 0x03, 0x22,
    	0x4A, 0x2E, 0x4A, 0x2E, 0x20, 0x5A, 0x68, 0x75, 0x61, 0x6E, 0x67, 0x22, 0x20, 0x3C, 0x6A, 0x6A,
    	0x7A, 0x68, 0x75, 0x61, 0x6E, 0x67, 0x40, 0x74, 0x65, 0x73, 0x74, 0x62, 0x65, 0x64, 0x2E, 0x6C,
    	0x6F, 0x63, 0x61, 0x6C, 0x3E, 0x00, 0x01, 0x54, 0x03, 0x53, 0x75, 0x62, 0x32, 0x00, 0x01, 0x4F,
    	0x03, 0x32, 0x30, 0x30, 0x35, 0x2D, 0x30, 0x37, 0x2D, 0x30, 0x31, 0x54, 0x30, 0x38, 0x3A, 0x31,
    	0x38, 0x3A, 0x30, 0x39, 0x2E, 0x37, 0x30, 0x36, 0x5A, 0x00, 0x01, 0x51, 0x03, 0x4A, 0x2E, 0x4A,
    	0x2E, 0x20, 0x5A, 0x68, 0x75, 0x61, 0x6E, 0x67, 0x00, 0x01, 0x52, 0x03, 0x32, 0x00, 0x01, 0x55,
    	0x03, 0x30, 0x00, 0x01, 0x4E, 0x03, 0x30, 0x00, 0x01, 0x4C, 0x03, 0x42, 0x6F, 0x64, 0x79, 0x32,
    	0x00, 0x01, 0x53, 0x03, 0x49, 0x50, 0x4D, 0x2E, 0x4E, 0x6F, 0x74, 0x65, 0x00, 0x01, 0x01, 0x01,
    	0x01, 0x01, 0x01, 0x01,
    };
    
    public void testParseSyncResponse() throws Exception {
    	ByteArrayOutputStream bao = new ByteArrayOutputStream();
    	BinarySerializer serializer = new BinarySerializer(bao, true);
    	
    	serializer.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SYNC);
    	serializer.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTIONS);
    	serializer.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTION);
    	serializer.textElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_CLASS, "Email");
    	serializer.textElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SYNCKEY, "{F1252269-B8CE-422E-8109-8DE067B7B0BA}4");
    	serializer.textElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTIONID, "ef23694f89d86f40bad1e46d83415b4a-281d");
    	serializer.integerElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_STATUS, 1);
    	serializer.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COMMANDS);
    	serializer.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_CHANGE);
    	serializer.textElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SERVERID, "rid:ef23694f89d86f40bad1e46d83415b4a000000004962");
    	serializer.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_APPLICATIONDATA);
    	serializer.integerElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_READ, 1);
    	serializer.closeTag();
    	serializer.closeTag();
    	serializer.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_ADD);
    	serializer.textElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SERVERID, "rid:ef23694f89d86f40bad1e46d83415b4a000000004963");
    	serializer.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_APPLICATIONDATA);
    	serializer.textElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_TO, "\"J.J. Zhuang\" <jjzhuang@testbed.local>");
    	serializer.textElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_FROM, "\"J.J. Zhuang\" <jjzhuang@testbed.local>");
    	serializer.textElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_SUBJECT, "Sub2");
    	serializer.textElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_DATERECEIVED, "2005-07-01T08:18:09.706Z");
    	serializer.textElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_DISPLAYTO, "J.J. Zhuang");
    	serializer.integerElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_IMPORTANCE, 2);
    	serializer.integerElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_READ, 0);
    	serializer.integerElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_BODYTRUNCATED, 0);
    	serializer.textElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_BODY, "Body2");
    	serializer.textElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_MESSAGECLASS, "IPM.Note");
    	serializer.closeTag();
    	serializer.closeTag();   	
    	serializer.closeTag();
    	serializer.closeTag();
    	serializer.closeTag();
    	serializer.closeTag();

    	assertTrue(new String(bao.toByteArray()).equals(new String(SYNC_RESPONSE)));
    }
}

