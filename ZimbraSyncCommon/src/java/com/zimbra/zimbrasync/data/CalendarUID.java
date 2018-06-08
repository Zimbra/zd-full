/*
 * 
 */
package com.zimbra.zimbrasync.data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;

import com.zimbra.common.util.ZimbraLog;

/*
Notes on UID <-> GlobalObjId

The relationship between the GlobalObjId and the calendar UID seems to be quite
cryptic. Therefore it's probably a good idea to write down very thing I've
figured out so far so that I don't completely forget 2 days later.

If the meeting is first composed with an exchange server using clients such as
Outlook, the exchange web UI or a PocketPC sync client, the UID of the calendar
item is always in hex string format like this:
040000008200E00074C5B7101A82E0080000000000E2071470C4C601000000000000000010000000974D2325BAEA914F9E0C97EC5173BF48

If we look at this string as the hex values of a sequence of bytes like this:
04 00 00 00 82 00 E0 ..., we can recover the byte sequence.  Let's call the
recovered bytes "raw UID".  Raw UID seems to be completely meaningless, at
least to me.  The only thing I can observ is that the first 20 bytes are fixed.
Byte 21 to 28 vary slightly from one meeting to another.  Byte 29 to 40 again
is pretty much fixed.  Then byte 41 to 56 seems to be the real identifier.

When a meeting invitation is downloaded to sync client, the GlobalObjId field
contains base64 encoded raw UID like this:
BAAAAIIA4AB0xbcQGoLgCAAAAAAA4gcUcMTGAQAAAAAAAAAAEAAAAJdNIyW66pFPngyX7FFzv0g=

However when the meeting is downloaded to client as calendar item the UID is
040000008200E00074C5B7101A82E0080000000000E2071470C4C601000000000000000010000000974D2325BAEA914F9E0C97EC5173BF48
in hex value sequence format.  This relationship is understood not only by the
server but also by clients such as PocketPC or Treo650.  When Treo650 responds
to the meeting invitation, the UID specified in the text/calendar part is
040000008200E00074C5B7101A82E0080000000000E2071470C4C601000000000000000010000000974D2325BAEA914F9E0C97EC5173BF48.
If the client is PocketPC, the TNEF reply contains a MAPI property 0x8203 with
binary byte sequence of raw UID.

So far we've discussed only the case when the meeting was originally composed
by an exchange server client.  If the meeting invitation comes from outside,
the UID can be anything and can't be expected to conform to the format of a hex
value sequence.  For example, assume an incoming vCal invitation has a UID of
foobar123@abc.com, the meeting calendar item will be created with a UID of
foobar123@abc.com. In this case when downloading to sync client the GlobalObjId
is BAAAAIIA4AB0xbcQGoLgCAAAAAAAAAAAAAAAAAAAAAAAAAAAHgAAAHZDYWwtVWlkAQAAAGZvb2JhcjEyM0BhYmMuY29tAA==
	
If we base64 decode this GlobalObjId we get a binary sequence like this:
040000008200E00074C5B7101A82E00800000000000000000000000000000000000000001E0000007643616C2D55696401000000666F6F626172313233406162632E636F6D00

Note that this time around we can find some meaning from this sequence.  The
7643616C2D556964 part translates to "vCal-Uid" and the
666F6F626172313233406162632E636F6D is actually "foobar123@abc.com".  There is
also a terminating 00 byte at the end.

The difference between UID created within exchange and UID from external is
that in the case of external UID, only the last part of GlobalObjId contains
calendar item UID.  That is different from native UID where the whole
GlobalObjId is directly translated to calendar item UID.

Upon close examination, we can see the 37th byte is really the length of the
actual ID part that starts at byte 41.  Well, for external UIDs the actual UID
actually starts at byte 53 because the "vCal-Uid" prefix starts at byte 41.
In any case, the length given at byte 37 does include the "vCal-Uid" part and
all the way to the terminating 00 after the actual UID.  If we look at the
native UID, we can see that the corresponding 37th byte is also the length
from byte 41 to the end.  However because for native UIDs the last part is
of fixed length, byte 37 is always 10 (16 in dec).

So what does it mean to us?  Well, when generating a GlobalObjId we need to first
check the UID of an appointment to see if it's of native or external format.
If the UID starts with the magic prefix of 040000008200E00074C5B7101A82E008,
we should encode it directly into GlobalObjId.  If on the other hand the UID
does conform to the native format, we will encode using the external form,
with a "vCal-Uid" label and a terminating 00 byte.  When receiving TNEF or
text/calendar replies we should check to see whether the UID conforms to
native or external format.  If external format, we can find the actual UID
between byte 53 and byte second to last 00.

If however the sync client composes a new meeting, we'll simply take the whole
UID coming from client as actual UID.

Sample raw UID hex sequence, first two created within exchange and the 3rd from
external:

040000008200E00074C5B7101A82E0080000000000E2071470C4C601000000000000000010000000974D2325BAEA914F9E0C97EC5173BF48
040000008200E00074C5B7101A82E0080000000050ED900D74C4C60100000000000000001000000067C578584FFD084BA806F67A02B86005
040000008200E00074C5B7101A82E00800000000000000000000000000000000000000001E0000007643616C2D55696401000000666F6F626172313233406162632E636F6D00

----
TNEF response carries a Message-ID header in the top level MIME which matches
the original invitation's ServerId.  However there's no such thing in the
treo650's vCal response.  Therefore we must find a way to match an encoded
client UID with not only server UID but also at least a recurrend ID for
exception invitations.

After closely examine the format of GlobalObjId again, I figured out a little
more of the magic.  Consider a simple meeting with a start time of 
2006-08-22T17:00:00.000Z.  Its GlobalObjId is:

BAAAAIIA4AB0xbcQGoLgCAAAAAAA4gcUcMTGAQAAAAAAAAAAEAAAAJdNIyW66pFPngyX7FFzv0g=

The hex sequence is:

040000008200E00074C5B7101A82E008  00000000  00E2071470  C4C601000000000000000010000000  974D2325BAEA914F9E0C97EC5173BF48

Now consider a weekly recurrent meeting on TU/TH that starts on 
2006-08-24T16:00:00.000Z and runs for 10 occurrences.  It has a GlobalObjId
like this:

BAAAAIIA4AB0xbcQGoLgCAAAAABQ7ZANdMTGAQAAAAAAAAAAEAAAAGfFeFhP/QhLqAb2egK4YAU=

And let's look at the GlobalObjIds of 3 exceptions:

BAAAAIIA4AB0xbcQGoLgCAfWCB9Q7ZANdMTGAQAAAAAAAAAAEAAAAGfFeFhP/QhLqAb2egK4YAU= (RecurrenceId=2006-08-31T16:00:00.000Z)
BAAAAIIA4AB0xbcQGoLgCAfWCQVQ7ZANdMTGAQAAAAAAAAAAEAAAAGfFeFhP/QhLqAb2egK4YAU= (RecurrenceId=2006-09-05T16:00:00.000Z)
BAAAAIIA4AB0xbcQGoLgCAfWCQdQ7ZANdMTGAQAAAAAAAAAAEAAAAGfFeFhP/QhLqAb2egK4YAU= (RecurrenceId=2006-09-07T16:00:00.000Z)

Here are the hex sequences:

040000008200E00074C5B7101A82E008  00000000  50ED900D74  C4C601000000000000000010000000  67C578584FFD084BA806F67A02B86005
040000008200E00074C5B7101A82E008  07D6081F  50ED900D74  C4C601000000000000000010000000  67C578584FFD084BA806F67A02B86005
040000008200E00074C5B7101A82E008  07D60905  50ED900D74  C4C601000000000000000010000000  67C578584FFD084BA806F67A02B86005
040000008200E00074C5B7101A82E008  07D60907  50ED900D74  C4C601000000000000000010000000  67C578584FFD084BA806F67A02B86005

We can see that the 4 bytes from #17 to #20 varies from one exception to
another, but the main invitation's same part is 00000000.  Also the
corresponding 4 bytes in the simple meeting's invitation is 00000000.

If you try a little harder it's easy to see that 07D6 is 2006, 081F is 8/31,
0905 is 9/5 and 0907 is 9/7.  In other words these 4 bytes matches perfectly
the date of RecurrenceId.

The 5 bytes right after, #21 to #25, remain the same within the same meeting
but varies from one meeting to another.  I have no clue how to interpret these
5 bytes.

Finally as a reference, compare the above with external form:

040000008200E00074C5B7101A82E008  00000000  0000000000  000000000000000000000033000000  7643616C2D556964010000007B41414638314143462D374433392D343142352D423230462D3541354144423534424131467D00 (main)
040000008200E00074C5B7101A82E008  07D50B19  0000000000  000000000000000000000033000000  7643616C2D556964010000007B41414638314143462D374433392D343142352D423230462D3541354144423534424131467D00 (RecurrenceId=2005-11-25T13:30:00.000Z)

So what can we do?  Maybe we can put the date of RecurrenceId in the 4 bytes
from #17 to #20 even in the "vCal-Uid" prefix.  Date alone is not enough as
RecurrenceId in the general case, but usually that works fine.

We still have no clue about the bytes from 21 to 28.
*/

public abstract class CalendarUID {
	//1                               17     20                               37      41                      53 
	//|                               |      |                                |       |                       |
	//040000008200E00074C5B7101A82E0080000000000000000000000000000000000000000000000007643616C2D55696401000000
	final private static byte[] GOID_PREFIX = {
		(byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x82, (byte)0x00, (byte)0xE0, (byte)0x00,
		(byte)0x74, (byte)0xC5, (byte)0xB7, (byte)0x10, (byte)0x1A, (byte)0x82, (byte)0xE0, (byte)0x08,
		(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x76, (byte)0x43, (byte)0x61, (byte)0x6C, (byte)0x2D, (byte)0x55, (byte)0x69, (byte)0x64,
		(byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00
	};

	final private static int CONSTANT_PREFIX_LENGTH = 16;
	
	final private static int RECURRENCE_ID_START = 16;
	
	final private static int UID_LENGTH_BYTE = 36;
	
	final private static int VCAL_UID_LABEL_START = 40;
	final private static int VCAL_UID_LABEL_LENGTH = 12;
	
	public static class InviteStub {
		String uid;
		int year;
		int month;
		int date;
		
		public InviteStub(String uid) {
			this.uid = uid;
		}
		
		public String getUid() {
		    return uid;
		}
		
		public boolean isException() {
			return year > 0 && month > 0 && date > 0;
		}
		
		public boolean isEquivRecurrenceId(long recurrenceId) {
	    	Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"), Locale.US);
	    	cal.setTime(new Date(recurrenceId));
	    	if (cal.get(Calendar.YEAR) == year &&
	    			cal.get(Calendar.MONTH) + 1 == month &&
	    			cal.get(Calendar.DATE) == date) {
	    		return true;
	    	}
			return false;
		}
		
		public boolean isEquivRecurrenceId(long recurrenceId, TimeZone tz) {
            Calendar cal = new GregorianCalendar(tz, Locale.US);
            cal.setTime(new Date(recurrenceId));
            if (cal.get(Calendar.YEAR) == year &&
                    cal.get(Calendar.MONTH) + 1 == month &&
                    cal.get(Calendar.DATE) == date) {
                return true;
            }
            return false;
        }
		
	}
	
	public static String uidToGlobalObjId(String uid, long recurrenceId) {
		//If a UID starts with a prefix of 040000008200E00074C5B7101A82E008, it's a native UID generated by Outlook or
		//WM client.  We will directly use that to encode GlobalObjId.
		//
		//If a UID doesn't follow that format then it an external UID.  In that case we will use the special prefix with
		//"vCal-Uid" label.
		//
		//Note: We were treating all UIDs as external UIDs and always use the "vCal-Uid" label to encode, bug bug#13174
		//made it clear that doesn't work.
		
		byte[] bytes = null;
		
		if (uid.startsWith("040000008200E00074C5B7101A82E008")) {
			try {
				bytes = hexSeqStrToBytes(uid);
			} catch (Exception t) {} //if we fail we fallback to treat it as external UID
		}
		
		if (bytes == null) {
			bytes = new byte[GOID_PREFIX.length + uid.length() + 1];
			System.arraycopy(GOID_PREFIX, 0, bytes, 0, GOID_PREFIX.length);
			if (recurrenceId > 0) {
		    	Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"), Locale.US);
		    	cal.setTime(new Date(recurrenceId));
		    	int year = cal.get(Calendar.YEAR);
		    	int month = cal.get(Calendar.MONTH) + 1;
		    	int date = cal.get(Calendar.DATE);
		    	
		    	bytes[RECURRENCE_ID_START] = (byte)((year >> 8) & 0xFF);
		    	bytes[RECURRENCE_ID_START + 1] = (byte)(year & 0xFF);
		    	bytes[RECURRENCE_ID_START + 2] = (byte)month;
		    	bytes[RECURRENCE_ID_START + 3] = (byte)date;
			}
			bytes[UID_LENGTH_BYTE] = (byte)(VCAL_UID_LABEL_LENGTH + uid.length() + 1);
			System.arraycopy(uid.getBytes(), 0, bytes, GOID_PREFIX.length, uid.length());
			bytes[bytes.length - 1] = 0x00;
		}
		
		return new String(Base64.encodeBase64(bytes));
	}

	public static InviteStub makeInviteIdFromGlobalObjId(String goidHexStr) {
		try {
			byte[] goid = hexSeqStrToBytes(goidHexStr);
			if (goid != null) {
				return makeInviteIdFromGlobalObjId(goid);
			}
		} catch (Exception t) {}
		
		//We assume the type of incoming GlobalObjId always matches the format of
		//040000008200E00074C5B7101A82E008..., either responses to invitations
		//generated by ZS (with vCal-Uid label) or requests generated by WM clients.
		ZimbraLog.sync.warn("Invalid GlobalObjId: " + goidHexStr);
		return null; 
	}
	
	public static InviteStub makeInviteIdFromGlobalObjId(byte[] goid) {
		if (goid.length <= VCAL_UID_LABEL_START ||
				!equals(goid, 0, GOID_PREFIX, 0, CONSTANT_PREFIX_LENGTH)) {
			return null;
		}
		
		String uid = null;
		if (equals(goid, VCAL_UID_LABEL_START, GOID_PREFIX, VCAL_UID_LABEL_START, VCAL_UID_LABEL_LENGTH)) {
			uid = new String(goid, GOID_PREFIX.length, goid.length - GOID_PREFIX.length - 1); //rid of ending 0x00
		} else {
			uid = bytesToHexSeqStr(goid);
		}
		
		InviteStub invStub = new InviteStub(uid);
		invStub.year = ((goid[RECURRENCE_ID_START] & 0xFF) << 8) + (goid[RECURRENCE_ID_START + 1] & 0xFF);
		invStub.month = goid[RECURRENCE_ID_START + 2] & 0xFF;
		invStub.date = goid[RECURRENCE_ID_START + 3] & 0xFF;

		return invStub;
	}
	
	public static InviteStub makeInviteIdFromExceptionGlobalObjId(byte[] goid) {
	    if (goid.length <= VCAL_UID_LABEL_START ||
	            !equals(goid, 0, GOID_PREFIX, 0, CONSTANT_PREFIX_LENGTH)) {
	        return null;
	    }
	        
	    String uid = null;
	    if (equals(goid, VCAL_UID_LABEL_START, GOID_PREFIX, VCAL_UID_LABEL_START, VCAL_UID_LABEL_LENGTH)) {
	        uid = new String(goid, GOID_PREFIX.length, goid.length - GOID_PREFIX.length - 1); //rid of ending 0x00
	    } else {
	        uid = bytesToHexSeqStr(goid);
	    }
	        
	    int year = ((goid[RECURRENCE_ID_START] & 0xFF) << 8) + (goid[RECURRENCE_ID_START + 1] & 0xFF);
	    int month = goid[RECURRENCE_ID_START + 2] & 0xFF;
	    int date = goid[RECURRENCE_ID_START + 3] & 0xFF;
	        
	    // in case of exception, the UID in native format contains the recurrenceId which needs to be
	    // stripped off from the original uid.
	    if (uid.startsWith("040000008200E00074C5B7101A82E008") && year > 0 && month > 0 && date > 0) {
	        StringBuilder temp = new StringBuilder();
	        temp.append("040000008200E00074C5B7101A82E008");
	        temp.append("00000000"); // replace recurrence id with all 0s
	        temp.append(uid.substring(32 + 8)); // length of the prefix and recurrenId
	        uid = temp.toString();
	    }
	        
	    InviteStub invStub = new InviteStub(uid);
	    invStub.year = year;
	    invStub.month = month;
	    invStub.date = date;

	    return invStub;
	}
	
	static String bytesToHexSeqStr(byte[] bytes) {
		final String table = "0123456789ABCDEF";
		
		StringBuilder sb = new StringBuilder(bytes.length*2);
		for (byte b : bytes) {
			int hi = (b >> 4) & 0x0F;
			int low = b & 0x0F;
			sb.append(table.charAt(hi));
			sb.append(table.charAt(low));
		}
		return sb.toString();
	}

	static byte[] hexSeqStrToBytes(String hexSeqStr) {
		byte[] hexSeq = hexSeqStr.getBytes();
		if (hexSeq.length / 2 * 2 != hexSeq.length) {
			ZimbraLog.sync.warn("HEX sequence of odd length: " + hexSeqStr);
			return null;
		}
		
		byte[] bytes = new byte[hexSeq.length/2];
		for (int i = 0; i < bytes.length; ++i) {
			bytes[i] = (byte)((parseByteHex(hexSeq[i*2]) << 4) + parseByteHex(hexSeq[i*2+1]));
		}
		
		return bytes;
	}
	
	static boolean equals(byte[] a, int offsetA, byte[] b, int offsetB, int length) {
		for (int i = 0; i < length; ++i) {
			if (a[offsetA + i] != b[offsetB + i]) {
				return false;
			}
		}
		return true;
	}
	
	static byte parseByteHex(byte hex) {
		switch (hex) {
		case '0': return 0;
		case '1': return 1;
		case '2': return 2;
		case '3': return 3;		
		case '4': return 4;
		case '5': return 5;		
		case '6': return 6;
		case '7': return 7;		
		case '8': return 8;
		case '9': return 9;		
		case 'a': case 'A': return 10;
		case 'b': case 'B': return 11;
		case 'c': case 'C': return 12;
		case 'd': case 'D': return 13;
		case 'e': case 'E': return 14;
		case 'f': case 'F': return 15;
		}
		
		throw new RuntimeException("Not a hex character: " + hex);
	}
}
