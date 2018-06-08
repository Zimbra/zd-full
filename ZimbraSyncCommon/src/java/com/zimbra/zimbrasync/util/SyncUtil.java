/*
 * 
 */
package com.zimbra.zimbrasync.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import net.freeutils.tnef.TNEFUtils;

import org.apache.commons.codec.binary.Base64;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.mailbox.calendar.ICalTimeZone;
import com.zimbra.cs.mailbox.calendar.Invite;
import com.zimbra.cs.mailbox.calendar.ParsedDateTime;
import com.zimbra.cs.mailbox.calendar.Recurrence;
import com.zimbra.cs.mailbox.calendar.Recurrence.IRecurrence;
import com.zimbra.cs.mailbox.calendar.TimeZoneMap;
import com.zimbra.cs.mailbox.calendar.WindowsSystemTime;
import com.zimbra.cs.mailbox.calendar.WindowsTimeZoneInformation;
import com.zimbra.cs.mailbox.calendar.ZRecur;
import com.zimbra.zimbrasync.data.SyncServiceException;
import com.zimbra.zimbrasync.wbxml.BinaryCodec;

public abstract class SyncUtil extends BinaryCodec {

    public static String decodeCompressedRtf(String input) {
        try {
            byte[] rtfz = Base64.decodeBase64(input.getBytes());
            byte[] rtf = TNEFUtils.decompressRTF(rtfz);
            InputStream in = new ByteArrayInputStream(rtf);
            DefaultStyledDocument doc = new DefaultStyledDocument();
            RTFEditorKit kit = new RTFEditorKit();
            kit.read(in, doc, 0);
            return doc.getText(0, doc.getLength());
        } catch(Exception x) {
            ZimbraLog.sync.warn("Can't decode compressed RTF [" + input + "]");
            return input;
        }
    }

    //not really standard RTF, but some kind of TNEF related thingy
    public static String textToRtf(String text) {
        try {
            DefaultStyledDocument doc = new DefaultStyledDocument();
            doc.insertString(0, text, null);
            RTFEditorKit kit = new RTFEditorKit();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            kit.write(out, doc, 0, doc.getLength());

            //	The PR_RTF_COMPRESSED data is organized according to the definitions in RTFLIB.H:
            //
            //	typedef struct _lzfuhdr
            //	{
            //	 ULONG cbSize;  // total number of bytes following this field
            //	 ULONG cbRawSize; // size before compression
            //	 DWORD dwMagic;  // identifies this as a compressed stream
            //	 DWORD dwCRC;  // CRC-32 of the compressed data for error checking
            //	} LZFUHDR;
            //
            //	#define dwMagicCompressedRTF 0x75465a4c
            //	#define dwMagicUncompressedRTF 0x414c454d
            byte[] raw = out.toByteArray();
            byte[] bytes = new byte[16 + raw.length]; //16 is the magic header
            writeIntToLeBytes(bytes, 0, bytes.length - 4);
            writeIntToLeBytes(bytes, 4, raw.length);
            writeIntToLeBytes(bytes, 8, 0x414c454d);
            writeIntToLeBytes(bytes, 12, TNEFUtils.calculateCRC32(raw, 0, raw.length));
            System.arraycopy(raw, 0, bytes, 16, out.size());

            return new String(Base64.encodeBase64(bytes));
        } catch (Exception x) {
            ZimbraLog.sync.warn("Can't encode RTF [" + text + "]");
            return text;
        }
    }

    public static String md5Digest(String input) throws NoSuchAlgorithmException {
        MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(input.getBytes());
        byte digest[] = algorithm.digest();

        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < digest.length; ++i) {
            String hex = Integer.toHexString(0xFF & digest[i]);
            if (hex.length() == 1) {
                buf.append('0');
            }
            buf.append(hex);
        }

        return buf.toString();
    }

    public static String getFormattedUtcDateTime(long time) {
        return getFormattedLocalDateTime(time, TimeZone.getTimeZone("GMT"), false) + "Z";
    }

    public static String getFormattedUtcDateTime(long time, boolean useDelim) {
        return getFormattedLocalDateTime(time, TimeZone.getTimeZone("GMT"), useDelim) + "Z";
    }

    public static String getFormattedLocalDateTime(long time, TimeZone tz, boolean useDelim) {
        Calendar cal = new GregorianCalendar(tz, Locale.US);
        cal.setTime(new Date(time));

        DecimalFormat fourDigitFormat = new DecimalFormat("0000");
        DecimalFormat threeDigitFormat = new DecimalFormat("000");
        DecimalFormat twoDigitFormat = new DecimalFormat("00");

        StringBuilder buf = new StringBuilder();

        buf.append(fourDigitFormat.format(cal.get(Calendar.YEAR)));
        if (useDelim) buf.append('-');
        buf.append(twoDigitFormat.format(cal.get(Calendar.MONTH) + 1));
        if (useDelim) buf.append('-');
        buf.append(twoDigitFormat.format(cal.get(Calendar.DATE)));

        buf.append("T");

        buf.append(twoDigitFormat.format(cal.get(Calendar.HOUR_OF_DAY)));
        if (useDelim) buf.append(':');
        buf.append(twoDigitFormat.format(cal.get(Calendar.MINUTE)));
        if (useDelim) buf.append(':');
        buf.append(twoDigitFormat.format(cal.get(Calendar.SECOND)));
        if (useDelim) {
            buf.append('.');
            buf.append(threeDigitFormat.format(cal.get(Calendar.MILLISECOND)));
        }

        return buf.toString();
    }

    public static String getFormattedLocalDate(long time, TimeZone tz) {
        Calendar cal = new GregorianCalendar(tz, Locale.US);
        cal.setTime(new Date(time));

        DecimalFormat fourDigitFormat = new DecimalFormat("0000");
        DecimalFormat twoDigitFormat = new DecimalFormat("00");

        StringBuilder buf = new StringBuilder();

        buf.append(fourDigitFormat.format(cal.get(Calendar.YEAR)));
        buf.append(twoDigitFormat.format(cal.get(Calendar.MONTH) + 1));
        buf.append(twoDigitFormat.format(cal.get(Calendar.DATE)));

        return buf.toString();
    }

    public static ParsedDateTime localDateTimeFromUtcString(String utcStr, ICalTimeZone tz, TimeZoneMap tzMap)
        throws ParseException {

        //I'm sure there's a better way to do this
        ParsedDateTime utc = ParsedDateTime.parse(utcStr, tzMap, ICalTimeZone.getUTC(), tz);
        //long local = utc.getUtcTime() + tz.getOffset(utc.getUtcTime());
        String localStr = getFormattedLocalDateTime(utc.getUtcTime(), tz, false);
        return ParsedDateTime.parse(localStr, tzMap, tz, tz);
    }

    //Converting UTC time of format of 1980-12-21T08:00:00.000Z to local date in of format of 1980-12-21
    public static String formattedUtcToLocalDate(String formattedTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'z");
        Date date = sdf.parse(formattedTime + "GMT");
        sdf.applyPattern("yyyy-MM-dd");
        return sdf.format(date);
    }
    
  //Converting UTC time of format of 1980-12-21T08:00:00.000Z to local date
    public static Date formattedUtcToDate(String formattedTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'z");
        Date date = sdf.parse(formattedTime + "GMT");
        return date;
    }

    //Converting local date in of format of 1980-12-21 to UTC time of format of 1980-12-21T08:00:00.000Z
    public static String localDateToFormattedUtc(String localDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(localDate);
        return getFormattedUtcDateTime(date.getTime(), true);
    }

    //    typedef struct _TIME_ZONE_INFORMATION {
    //    	  LONG Bias;				//4
    //    	  WCHAR StandardName[32];	//64
    //    	  SYSTEMTIME StandardDate;	//16
    //    	  LONG StandardBias;		//4
    //    	  WCHAR DaylightName[32];	//64
    //    	  SYSTEMTIME DaylightDate;	//16
    //    	  LONG DaylightBias;		//4
    //    	} TIME_ZONE_INFORMATION

    //    typedef struct _SYSTEMTIME {
    //    	  WORD wYear;
    //    	  WORD wMonth;
    //    	  WORD wDayOfWeek;
    //    	  WORD wDay;
    //    	  WORD wHour;
    //    	  WORD wMinute;
    //    	  WORD wSecond;
    //    	  WORD wMilliseconds;
    //    	} SYSTEMTIME
    public static String encodeTimezoneInformation(ICalTimeZone tz) {

        WindowsTimeZoneInformation wtzi =
            WindowsTimeZoneInformation.fromICal(tz);

        byte[] tziBytes = new byte[172];

        writeIntToLeBytes(tziBytes, 0, (int)wtzi.getBiasMins());

        if (tz.useDaylightTime()) {
            assert wtzi.getStandardDate() != null;
            encodeOnsetSystemTime(wtzi.getStandardDate(), tziBytes, 68);
            writeIntToLeBytes(tziBytes, 84, wtzi.getStandardBiasMins()); //this usually has zero effect

            assert wtzi.getDaylightDate() != null;
            encodeOnsetSystemTime(wtzi.getDaylightDate(), tziBytes, 152);
            writeIntToLeBytes(tziBytes, 168, wtzi.getDaylightBiasMins());
        } else {
            assert wtzi.getStandardDate() == null;
            assert wtzi.getDaylightDate() == null;
        }

        return new String(Base64.encodeBase64(tziBytes));
    }

    private static void encodeOnsetSystemTime(WindowsSystemTime wst, byte[] bytes, int offset) {
        writeIntToLeBytes(bytes, offset +  0, wst.getYear());
        writeIntToLeBytes(bytes, offset +  2, wst.getMonth());
        writeIntToLeBytes(bytes, offset +  4, wst.getDayOfWeek());
        writeIntToLeBytes(bytes, offset +  6, wst.getDay());
        writeIntToLeBytes(bytes, offset +  8, wst.getHour());
        writeIntToLeBytes(bytes, offset + 10, wst.getMinute());
        writeIntToLeBytes(bytes, offset + 12, wst.getSecond());
        writeIntToLeBytes(bytes, offset + 14, wst.getMilliseconds());
    }

    /**
     * @param tzi raw String from device
     * @return decoded ICalTimeZone 
     * @throws SyncServiceException If there is unexpected data. Under any circumstance this exception should be resolved 
     * as we can always use default time zone from account attribute "zimbraPrefTimeZoneId".
     */
    public static ICalTimeZone decodeTimezoneInformation(String tzi) throws SyncServiceException {

        byte[] tziBytes = Base64.decodeBase64(tzi.getBytes());

        int bias = readLeBytesToInt(tziBytes, 0);

        WindowsSystemTime standardDate = null;
        int standardBias = 0;
        WindowsSystemTime daylightDate = null;
        int daylightBias = 0;

        if (tziBytes[70] != 0) { //use DST
            int standardYear         = tziBytes[68];
            int standardMonth        = tziBytes[70]; //1 means Jan
            int standardDayOfWeek    = tziBytes[72]; //0 means Sunday
            int standardDay          = tziBytes[74]; //could double to mean week number from 1 to 5
            int standardHour         = tziBytes[76]; //0 means midnight
            int standardMinute       = tziBytes[78];
            int standardSecond       = tziBytes[80];
            int standardMilliSeconds = tziBytes[82];

            standardDate = new WindowsSystemTime(standardYear, standardMonth, standardDayOfWeek, standardDay,
                standardHour, standardMinute, standardSecond, standardMilliSeconds);

            //this is the modifier of bias during standard period,
            //which is zero in most timezones
            standardBias = readLeBytesToInt(tziBytes, 84);

            if (tziBytes[154] == 0) {
                throw SyncServiceException.UNEXPECTED_DATA("daylightMonth should not be 0. Jan is 1.");
            }

            int daylightYear         = tziBytes[152];
            int daylightMonth        = tziBytes[154]; //1 means Jan
            int daylightDayOfWeek    = tziBytes[156]; //0 means Sunday
            int daylightDay          = tziBytes[158]; //could double to mean week number from 1 to 5
            int daylightHour         = tziBytes[160]; //0 means midnight
            int daylightMinute       = tziBytes[162];
            int daylightSecond       = tziBytes[164];
            int daylightMilliSeconds = tziBytes[166];

            daylightDate = new WindowsSystemTime(daylightYear, daylightMonth, daylightDayOfWeek, daylightDay,
                daylightHour, daylightMinute, daylightSecond, daylightMilliSeconds);

            //this is the modifier of bias during standard period,
            //which is -60 in most timezones
            daylightBias = readLeBytesToInt(tziBytes, 168);
            if (daylightBias == 0) {
                //if it's 0, most of the fields of the returning ICalTimeZone obj will be 0/null, we'd be better off
                //to use account's default time zone.
                throw SyncServiceException.UNEXPECTED_DATA("daylightBias should not be 0");
            }
        }

        StringBuilder tzName = new StringBuilder();
        tzName.append("GMT");

        //Make a presentable name for the timezone
        int standardOffset = -(bias + standardBias);
        int standardHourOffset = Math.abs(standardOffset) / 60;
        int standardMinuteOffset = Math.abs(standardOffset) % 60;

        if (standardOffset != 0) {
            tzName.append(standardOffset < 0 ? "-" : "+");

            if (standardHourOffset < 10) {
                tzName.append("0");
            }
            tzName.append(standardHourOffset);
            tzName.append(".");
            if (standardMinuteOffset < 10) {
                tzName.append("0");
            }
            tzName.append(standardMinuteOffset);
        }

        int daylightOffset = -(bias + daylightBias);
        int daylightHourOffset = Math.abs(daylightOffset) / 60;
        int daylightMinuteOffset = Math.abs(daylightOffset) % 60;

        if (daylightOffset != standardOffset) {
            tzName.append("/");
            tzName.append(daylightOffset < 0 ? "-" : "+");

            if (daylightHourOffset < 10) {
                tzName.append("0");
            }
            tzName.append(daylightHourOffset);
            tzName.append(".");
            if (daylightMinuteOffset < 10) {
                tzName.append("0");
            }
            tzName.append(daylightMinuteOffset);
        }

        WindowsTimeZoneInformation wtzi = new WindowsTimeZoneInformation(tzName.toString(), bias, standardDate, standardBias, null,
            daylightDate, daylightBias, null);
        return wtzi.toICal();
    }

    private static int readLeBytesToInt(byte[] bytes, int offset) {
        return (bytes[offset+3] << 24)
            + ((bytes[offset+2] << 24) >>> 8)
            + ((bytes[offset+1] << 24) >>> 16)
            + ((bytes[offset] << 24) >>> 24);
    }

    private static void writeIntToLeBytes(byte[] bytes, int offset, int value) {
        bytes[offset] = (byte)(value & 0xFF);
        bytes[offset+1] = (byte)((value >>> 8) & 0xFF);
        bytes[offset+2] = (byte)((value >>> 16) & 0xFF);
        bytes[offset+3] = (byte)((value >>> 24) & 0xFF);
    }

    public static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        int b = 0;
        while ((b = in.read()) != -1) {
            bao.write(b);
        }
        return bao.toByteArray();
    }

    public static int[] integerListToArray(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static int weekOfMonth(ZRecur recur) {
        List<ZRecur.ZWeekDayNum> weekdayList = recur.getByDayList();
        int offset = weekdayList.get(0).mOrdinal;
        if (offset == 0) {
            List<Integer> setposList = recur.getBySetPosList();
            if (setposList != null && !setposList.isEmpty()) {
                assert setposList.size() == 1;
                offset = setposList.get(0).intValue();
            }
        }
        switch (offset) {
        case -1: case 1: case 2: case 3: case 4:
            break;
        default:
            ZimbraLog.sync.warn("Invalid RECUR pattern: " + recur.toString());
            break;
        }
        return offset;
    }

    public static ZRecur getRecur(Invite invite) {
        ZRecur zr = null;

        Recurrence.IRecurrence r = invite.getRecurrence();
        if (r != null) {
            //The Appointment object's IRecurrence is always a RecurrenceRule,
            //which is a container of everything.
            assert r.getType() == Recurrence.TYPE_RECURRENCE;
            Recurrence.RecurrenceRule masterRule = (Recurrence.RecurrenceRule)r;

            //We then go through the RRULE, RDATE, EXRULE and EXDATE.
            //We only support a single RRULE.

            for (Iterator<IRecurrence> iter = masterRule.addRulesIterator(); iter!=null && iter.hasNext();) {
                r = iter.next();

                switch (r.getType()) {
                case Recurrence.TYPE_SINGLE_DATES:
                    //Recurrence.SingleInstanceRule sir = (Recurrence.SingleInstanceRule)r;
                    assert false; //We don't support single instance.
                    break;
                case Recurrence.TYPE_REPEATING:
                    Recurrence.SimpleRepeatingRule srr = (Recurrence.SimpleRepeatingRule)r;
                    zr = srr.getRule();
                    break;
                default:
                    assert false; //can't be anything else
                    break;
                }

                assert !iter.hasNext(); //we don't support more than one RRULE/RDATE
                break;
            }
            assert zr != null;
        }

        return zr;
    }

    public static String getNotesFromTextPart(String description) {
        if (description != null && description.length() > 0) {
            String separator = "*~*~*~*~*~*~*~*~*~*";
            int pos = description.indexOf(separator);
            if (pos >= 0) {
                description = description.substring(pos + separator.length());
            }
            description = description.trim();
        }
        return description;
    }
}
