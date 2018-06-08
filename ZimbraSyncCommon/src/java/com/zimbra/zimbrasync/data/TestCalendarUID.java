/*
 * 
 */
package com.zimbra.zimbrasync.data;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.zimbra.zimbrasync.data.CalendarUID.InviteStub;

import junit.framework.TestCase;

public class TestCalendarUID extends TestCase {

	public void testUidToGlobalObjId() throws Exception {
		String goid = CalendarUID.uidToGlobalObjId("foobar123@abc.com", 1159864211000l);
		assertEquals(goid, "BAAAAIIA4AB0xbcQGoLgCAfWCgMAAAAAAAAAAAAAAAAAAAAAHgAAAHZDYWwtVWlkAQAAAGZvb2JhcjEyM0BhYmMuY29tAA==");
	}
	
	public void testClientHexSeqToServerUid() throws Exception {
		InviteStub result = CalendarUID.makeInviteIdFromGlobalObjId("foobar123@abc.com");
		assertNull(result);
		
		result = CalendarUID.makeInviteIdFromGlobalObjId("0123456789ABCDEF");
		assertNull(result);
		
		result = CalendarUID.makeInviteIdFromGlobalObjId("040000008200E00074C5B7101A82E0080000000000E2071470C4C601000000000000000010000000974D2325BAEA914F9E0C97EC5173BF48");
		assertEquals(result.uid, "040000008200E00074C5B7101A82E0080000000000E2071470C4C601000000000000000010000000974D2325BAEA914F9E0C97EC5173BF48");
		assertFalse(result.isException());
		
		result = CalendarUID.makeInviteIdFromGlobalObjId("040000008200E00074C5B7101A82E00807D60A03000000000000000000000000000000001E0000007643616C2D55696401000000666F6F626172313233406162632E636F6D00");
		assertEquals(result.uid, "foobar123@abc.com");
		assertTrue(result.isException());
		assertTrue(result.isEquivRecurrenceId(1159864211000l));
	}

	public void testHexSeqStrToBytes() throws Exception {
		
		byte[] bytes = {
			(byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x82, (byte)0x00, (byte)0xE0, (byte)0x00,
			(byte)0x74, (byte)0xC5, (byte)0xB7, (byte)0x10, (byte)0x1A, (byte)0x82, (byte)0xE0, (byte)0x08,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xE2, (byte)0x07, (byte)0x14,
			(byte)0x70, (byte)0xC4, (byte)0xC6, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x97, (byte)0x4D, (byte)0x23, (byte)0x25, (byte)0xBA, (byte)0xEA, (byte)0x91, (byte)0x4F,
			(byte)0x9E, (byte)0x0C, (byte)0x97, (byte)0xEC, (byte)0x51, (byte)0x73, (byte)0xBF, (byte)0x48
		};
		
		byte[] result = CalendarUID.hexSeqStrToBytes("040000008200E00074C5B7101A82E0080000000000E2071470C4C601000000000000000010000000974D2325BAEA914F9E0C97EC5173BF48");
		assertTrue(CalendarUID.equals(result, 0, bytes, 0, result.length));
	}
	
	public static void main(String[] args) {
		String goid = CalendarUID.uidToGlobalObjId("8f478e35-e096-49cf-840a-5c3705ba008d", System.currentTimeMillis());
		System.out.println(goid);
		
		byte[] raw = Base64.decode(goid);
		System.out.println(CalendarUID.bytesToHexSeqStr(raw));
	}
}
