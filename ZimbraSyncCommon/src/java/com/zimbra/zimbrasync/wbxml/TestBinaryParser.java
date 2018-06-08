/*
 * 
 */
package com.zimbra.zimbrasync.wbxml;

import junit.framework.TestCase;
import java.io.ByteArrayInputStream;

public class TestBinaryParser extends TestCase {
	
	//<FolderSync xmlns="FolderHierarchy">
	//	<SyncKey>0</SyncKey>
	//</FolderSync>
	private byte[] FOLDERSYNC_REQUEST = {
		0x03, 0x01, 0x6A, 0x00, 0x00, 0x07, 0x56, 0x52, 0x03, 0x30, 0x00, 0x01, 0x01
	};
	
    public void testParseFolderSyncRequest()
		throws Exception {
    	
    	BinaryParser parser = new BinaryParser(new ByteArrayInputStream(FOLDERSYNC_REQUEST), true);
    	
    	int event = parser.nextToken();
    	assertTrue(event == BinaryParser.START_TAG);
    	assertTrue(parser.getNamespace() == BinaryCodepages.NAMESPACE_FOLDERHIERARCHY);
    	assertTrue(parser.getName() == BinaryCodepages.FOLDERHIERARCHY_FOLDERSYNC);
    	assertTrue(!parser.isEmptyElementTag());
    	assertTrue(parser.getDepth() == 1);

    	event = parser.nextToken();
    	parser.require(BinaryParser.START_TAG,
    			BinaryCodepages.NAMESPACE_FOLDERHIERARCHY, BinaryCodepages.FOLDERHIERARCHY_SYNCKEY);
    	assertTrue(!parser.isEmptyElementTag());
    	assertTrue(parser.getDepth() == 2);

    	event = parser.nextToken();
    	parser.require(BinaryParser.TEXT, null, null);
    	assertEquals(parser.getContentAsInteger(), 0);
    	assertTrue(parser.getDepth() == 2);
    	
    	event = parser.nextToken();
    	parser.require(BinaryParser.END_TAG,
    			BinaryCodepages.NAMESPACE_FOLDERHIERARCHY, BinaryCodepages.FOLDERHIERARCHY_SYNCKEY);
    	assertTrue(parser.getDepth() == 1);
    	
    	event = parser.nextToken();
    	parser.require(BinaryParser.END_TAG,
    			BinaryCodepages.NAMESPACE_FOLDERHIERARCHY, BinaryCodepages.FOLDERHIERARCHY_FOLDERSYNC);
    	assertTrue(parser.getDepth() == 0);
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

		BinaryParser parser = new BinaryParser(new ByteArrayInputStream(SYNC_REQUEST), true);
		
		parser.nextTag();
		parser.require(BinaryParser.START_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SYNC);
		parser.nextTag();
		parser.require(BinaryParser.START_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTIONS);
		parser.nextTag();
		parser.require(BinaryParser.START_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTION);

		parser.nextTag();
		parser.require(BinaryParser.START_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_CLASS);
		String text = parser.nextText();
		assertEquals(text, "Email");
		parser.require(BinaryParser.END_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_CLASS);
		
		parser.nextTag();
		parser.require(BinaryParser.START_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SYNCKEY);
		text = parser.nextText();
		assertEquals(text, "{F1252269-B8CE-422E-8109-8DE067B7B0BA}3");
		parser.require(BinaryParser.END_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SYNCKEY);
		
		parser.nextTag();
		parser.require(BinaryParser.START_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTIONID);
		text = parser.nextText();
		assertEquals(text, "ef23694f89d86f40bad1e46d83415b4a-281d");
		parser.require(BinaryParser.END_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTIONID);

		parser.nextTag();
		parser.require(BinaryParser.START_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_DELETESASMOVES);
		assertTrue(parser.isEmptyElementTag());
		text = parser.nextText();
		assertEquals(text, "");
		parser.require(BinaryParser.END_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_DELETESASMOVES);

		parser.nextTag();
		parser.require(BinaryParser.START_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_GETCHANGES);
		assertTrue(parser.isEmptyElementTag());
		text = parser.nextText();
		assertEquals(text, "");
		parser.require(BinaryParser.END_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_GETCHANGES);
		
		parser.nextTag();
		parser.require(BinaryParser.START_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_WINDOWSIZE);
		assertEquals(parser.nextIntegerContent(), 100);
		parser.require(BinaryParser.END_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_WINDOWSIZE);
		
		parser.nextTag();
		parser.require(BinaryParser.START_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_OPTIONS);
		
		parser.nextTag();
		parser.require(BinaryParser.START_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_FILTERTYPE);
		assertEquals(parser.nextIntegerContent(), 2);
		parser.require(BinaryParser.END_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_FILTERTYPE);
		
		parser.nextTag();
		parser.require(BinaryParser.START_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_TRUNCATION);
		assertEquals(parser.nextIntegerContent(), 1);
		parser.require(BinaryParser.END_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_TRUNCATION);
		
		parser.nextTag();
		parser.require(BinaryParser.START_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_CONFLICT);
		assertEquals(parser.nextIntegerContent(), 1);
		parser.require(BinaryParser.END_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_CONFLICT);
		
		parser.nextTag();
		parser.require(BinaryParser.END_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_OPTIONS);

		parser.nextTag();
		parser.require(BinaryParser.END_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTION);
		parser.nextTag();
		parser.require(BinaryParser.END_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTIONS);
		parser.nextTag();
		parser.require(BinaryParser.END_TAG, BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SYNC);

		assertTrue(parser.getDepth() == 0);
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
    	
		BinaryParser parser = new BinaryParser(new ByteArrayInputStream(SYNC_RESPONSE), true);

		parser.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SYNC);
		parser.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTIONS);
		parser.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTION);

		String text = parser.nextTextElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_CLASS);
		assertEquals(text, "Email");
		
		text = parser.nextTextElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SYNCKEY);
		assertEquals(text, "{F1252269-B8CE-422E-8109-8DE067B7B0BA}4");

		text = parser.nextTextElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COLLECTIONID);
		assertEquals(text, "ef23694f89d86f40bad1e46d83415b4a-281d");

		int number = parser.nextIntegerElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_STATUS);
		assertEquals(number, 1);
		
		parser.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_COMMANDS);
		parser.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_CHANGE);
		
		text = parser.nextTextElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SERVERID);
		assertEquals(text, "rid:ef23694f89d86f40bad1e46d83415b4a000000004962");
		
		parser.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_APPLICATIONDATA);
		
		number = parser.nextIntegerElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_READ);
		assertEquals(number, 1);
		
		parser.closeTag();
		
		parser.closeTag();
		parser.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_ADD);
		
		text = parser.nextTextElement(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_SERVERID);
		assertEquals(text, "rid:ef23694f89d86f40bad1e46d83415b4a000000004963");
		
		parser.openTag(BinaryCodepages.NAMESPACE_AIRSYNC, BinaryCodepages.AIRSYNC_APPLICATIONDATA);
		
		text = parser.nextTextElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_TO);
		assertEquals(text, "\"J.J. Zhuang\" <jjzhuang@testbed.local>");

		text = parser.nextTextElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_FROM);
		assertEquals(text, "\"J.J. Zhuang\" <jjzhuang@testbed.local>");
		
		text = parser.nextTextElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_SUBJECT);
		assertEquals(text, "Sub2");
		
		text = parser.nextTextElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_DATERECEIVED);
		assertEquals(text, "2005-07-01T08:18:09.706Z");
		
		text = parser.nextTextElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_DISPLAYTO);
		assertEquals(text, "J.J. Zhuang");
		
		number = parser.nextIntegerElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_IMPORTANCE);
		assertEquals(number, 2);
		
		number = parser.nextIntegerElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_READ);
		assertEquals(number, 0);
		
		number = parser.nextIntegerElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_BODYTRUNCATED);
		assertEquals(number, 0);
		
		text = parser.nextTextElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_BODY);
		assertEquals(text, "Body2");
		
		text = parser.nextTextElement(BinaryCodepages.NAMESPACE_POOMMAIL, BinaryCodepages.POOMMAIL_MESSAGECLASS);
		assertEquals(text, "IPM.Note");
	
		parser.closeTag();
		
		parser.closeTag();

		parser.closeTag();
		parser.closeTag();
		parser.closeTag();
		parser.closeTag();
	}
}


