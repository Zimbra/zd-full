/*
 * 
 */
package com.zimbra.zimbrasync.util;

import com.zimbra.cs.mailbox.calendar.ICalTimeZone;
import com.zimbra.cs.mailbox.calendar.ParsedDateTime;
import com.zimbra.cs.mailbox.calendar.TimeZoneMap;

import junit.framework.TestCase;

public class TestSyncUtil extends TestCase {

    public void testDecodeCompressedRtf() {
        String rtf = "IwEAAKMDAABMWkZ1Pl/HSj8ACQMwAQMB9wKnAgBjaBEKwHNldALRcHJx4DAgVGFoA3ECgwBQ6wNUDzcyD9MyBgAGwwKDpxIBA+MReDA0EhUgAoArApEI5jsJbzAVwzEyvjgJtBdCCjIXQRb0ORIAHxeEGOEYExjgFcMyNTX/CbQaYgoyGmEaHBaKCaUa9v8c6woUG3YdTRt/Hwwabxbt/xyPF7gePxg4JY0YVyRMKR+dJfh9CoEBMAOyMTYDMcksgSc3FGAnNhdgLZD6OS1BMy1ADAEt3ADgLdPcNmUtgRqALlMzAFAuUf8KhQqBLV8uby9/MIESADDf/zHvMv8vvjbTNV82bzd/L+v/OmE5jzqfO68vzTAzPf8/Dy9AHy/oQkcKhX1HIA==";
        String text = SyncUtil.decodeCompressedRtf(rtf);
        System.out.println(text);
    }

    public void testTextToRtf() {
        String rtf = SyncUtil.textToRtf("foo\nbar\n");
        System.out.println(rtf);
    }

    public void test() throws Exception {

        ICalTimeZone pst = ICalTimeZone.lookup("TestPST", -28800000, "16010101T020000", "FREQ=YEARLY;WKST=SU;INTERVAL=1;BYMONTH=10;BYDAY=-1SU", "PST",
            -25200000, "16010101T020000", "FREQ=YEARLY;WKST=SU;INTERVAL=1;BYMONTH=4;BYDAY=1SU", "PDT");
        String tzi = SyncUtil.encodeTimezoneInformation(pst);
        assertTrue(tzi.equals("4AEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAAFAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAABAAIAAAAAAAAAxP///w=="));

        ICalTimeZone tz = SyncUtil.decodeTimezoneInformation(tzi);
        assertTrue(tz.getDisplayName().equals("GMT-08.00/-07.00"));
    }

    public void testTimeZone() throws Exception {
        //this is GMT+5.30
        String tzStr = "tv7//wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";
        ICalTimeZone tz = SyncUtil.decodeTimezoneInformation(tzStr);
        ICalTimeZone local = ICalTimeZone.lookup("TestPST", -28800000, "16010101T020000", "FREQ=YEARLY;WKST=SU;INTERVAL=1;BYMONTH=10;BYDAY=-1SU", "PST",
            -25200000, "16010101T020000", "FREQ=YEARLY;WKST=SU;INTERVAL=1;BYMONTH=4;BYDAY=1SU", "PDT");
        ParsedDateTime pdt = ParsedDateTime.parse("20061221T183000Z", new TimeZoneMap(local), tz, local);
        String time = SyncUtil.getFormattedLocalDateTime(pdt.getUtcTime(), tz, false);
        System.out.println(time);
        assertTrue(time.equals("20061222T000000"));
    }

    public void testMd5Digest() throws Exception {

        String digest = SyncUtil.md5Digest("FOO");
        assertTrue(digest.equals("901890a8e9c8cf6d5a1a542b229febff"));
    }

    public static void main(String[] args) throws Exception {
        String tzStr = "iP///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAAFAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAFAAMAAAAAAAAAxP///w==";
        ICalTimeZone tz = SyncUtil.decodeTimezoneInformation(tzStr);
        System.out.println(tz);
    }
}
