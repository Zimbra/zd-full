/*
 * 
 */
package com.zimbra.common.mime;

import com.google.common.collect.ImmutableSet;

public class MimeConstants {

    // content types
    public static final String CT_TEXT_PLAIN = "text/plain";
    public static final String CT_TEXT_HTML = "text/html";
    public static final String CT_TEXT_ENRICHED = "text/enriched";
    public static final String CT_TEXT_CALENDAR = "text/calendar";
    public static final String CT_TEXT_VCARD = "text/vcard";
    public static final String CT_TEXT_VCARD_LEGACY = "text/x-vcard";
    public static final String CT_TEXT_VCARD_LEGACY2 = "text/directory";
    public static final String CT_TEXT_XML = "text/xml";
    public static final String CT_TEXT_XML_LEGACY = "application/xml";
    public static final String CT_MESSAGE_RFC822 = "message/rfc822";
    public static final String CT_APPLICATION_XHTML = "application/xhtml+xml";
    public static final String CT_APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String CT_APPLICATION_BINARY = "application/binary";
    public static final String CT_APPLICATION_MSWORD = "application/msword";
    public static final String CT_APPLICATION_TNEF = "application/ms-tnef";
    public static final String CT_APPLICATION_PDF = "application/pdf";
    public static final String CT_APPLICATION_SHOCKWAVE_FLASH = "application/x-shockwave-flash";
    public static final String CT_MULTIPART_ALTERNATIVE = "multipart/alternative";
    public static final String CT_MULTIPART_DIGEST = "multipart/digest";
    public static final String CT_MULTIPART_MIXED = "multipart/mixed";
    public static final String CT_MULTIPART_REPORT = "multipart/report";
    public static final String CT_MULTIPART_RELATED = "multipart/related";
    public static final String CT_MULTIPART_SIGNED = "multipart/signed";
    public static final String CT_MULTIPART_ENCRYPTED = "multipart/encrypted";
    public static final String CT_XML_ZIMBRA_SHARE = "xml/x-zimbra-share";
    public static final String CT_MULTIPART_PREFIX = "multipart/";
    public static final String CT_TEXT_PREFIX = "text/";
    public static final String CT_MESSAGE_PREFIX = "message/";
    public static final String CT_APPLICATION_WILD = "application/.*";
    public static final String CT_IMAGE_WILD = "image/.*";
    public static final String CT_IMAGE_SVG = "image/svg+xml";
    public static final String CT_AUDIO_WILD = "audio/.*";
    public static final String CT_VIDEO_WILD = "video/.*";
    public static final String CT_MULTIPART_WILD = "multipart/.*";
    public static final String CT_TEXT_WILD = "text/.*";
    public static final String CT_XML_WILD = "xml/.*";
    public static final String CT_DEFAULT = CT_TEXT_PLAIN;
    public static final String CT_APPLICATION_ZIMBRA_DOC = "application/x-zimbra-doc";
    public static final String CT_APPLICATION_ZIMBRA_SLIDES = "application/x-zimbra-slides";
    public static final String CT_APPLICATION_ZIMBRA_SPREADSHEET = "application/x-zimbra-xls";

    // encodings
    public static final String ET_7BIT = "7bit";
    public static final String ET_8BIT = "8bit";
    public static final String ET_BINARY = "binary";
    public static final String ET_QUOTED_PRINTABLE = "quoted-printable";
    public static final String ET_BASE64 = "base64";
    public static final String ET_DEFAULT = ET_7BIT;

    // parameters
    public static final String P_CHARSET = "charset";
    public static final String P_CHARSET_ASCII = "us-ascii";
    public static final String P_CHARSET_UTF8 = "utf-8";
    public static final String P_CHARSET_LATIN1 = "iso-8859-1";
    public static final String P_CHARSET_WINDOWS_1252 = "windows-1252";
    public static final String P_CHARSET_EUC_CN = "euc_cn";
    public static final String P_CHARSET_GB2312 = "gb2312";
    public static final String P_CHARSET_GBK = "gbk";
    public static final String P_CHARSET_WINDOWS_31J = "Windows-31J";
    public static final String P_CHARSET_SHIFT_JIS = "shift_jis";
    public static final String P_CHARSET_DEFAULT = P_CHARSET_ASCII;
    
    public static final ImmutableSet<String> ZIMBRA_DOC_CT_SET = 
        new ImmutableSet.Builder<String>().add(CT_APPLICATION_ZIMBRA_DOC,
                                               CT_APPLICATION_ZIMBRA_SLIDES,
                                               CT_APPLICATION_ZIMBRA_SPREADSHEET).build();
    public static boolean isZimbraDocument(String contentType) {
        return ZIMBRA_DOC_CT_SET.contains(contentType);
    }
}
