/*
 * 
 */
package com.zimbra.zimbrasync.data;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains EAS InternetCPID mapping for the supported character encodings
 * as per .NET framework 4 text encoding;
 * Ref: http://msdn.microsoft.com/en-us/library/system.text.encoding.aspx 
 * 
 * @author smukhopadhyay
 *
 */
public class InternetCPID {
    
    private static Map<String, Integer> cpidMapping;
    private static Map<String, Integer> charset2CpidMapping;
    
    static {
        cpidMapping = new HashMap<String, Integer>();
        charset2CpidMapping = new HashMap<String, Integer>();
        
        cpidMapping.put("IBM037", 37);
        cpidMapping.put("IBM437", 437);
        cpidMapping.put("IBM500", 500);
        cpidMapping.put("ASMO-708", 708); //Not supported; Represented by ISO-8859-6
        cpidMapping.put("DOS-720", 720);  //Not supported;
        cpidMapping.put("IBM737", 737);
        cpidMapping.put("IBM775", 775);
        cpidMapping.put("IBM850", 850);
        cpidMapping.put("IBM852", 852);
        cpidMapping.put("IBM855", 855);
        cpidMapping.put("IBM857", 857);
        cpidMapping.put("IBM00858", 858);
        cpidMapping.put("IBM860", 860);
        cpidMapping.put("IBM861", 861);
        cpidMapping.put("dos-862", 862); //Not supported;
        cpidMapping.put("IBM863", 863);
        cpidMapping.put("IBM864", 864);
        cpidMapping.put("IBM865", 865);
        cpidMapping.put("cp866", 866);
        cpidMapping.put("IBM869", 869);
        cpidMapping.put("IBM870", 870);
        cpidMapping.put("windows-874", 874);
        cpidMapping.put("cp875", 875);
        cpidMapping.put("shift_jis", 932);
        cpidMapping.put("gb2312", 936);
        cpidMapping.put("ks_c_5601-1987", 949);
        cpidMapping.put("Big5", 950);
        cpidMapping.put("IBM1026", 1026);
        cpidMapping.put("IBM01047", 1047);
        cpidMapping.put("IBM01140", 1140);
        cpidMapping.put("IBM01141", 1141);
        cpidMapping.put("IBM01142", 1142);
        cpidMapping.put("IBM01143", 1143);
        cpidMapping.put("IBM01144", 1144);
        cpidMapping.put("IBM01145", 1145);
        cpidMapping.put("IBM01146", 1146);
        cpidMapping.put("IBM01147", 1147);
        cpidMapping.put("IBM01148", 1148);
        cpidMapping.put("IBM01149", 1149);
        cpidMapping.put("UTF-16", 1200);
        cpidMapping.put("unicodeFFFE", 1201); //Not supported;
        cpidMapping.put("windows-1250", 1250);
        cpidMapping.put("windows-1251", 1251);
        cpidMapping.put("windows-1252", 1252);
        cpidMapping.put("windows-1253", 1253);
        cpidMapping.put("windows-1254", 1254);
        cpidMapping.put("windows-1255", 1255);
        cpidMapping.put("windows-1256", 1256);
        cpidMapping.put("windows-1257", 1257);
        cpidMapping.put("windows-1258", 1258);
        cpidMapping.put("johab", 1361);
        cpidMapping.put("macintosh", 10000);
        cpidMapping.put("x-mac-japanese", 10001); //Not supported;
        cpidMapping.put("x-mac-chinesetrad", 10002); //Not supported;
        cpidMapping.put("x-mac-korean", 10003); //Not supported;
        cpidMapping.put("x-mac-arabic", 10004);
        cpidMapping.put("x-mac-hebrew", 10005);
        cpidMapping.put("x-mac-greek", 10006);
        cpidMapping.put("x-mac-cyrillic", 10007);
        cpidMapping.put("x-mac-chinesesimp", 10008); //Not supported;
        cpidMapping.put("x-mac-romanian", 10010);
        cpidMapping.put("x-mac-ukrainian", 10017);
        cpidMapping.put("x-mac-thai", 10021);
        cpidMapping.put("x-mac-ce", 10029);
        cpidMapping.put("x-mac-icelandic", 10079);
        cpidMapping.put("x-mac-turkish", 10081);
        cpidMapping.put("x-mac-croatian", 10082);
        cpidMapping.put("x-Chinese-CNS", 20000); //Not supported;
        cpidMapping.put("x-cp20001", 20001); //Not supported;
        cpidMapping.put("x-Chinese-Eten", 20002); //Not supported;
        cpidMapping.put("x-cp20003", 20003); //Not supported;
        cpidMapping.put("x-cp20004", 20004); //Not supported;
        cpidMapping.put("x-cp20005", 20005); //Not supported;
        cpidMapping.put("x-IA5", 20105); //Not supported;
        cpidMapping.put("x-IA5-German", 20106); //Not supported;
        cpidMapping.put("x-IA5-Swedish", 20107); //Not supported;
        cpidMapping.put("x-IA5-Norwegian", 20108); //Not supported;
        cpidMapping.put("US-ASCII", 20127);
        cpidMapping.put("x-cp20261", 20261); //Not supported;
        cpidMapping.put("x-cp20269", 20269); //Not supported;
        cpidMapping.put("IBM273", 20273);
        cpidMapping.put("IBM277", 20277);
        cpidMapping.put("IBM278", 20278);
        cpidMapping.put("IBM280", 20280);
        cpidMapping.put("IBM284", 20284);
        cpidMapping.put("IBM285", 20285);
        cpidMapping.put("IBM290", 20290);
        cpidMapping.put("IBM297", 20297);
        cpidMapping.put("IBM420", 20420);
        cpidMapping.put("IBM423", 20423);
        cpidMapping.put("IBM424", 20424);
        cpidMapping.put("x-EBCDIC-KoreanExtended", 20833); //Not supported;
        cpidMapping.put("IBM-Thai", 20838);
        cpidMapping.put("KOI8-R", 20866);
        cpidMapping.put("IBM871", 20871);
        cpidMapping.put("IBM880", 20880);
        cpidMapping.put("IBM905", 20905);
        cpidMapping.put("IBM00924", 20924); //Not supported;
        cpidMapping.put("EUC-JP", 20932);
        cpidMapping.put("x-cp20936", 20936); //Not supported;
        cpidMapping.put("x-cp20949", 20949); //Not supported;
        cpidMapping.put("cp1025", 21025);
        cpidMapping.put("KOI8-U", 21866);
        cpidMapping.put("ISO-8859-1", 28591);
        cpidMapping.put("ISO-8859-2", 28592);
        cpidMapping.put("ISO-8859-3", 28593);
        cpidMapping.put("ISO-8859-4", 28594);
        cpidMapping.put("ISO-8859-5", 28595);
        cpidMapping.put("ISO-8859-6", 28596);
        cpidMapping.put("ISO-8859-7", 28597);
        cpidMapping.put("ISO-8859-8", 28598);
        cpidMapping.put("ISO-8859-9", 28599);
        cpidMapping.put("ISO-8859-13", 28603);
        cpidMapping.put("ISO-8859-15", 28605);
        cpidMapping.put("x-Europa", 29001); //Not supported;
        cpidMapping.put("ISO-8859-8-I", 38598); //Not supported;
        cpidMapping.put("ISO-2022-JP", 50220);
        cpidMapping.put("csISO2022JP", 50221);
        cpidMapping.put("ISO-2022-JP", 50222);
        cpidMapping.put("ISO-2022-KR", 50225);
        cpidMapping.put("x-cp50227", 50227); //Not supported;
        cpidMapping.put("EUC-JP", 51932);
        cpidMapping.put("euc-cn", 51936);
        cpidMapping.put("EUC-KR", 51949);
        cpidMapping.put("hz-gb-2312", 52936); //Not supported;
        cpidMapping.put("GB18030", 54936);
        cpidMapping.put("x-iscii-de", 57002); //Not supported;
        cpidMapping.put("x-iscii-be", 57003); //Not supported;
        cpidMapping.put("x-iscii-ta", 57004); //Not supported;
        cpidMapping.put("x-iscii-te", 57005); //Not supported;
        cpidMapping.put("x-iscii-as", 57006); //Not supported;
        cpidMapping.put("x-iscii-or", 57007); //Not supported;
        cpidMapping.put("x-iscii-ka", 57008); //Not supported;
        cpidMapping.put("x-iscii-ma", 57009); //Not supported;
        cpidMapping.put("x-iscii-gu", 57010); //Not supported;
        cpidMapping.put("x-iscii-pa", 57011); //Not supported;
        cpidMapping.put("utf-7", 65000);
        cpidMapping.put("UTF-8", 65001);
        cpidMapping.put("UTF-32", 65005);
        cpidMapping.put("UTF-32BE", 65006);
        
        for (String name : cpidMapping.keySet()) {
            int cpid = cpidMapping.get(name);
            try {
                Charset charset = Charset.forName(name);
                charset2CpidMapping.put(name.toLowerCase(), cpid);
                for (String alias : charset.aliases())
                    charset2CpidMapping.put(alias.toLowerCase(), cpid);
            } catch (Exception e) {
                if (e instanceof UnsupportedCharsetException)
                    charset2CpidMapping.put(name.toLowerCase(), cpid);
            }
        }
    }
    
    public static int getInternetCPID(String charset) {
        Integer retval = charset2CpidMapping.get(charset.toLowerCase().trim());
        if (retval == null)
            return -1;
        
        return retval;
    }

}
