/*
 * 
 */
package com.zimbra.zimbrasync.wbxml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;

import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.ZimbraLog;

public class BinarySerializer extends BinaryCodec {
	
	OutputStream out;
	
	boolean pageSwitchPending = false;
	
	public BinarySerializer(OutputStream out, boolean isDebugTraceOn) throws IOException, BinaryCodecException {
	    super(isDebugTraceOn);
		this.out = out;
		startDocument();
	}
    
    protected void startDocument ()
        throws IOException, BinaryCodecException {
    	
		// All documents start with 03 01 6A 00
		// 03 WBXML version 1.3
		// 01 unknown public identifyier
		// 6A charset=UTF-8
		// 00 string table length 0 (mswbxml never uses string table)

		writeByte(0x03);
		writeByte(0x01);
		writeByte(0x6A);
		writeByte(0x00);

		super.startDocument();
    }
    
    public void openTag (String namespace, String name)
        throws IOException, BinaryCodecException {
    	
    	if (pageSwitchPending) {
    		pageSwitchPending = false;
    		writeByte(SWITCH_PAGE);
    		writeByte(codepage);
    	}
    	
    	//If the previous event was START_TAG, write out the previous tag
    	if (eventType == START_TAG) {
    		writeByte(getCode() | 0x40);
    	}    	
    	
    	int codepage = namespaceToCodepage(namespace);
		eventType = START_TAG;
    	selectPage(codepage);

    	//We have to know if this tag is degenerated first, so
    	//we'll save this on the stack first.
    	
    	pushElementStack(tagNameToCode(codepage, name));
    }
    
    public void closeTag ()
        throws IOException, BinaryCodecException {
    	
    	switch(eventType) {
    	case START_TAG: //degenerated
    		writeByte(getCode());
    		break;
    	case TEXT:
    	case END_TAG:
    		writeByte(END);
    		break;
    	default:
    		throw new BinaryCodecException("Invalid context");
    	}
    	
    	popElementStack();
    	if (depth == 0) {
    		flush();
    	}
    }
    
    public void text(String text) throws IOException, BinaryCodecException {
    	if (eventType != START_TAG) {
    		throw new BinaryCodecException("Invalid context");
    	}
    	
    	//write out the previous tag
    	writeByte(getCode() | 0x40);
    	
    	writeStrI(text);
    	super.text(text);
    }
    
    public void text(InputStream in, int limit) throws IOException, BinaryCodecException {
    	if (eventType != START_TAG) {
    		throw new BinaryCodecException("Invalid context");
    	}
    	
    	ByteArrayOutputStream bao = new ByteArrayOutputStream(); //this is for logging purpose only
    	ByteUtil.copy(in, false, bao, false, limit < TEXT_LOGGING_LIMIT ? limit : TEXT_LOGGING_LIMIT);
    	byte[] firstBytes = bao.toByteArray();
    	
    	//write out the previous tag
    	writeByte(getCode() | 0x40);
    	
    	writeStrI(new SequenceInputStream(new ByteArrayInputStream(firstBytes), in), limit);
    	super.text(new String(firstBytes));
    }
    
    public void integerContent(int number) throws IOException, BinaryCodecException {
    	text(Integer.toString(number));
    }
    
    public void textElement(String namespace, String name, String text)
    		throws IOException, BinaryCodecException {
    	openTag(namespace, name);
    	text(text);
    	closeTag();
    }
    
    public void textElement(String namespace, String name, InputStream in, int limit)
			throws IOException, BinaryCodecException {
		openTag(namespace, name);
		text(in, limit);
		closeTag();
    }
    
    public void integerElement(String namespace, String name, int number)
    	    throws IOException, BinaryCodecException {
    	openTag(namespace, name);
    	integerContent(number);
    	closeTag();
    }

    public void emptyElement(String namespace, String name)
		throws IOException, BinaryCodecException {
    	openTag(namespace, name);
    	closeTag();
    }
    
    public void flush () throws IOException {
    	out.flush();
    }
    
    private void writeByte(int b) throws IOException {
    	out.write(b);
    	++byteCount;
    	if (byteCount <= wbxml.length)
    	    wbxml[byteCount - 1] = (byte)b;
    }

    private void writeStrI(String text) throws IOException {
    	writeByte(STR_I);
    	byte[] bytes = null;
    	try {
    		bytes = text.getBytes("UTF-8");
    	} catch (UnsupportedEncodingException x) {
    		ZimbraLog.sync.warn(x);
    		bytes = text.getBytes(); //fall back to just simply get bytes
    	}
    	for (byte b : bytes) {
    		writeByte((int)b);
    	}
        writeByte(0x00);
    }
    
    private void writeStrI(InputStream in, int limit) throws IOException {
    	writeByte(STR_I);

    	int b = 0;
    	int count = 0;
    	while ((limit == -1 || count++ < limit) && (b = in.read()) != -1)
    		writeByte(b);
    	
        writeByte(0x00);
    }
    
	protected void selectPage(int codepage) throws IOException, BinaryCodecException {
    	if (this.codepage == codepage) {
    		return;
    	}
		
    	//If the page swith is caused by closing tag, we need to defer the switch
    	//until the next opening tag.
    	if (eventType == END_TAG) {
    		pageSwitchPending = true;
    	} else {
    		writeByte(SWITCH_PAGE);
    		writeByte(codepage);
    	}
		
		super.selectPage(codepage);
	}
}

