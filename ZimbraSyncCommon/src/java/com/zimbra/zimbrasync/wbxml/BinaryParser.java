/*
 * 
 */
package com.zimbra.zimbrasync.wbxml;

import java.io.ByteArrayInputStream;
import java.io.IOException;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.zimbra.common.util.ZimbraLog;

public class BinaryParser extends BinaryCodec {
    
	protected InputStream in;
	
	//stores the next byte just peeked; -2 means not read
	private int nextByte = -2;
	
	public int nextToken() throws BinaryCodecException, IOException {
		if (degenerated) {
			degenerated = false;
			popElementStack();
			return eventType;
		}

		text = null;

		int id = peek();
		while (id == SWITCH_PAGE) {
			nextByte = -2;
			selectPage(readByte());
			id = peek();
		}
		nextByte = -2;

		switch (id) {
		case -1:
			eventType = END_DOCUMENT;
			break;

		case END:
			popElementStack();
			break;

		case ENTITY:
			throw new BinaryCodecException("Entities not supported");

		case STR_I:
			text = readStrI();
			break;

		default:
			parseElement(id);
		}
		
		return eventType;
	}
	
	public int next() throws BinaryCodecException, IOException {
		return nextToken();
	}

	public int nextTag() throws BinaryCodecException, IOException {

		next();

		if (eventType != END_TAG && eventType != START_TAG) {
			throw new BinaryCodecException("unexpected type");
		}

		return eventType;
	}

	public String nextText() throws BinaryCodecException, IOException {
		if (eventType != START_TAG)
			throw new BinaryCodecException("precondition: START_TAG");

		next();

		String result = "";
		if (eventType == TEXT) {
			result = getText();
			next();
		}

		if (eventType != END_TAG)
			throw new BinaryCodecException("END_TAG expected");

		return result;
	}
	
	public int nextIntegerContent() throws BinaryCodecException, IOException {
		return Integer.parseInt(nextText());
	}
	
	public int nextIntegerContent(int defaultVal) throws BinaryCodecException, IOException {
		try {
			return nextIntegerContent();
		} catch (NumberFormatException x) {
			return defaultVal;
		}
	}

	public void require(int type, String namespace, String name)
			throws BinaryCodecException, IOException {

		if (type != this.eventType
				|| (namespace != null && !namespace.equals(getNamespace()))
				|| (name != null && !name.equals(getName()))) {
			throw new BinaryCodecException("expected: " + TYPES[type] + " {" + namespace + "}" + name +
			                               "; got: " + TYPES[eventType] + " {" + getNamespace() + "}" + getName());
		}
	}
	
	public void openTag(String namespace, String name)
			throws IOException, BinaryCodecException {
		
		nextTag();
		require(START_TAG, namespace, name);
	}
	
	public void closeTag()
			throws IOException, BinaryCodecException {
	
		nextTag();
		require(END_TAG, null, null);
	}
	
	public int getContentAsInteger() {
		return Integer.parseInt(getText());
	}
	
	public String nextTextElement(String namespace, String name)
			throws IOException, BinaryCodecException {
		
		openTag(namespace, name);
		text = nextText();
		require(END_TAG, namespace, name);
		return text;
	}

	public int nextIntegerElement(String namespace, String name)
		throws IOException, BinaryCodecException {

		openTag(namespace, name);
		int number = nextIntegerContent();
		require(END_TAG, namespace, name);
		return number;
	}
	
	public int nextIntegerElement(String namespace, String name, int defaultVal)
		throws BinaryCodecException, IOException {
		
		openTag(namespace, name);
		int number = nextIntegerContent(defaultVal);
		require(END_TAG, namespace, name);
		return number;
	}
	
	public InputStream nextInputStream() throws BinaryCodecException, IOException {
	    if (peek() != STR_I)
	        throw new BinaryCodecException("STR_I expected for element " + getName());
	    nextByte = -2;
	    
	    text("...(data_stream)..."); //logging purpose
	    
	    return new InputStream() {
	        boolean eof;
	        
	        @Override
	        public int read() throws IOException {
	            if (eof)
	                return -1;
	            int b = readByte();
	            if (b == 0) {
	                eof = true;
	                return -1;
	            }
	            return b;
	        }
	        
	        @Override
	        public int read(byte[] bytes) throws IOException {
	            return read(bytes, 0, bytes.length);
	        }
	        
	        @Override
	        public int read(byte[] bytes, int off, int len) throws IOException {
	            if (eof)
	                return -1;
                for (int i = 0; i < len; ++i) {
                    int b = readByte();
                    if (b == 0) {
                        eof = true;
                        return i == 0 ? -1 : i;
                    }
                    bytes[off + i] = (byte)b;
                }
                return len;
	        }
	    };
	}
	
	public BinaryParser(InputStream in, boolean isDebugTraceOn) throws IOException, BinaryCodecException {
	    super(isDebugTraceOn);
		this.in = in;
		startDocument();
	}

	protected void startDocument() throws IOException, BinaryCodecException {
		try {
			// All documents start with 03 01 6A 00
			// 03 WBXML version 1.3
			// 01 unknown public identifyier
			// 6A charset=UTF-8
			// 00 string table length 0 (mswbxml never uses string table)

			if (readByte() != 0x03 || readByte() != 0x01 || readByte() != 0x6A
					|| readByte() != 0x00) {
				throw new BinaryCodecException("Invalid preamble");
			}
		} catch (IOException x) {
			throw new BinaryCodecException(x);
		}
		
		super.startDocument();
		
		try {
			int id = peek();
			if (id != SWITCH_PAGE) {
				selectPage(0); //default to code page 0
			}
		} catch (IOException x) {
			throw new BinaryCodecException(x);
		}
	}

	private int peek() throws IOException {
		if (nextByte == -2) {
			nextByte = readByte();
		}
		return nextByte;
	}

	void parseElement(int id) throws IOException, BinaryCodecException {

		//
		// An element tag is encoded using only 5-0 bits
		// 6th bit indicates whether the element has content
		// 7th bit indicates whether the tag has attributes
		//

		if ((id & 0x80) != 0) {
			throw new BinaryCodecException("Attributes not supported");
		}

		degenerated = (id & 0x40) == 0;

		pushElementStack(id & 0x3F);
	}



	protected int readByte() throws IOException {
		int i = in.read();
		if (i == -1) {
			throw new IOException("Unexpected EOF");
		}
		++byteCount;
		if (byteCount <= wbxml.length)
		    wbxml[byteCount - 1] = (byte)i;
		return i;
	}
	
	protected String readStrI() throws IOException, BinaryCodecException {
		List<Integer> byteList = new ArrayList<Integer>();
		while (true) {
			int i = readByte();
			if (i == -1) {
				throw new IOException("Unexpected EOF");
			}
			if (i == 0) {
				break;
			}
			byteList.add(i);
		}
		byte[] bytes = new byte[byteList.size()];
		for (int i = 0; i < bytes.length; ++i) {
			bytes[i] = byteList.get(i).byteValue();
		}
		
		text(new String(bytes, "UTF-8"));
		
		return text;
	}

	public boolean isEmptyElementTag() throws BinaryCodecException {
		if (eventType != START_TAG) {
			throw new BinaryCodecException("Wrong position in document");
		}
		return degenerated;
	}
	
	
	public String getPositionDescription() {

		StringBuffer buf = new StringBuffer(eventType < TYPES.length ? TYPES[eventType]
				: "unknown");
		buf.append(' ');

		if (eventType == START_TAG || eventType == END_TAG) {
			if (degenerated)
				buf.append("(empty) ");
			buf.append('<');
			if (eventType == END_TAG)
				buf.append('/');

			buf.append(getNamespace());
			buf.append(":");
			buf.append(getName());
			buf.append('>');
		}
		else if (eventType != TEXT)
			buf.append(getText());
		else {
			text = getText();
			if (text.length() > 16)
				text = text.substring(0, 16) + "...";
			buf.append(text);
		}

		return buf.toString();
	}

	public void skipUnknownElement() throws BinaryCodecException, IOException {
	    ZimbraLog.sync.warn("skipping unexpected element " + getName());
	    skipElement();
	}
	
	public void skipElement() throws BinaryCodecException, IOException {
	   String namespace = getNamespace();
	   String name = getName();
	  
	   next();
	   
	   switch (eventType) {
	   case TEXT:
	       closeTag();
	       break;
	   case END_TAG:
	       break;
	   case START_TAG:
	       skipElement();
	       while (next() == START_TAG)
	           skipElement();
	       break;
	   default:
	       throw new BinaryCodecException("unexpected eventType=" + eventType + " while skipping");
	   }
	   
	   require(END_TAG, namespace, name);
	}
	
	public static void main(String[] args) throws Exception {
	    ZimbraLog.toolSetupLog4j("DEBUG", null, false);
	    
	    ByteArrayOutputStream bao = new ByteArrayOutputStream();
	    BinarySerializer bs = new BinarySerializer(bao, true);
	    bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_SYNC);
	    bs.textElement(NAMESPACE_AIRSYNC, AIRSYNC_SYNCKEY, "It works like a charm!");
	    bs.closeTag();
	    
	    byte[] sync = bao.toByteArray();
	    
	    BinaryParser bp = new BinaryParser(new ByteArrayInputStream(sync), true);	    
	    bp.openTag(NAMESPACE_AIRSYNC, AIRSYNC_SYNC);
	    bp.next();
	    assert (bp.getName().equals(AIRSYNC_SYNCKEY));
	    InputStream in = bp.nextInputStream();
	    bao.reset();
	    int b = 0;
	    while ((b = in.read()) != -1)
	        bao.write(b);
        bp.closeTag(); //SyncKey
        bp.closeTag(); //Sync
	    System.out.println(new String(bao.toByteArray()));
	    
	    bp = new BinaryParser(new ByteArrayInputStream(sync), true);
        bp.openTag(NAMESPACE_AIRSYNC, AIRSYNC_SYNC);
        bp.next();
        assert (bp.getName().equals(AIRSYNC_SYNCKEY));
        in = bp.nextInputStream();
        bao.reset();
        byte[] bytes = new byte[10];
        int n = 0;
        while ((n = in.read(bytes)) != -1) {
            bao.write(bytes, 0, n);
        }
        bp.closeTag(); //SyncKey
        bp.closeTag(); //Sync
        System.out.println(new String(bao.toByteArray()));
        
        bp = new BinaryParser(new ByteArrayInputStream(sync), true);        
        bp.openTag(NAMESPACE_AIRSYNC, AIRSYNC_SYNC);
        bp.next();
        assert (bp.getName().equals(AIRSYNC_SYNCKEY));
        in = bp.nextInputStream();
        bao.reset();
        bytes = new byte[10];
        n = 0;
        while ((n = in.read(bytes, 3, 5)) != -1) {
            bao.write(bytes, 3, n);
        }
        bp.closeTag(); //SyncKey
        bp.closeTag(); //Sync
        System.out.println(new String(bao.toByteArray()));
        
        
        bp = new BinaryParser(new ByteArrayInputStream(sync), true);
        bp.openTag(NAMESPACE_AIRSYNC, AIRSYNC_SYNC);
        bp.skipElement();
        
        bao = new ByteArrayOutputStream();
        bs = new BinarySerializer(bao, true);
        bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_SYNC);
        
        bs.textElement(NAMESPACE_AIRSYNC, AIRSYNC_SYNCKEY, "key");
        
        bs.openTag(NAMESPACE_POOMMAIL, POOMMAIL_MEETINGREQUEST);
        bs.textElement(NAMESPACE_POOMMAIL, POOMMAIL_SUBJECT, "Here you go");
        bs.textElement(NAMESPACE_POOMMAIL, POOMCAL_BODY, "blah");
        
        bs.openTag(NAMESPACE_POOMMAIL, POOMMAIL_RECURRENCE);
        bs.textElement(NAMESPACE_POOMMAIL, POOMCAL_UNTIL, "end of days");
        bs.textElement(NAMESPACE_POOMMAIL, POOMCAL_MONTHOFYEAR, "October");
        bs.closeTag(); //Recurrence
        bs.openTag(NAMESPACE_POOMMAIL, POOMMAIL_RECURRENCE);
        bs.textElement(NAMESPACE_POOMMAIL, POOMCAL_UNTIL, "end of days");
        bs.textElement(NAMESPACE_POOMMAIL, POOMCAL_MONTHOFYEAR, "October");
        bs.closeTag(); //Recurrence
        bs.textElement(NAMESPACE_POOMMAIL, POOMCAL_DTSTAMP, "1234");
        bs.closeTag(); //MeetingRequest
        bs.closeTag(); //Sync
        
        bp = new BinaryParser(new ByteArrayInputStream(bao.toByteArray()), true);
        bp.openTag(NAMESPACE_AIRSYNC, AIRSYNC_SYNC);
        bp.nextTextElement(NAMESPACE_AIRSYNC, AIRSYNC_SYNCKEY);
        bp.openTag(NAMESPACE_POOMMAIL, POOMMAIL_MEETINGREQUEST);
        bp.skipElement();
        bp.closeTag();
	}
}
