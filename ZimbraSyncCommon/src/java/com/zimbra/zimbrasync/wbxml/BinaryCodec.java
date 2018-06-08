/*
 * 
 */
package com.zimbra.zimbrasync.wbxml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.StringWriter;
import java.util.HashMap;

import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.SAXException;

import com.zimbra.common.util.ZimbraLog;

public abstract class BinaryCodec extends BinaryCodepages {
	
	static final int TEXT_LOGGING_LIMIT = 4 * 1024;
	static final int WBXML_LOGGING_LIMIT = 4 * 1024;

	public static final boolean isValidCodepage(int codepage) {
		if (codepage >= 0 && codepage < NAMESPACES.length) return true;
		return false;
	}

	public static final String codepageToNamespace(int codepage) {
		return NAMESPACES[codepage];
	}
	
	public static final int namespaceToCodepage(String namespace)
			throws BinaryCodecException {
		for (int i = 0; i < NAMESPACES.length; ++i) {
			if (NAMESPACES[i] == namespace) return i;
		}
		throw new BinaryCodecException("Unknown namespace");
	}
	public static final String codeToTagName(int codepage, int code) {
		try {
			return TAGTABLES[codepage][code-5]; //each table starts at 0x05
		} catch (Exception t) {
			ZimbraLog.sync.warn("Unexpected code: codepage=" + codepage + ";code=" + code, t);
			return "UNKNOWN";
		}
	}
	
	public static final HashMap[] TAGTABLE_MAPS = {
		new HashMap(TAGTABLES[0].length),
		new HashMap(TAGTABLES[1].length),
		new HashMap(TAGTABLES[2].length),
		new HashMap(TAGTABLES[3].length),
		new HashMap(TAGTABLES[4].length),
		new HashMap(TAGTABLES[5].length),
		new HashMap(TAGTABLES[6].length),
		new HashMap(TAGTABLES[7].length),
		new HashMap(TAGTABLES[8].length),
		new HashMap(TAGTABLES[9].length),
		null,
		null,
		new HashMap(TAGTABLES[12].length),
		new HashMap(TAGTABLES[13].length),
		new HashMap(TAGTABLES[14].length),
		new HashMap(TAGTABLES[15].length),
		new HashMap(TAGTABLES[16].length),
		new HashMap(TAGTABLES[17].length),
		new HashMap(TAGTABLES[18].length),
		null,
		new HashMap(TAGTABLES[20].length)
	};
	
	static {
		for (int i = 0; i < TAGTABLES.length; ++i) {
			if (TAGTABLES[i] != null) {
				for (int j = 0; j < TAGTABLES[i].length; ++j) {
					TAGTABLE_MAPS[i].put(TAGTABLES[i][j], Integer.valueOf(j+5));
				}
			}
		}
	}
	
	public static final int tagNameToCode(int codepage, String tagName) {
		return ((Integer)TAGTABLE_MAPS[codepage].get(tagName)).intValue();
	}
	
	/**
     * Signalize that parser is at the very beginning of the document
     * and nothing was read yet.
     * This event type can only be observed by calling getEvent()
     * before the first call to next(), nextToken, or nextTag()</a>).
     *
     * @see #next
     * @see #nextToken
     */
    public static final int START_DOCUMENT = 0;

    /**
     * Logical end of the xml document. Returned from getEventType, next()
     * and nextToken()
     * when the end of the input document has been reached.
     * <p><strong>NOTE:</strong> calling again
     * <a href="#next()">next()</a> or <a href="#nextToken()">nextToken()</a>
     * will result in exception being thrown.
     *
     * @see #next
     * @see #nextToken
     */
    public static final int END_DOCUMENT = 1;

    /**
     * Returned from getEventType(),
     * <a href="#next()">next()</a>, <a href="#nextToken()">nextToken()</a> when
     * a start tag was read.
     * The name of start tag is available from getName(), its namespace and prefix are
     * available from getNamespace() and getPrefix()
     * if <a href='#FEATURE_PROCESS_NAMESPACES'>namespaces are enabled</a>.
     * See getAttribute* methods to retrieve element attributes.
     * See getNamespace* methods to retrieve newly declared namespaces.
     *
     * @see #next
     * @see #nextToken
     * @see #getName
     * @see #getNamespace
     * @see #getDepth
     * @see #getNamespace
     */
    public static final int START_TAG = 2;

    /**
     * Returned from getEventType(), <a href="#next()">next()</a>, or
     * <a href="#nextToken()">nextToken()</a> when an end tag was read.
     * The name of start tag is available from getName(), its
     * namespace is available from getNamespace().
     *
     * @see #next
     * @see #nextToken
     * @see #getName
     * @see #getNamespace
     */
    public static final int END_TAG = 3;


    /**
     * Character data was read and will is available by calling getText().
     * <p><strong>Please note:</strong> <a href="#next()">next()</a> will
     * accumulate multiple events into one TEXT event.
     * In contrast, <a href="#nextToken()">nextToken()</a> will stop reading
     * text when any other event is observed.
     * Also, when the state was reached by calling next(), the text value will
     * be normalized, whereas getText() will
     * return unnormalized content in the case of nextToken(). This allows
     * an exact roundtrip without chnanging line ends when examining low
     * level events, whereas for high level applications the text is
     * normalized apropriately.
     *
     * @see #next
     * @see #nextToken
     * @see #getText
     */
    public static final int TEXT = 4;

    /**
     * This array can be used to convert the event type integer constants
     * such as START_TAG or TEXT to
     * to a string. For example, the value of TYPES[START_TAG] is
     * the string "START_TAG".
     *
     * This array is intended for diagnostic output only. Relying
     * on the contents of the array may be dangerous since malicous
     * applications may alter the array, although it is final, due
     * to limitations of the Java language.
     */
    protected static final String [] TYPES = {
		"START_DOCUMENT",
		"END_DOCUMENT",
		"START_TAG",
		"END_TAG",
		"TEXT"
    };

	
    protected int byteCount = 0;
	
    //the event type just occurred
	protected int eventType = START_DOCUMENT;
	
	//current codepage
	protected int codepage = 0;
	
	protected int rootCodepage = 0;
	
	//nesting depth
	protected int depth = 0;

	//the element stack
	private int[] elementStack = new int[16];
	
	//the current element is degenerated
	protected boolean degenerated = false;
	
	//stores the text just read in
	protected String text;
	
    byte[] wbxml = new byte[WBXML_LOGGING_LIMIT];
	private boolean isDebugTraceOn;
    private StringWriter xmlWriter;
    private XMLSerializer xmlOut;
	
    protected BinaryCodec() {
        this(false);
    }
	
    public BinaryCodec(boolean isDebugTraceOn) {
        this.isDebugTraceOn = isDebugTraceOn;
        if (isDebugTraceOn) {
            xmlWriter = new StringWriter();
            xmlOut = new XMLSerializer(xmlWriter, new OutputFormat(Method.XML, "utf-8", true));
        }
    }
	
	public String getText() {
		return text;
	}
	
	public int getEventType() {
		return eventType;
	}
	
	public int getDepth() {
		return depth;
	}

	protected int getCode() {
		if (eventType == END_TAG) {
			return elementStack[depth] & 0xFF;
		}
		return elementStack[depth-1] & 0xFF;
	}
	
	public String getName() {
		return codeToTagName(codepage, getCode());
	}
	
	public String getNamespace() {
		return codepageToNamespace(codepage);
	}
	
	public void logCodecError(InputStream remainder, int contentLength, Exception x) throws BinaryCodecException, IOException {
	    InputStream is = new SequenceInputStream(new ByteArrayInputStream(wbxml, 0, byteCount), remainder);
	    if (x.getMessage() != null)
	        ZimbraLog.sync.warn("wbxml error: " + x.getMessage());
	    ZimbraLog.sync.warn(formatBytes(is, contentLength, contentLength > WBXML_LOGGING_LIMIT ? WBXML_LOGGING_LIMIT : contentLength, byteCount - 1));
	}

    protected void startDocument () throws IOException, BinaryCodecException {
        if (isDebugTraceOn) {
    		xmlWriter.getBuffer().append("\n");
    		try {
    			xmlOut.startDocument();
    		} catch (SAXException x) {
    			throw new BinaryCodecException(x);
    		}
        }
    }
    
    protected void endDocument () throws IOException, BinaryCodecException {
        if (isDebugTraceOn) {
        	try {
        	    if (ZimbraLog.wbxml.isDebugEnabled())
        	        ZimbraLog.wbxml.debug(formatBytes(new ByteArrayInputStream(wbxml), byteCount, byteCount > wbxml.length ? wbxml.length : byteCount));
    			xmlOut.endDocument();
    			xmlWriter.flush();
    			ZimbraLog.sync.debug(xmlWriter.toString());
    		} catch (SAXException x) {
    			throw new BinaryCodecException(x);
    		}
        }
    }
    
    private static String escapeCharacters(String text) {
    	
    	StringBuffer buf = new StringBuffer(text.length());
    	for (int i = 0; i < text.length(); ++i) {
    		char c = text.charAt(i);
    		if (c != '\n' && c != '\r' && c != '\t' && Character.isISOControl(c)) {
    			buf.append("##x").append(Integer.toHexString((int)c)).append(';');
    		} else {
    			buf.append(c);
    		}
    	}
    	return buf.toString();
    }
    
    public void text(String text) throws IOException, BinaryCodecException {
    	eventType = TEXT;
    	
    	this.text = text;
    	
    	if (isDebugTraceOn) {
    		try {
    			String escaped = escapeCharacters(text);
    			//xmlOut.startNonEscaping();
    			if (escaped.length() > TEXT_LOGGING_LIMIT - 3) {
    				xmlOut.characters(escaped.toCharArray(), 0, TEXT_LOGGING_LIMIT - 3);
    				xmlOut.characters("...".toCharArray(), 0, 3);
    			} else {
    				xmlOut.characters(escaped.toCharArray(), 0, escaped.length());
    			}
    			//xmlOut.endNonEscaping();
    		} catch (Exception x) {
    		    ZimbraLog.sync.warn(x);
    		}
    	}
    }

    protected void selectPage(int codepage) throws IOException, BinaryCodecException {
		if (!isValidCodepage(codepage)) {
			throw new BinaryCodecException("Invalid WBXML codepage, " + codepage);
		}

		this.codepage = codepage;

		if (depth == 0) {
		    try {
				rootCodepage = codepage;
				if (isDebugTraceOn)
				    xmlOut.startPrefixMapping("", getNamespace());
			} catch (SAXException x) {
				throw new BinaryCodecException(x);
			}
		}
    }
	
	protected void pushElementStack(int code) throws BinaryCodecException {
		eventType = START_TAG;
		
		elementStack = ensureCapacity(elementStack, depth+1);
		elementStack[depth++] = codepage << 8 | code;
		
		if (isDebugTraceOn) {
    		try {
    			if (codepage == rootCodepage) {
    				xmlOut.startElement(getNamespace(), getName(), null, null);
    			} else {
    				xmlOut.startElement(null, null, getNamespace() + ":" + getName(), null);
    			}
    		} catch (SAXException x) {
    			throw new BinaryCodecException(x);
    		}
		}
	}
	
	protected void popElementStack() throws IOException, BinaryCodecException {
		eventType = END_TAG;
		
		//in wbxml the codepage doesn't switch until an opening tag
		//so we have to force switching namespace
		selectPage(elementStack[--depth] >> 8);
		
		if (isDebugTraceOn) {
    		try {
    			if (codepage == rootCodepage) {
    				xmlOut.endElement(getNamespace(), getName(),  null);
    			} else {
    				xmlOut.endElement(null, null, getNamespace() + ":" + getName());
    			}
    			
    		} catch (SAXException x) {
    			throw new BinaryCodecException("Debug logging exception");
    		}
		}
		
		if (depth == 0) {
			endDocument();
		}
	}
	
	private final int[] ensureCapacity(int[] array, int required) {
		if (array.length >= required) {
			return array;
		}
		
		int[] bigger = new int[required / 16 * 16 + 16];
		System.arraycopy(array, 0, bigger, 0, array.length);
		return bigger;
	}
	
	public int getByteCount() {
		return byteCount;
	}
    
	private static String formatBytes(InputStream in, int total, int limit, int errPos) throws IOException {
		StringBuilder hexBuf = new StringBuilder();
		for (int i = 0; i < (limit > 0 && total > limit ? limit : total); ++i) {
			if (i % 32 == 0) {
				hexBuf.append("\n");
				String countStr = Integer.toHexString(i);
				switch(countStr.length()) {
				case 1:
					hexBuf.append("00000");
					break;
				case 2:
					hexBuf.append("0000");
					break;
				case 3:
					hexBuf.append("000");
					break;
				case 4:
					hexBuf.append("00");
					break;
				case 5:
					hexBuf.append("0");
					break;
				case 6:
					break;
				default:
					throw new RuntimeException();
				}
				hexBuf.append(countStr).append(":  ");
			} else if (i % 16 == 0) {
				hexBuf.append(" ");
			}
			String hex = Integer.toHexString(in.read()).toUpperCase();
			if (hex.length() == 1) {
				hexBuf.append('0');
			}
			hexBuf.append(hex);
			if (i == errPos) {
				hexBuf.append('*');
			} else {
				hexBuf.append(' ');
			}
		}
		if (limit > 0 && total > limit)
            hexBuf.append("\n         (" + (total - limit) + " remaining bytes skipped)");
		hexBuf.append("\n");
		return hexBuf.toString();
	}
	
	private static String formatBytes(InputStream in, int total, int limit) throws IOException {
		return formatBytes(in, total, limit, -1);
	}
	
	public static void main(String[] args) throws Exception {
	    ByteArrayOutputStream bao = new ByteArrayOutputStream();
	    BinarySerializer bs = new BinarySerializer(bao, true);
	    
	    bs.openTag(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_FOLDERSYNC);
	    bs.integerElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SYNCKEY, 0);
	    bs.closeTag();
	    
	    BinaryParser bp = new BinaryParser(new ByteArrayInputStream(bao.toByteArray()), true);
	    bp.openTag(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_FOLDERSYNC);
	    bp.nextIntegerElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_SYNCKEY);
	    bp.closeTag();
	    
	    InputStream is = new ByteArrayInputStream(bao.toByteArray());
	    bp = new BinaryParser(is, true);
	    bp.openTag(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_FOLDERSYNC);
	    try {
	        bp.nextIntegerElement(NAMESPACE_FOLDERHIERARCHY, FOLDERHIERARCHY_STATUS);
	    } catch (Exception x) {
	        bp.logCodecError(is, bao.size(), x);
	    }
	    
	    byte[] bytes = new byte[8 * 1024];
	    for (int i = 0; i < bytes.length; ++i)
	        bytes[i] = (byte)(i % 256);
	    
	    is = new ByteArrayInputStream(bytes);
	    System.out.println(BinaryCodec.formatBytes(is, bytes.length, WBXML_LOGGING_LIMIT));
	    
	    is.reset();
	    System.out.println(BinaryCodec.formatBytes(is, bytes.length, WBXML_LOGGING_LIMIT, 2 * 1024 - 5));
	}
}
