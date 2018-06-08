/*
 * 
 */

/*
 * Created on Apr 1, 2004
 *
 */
package com.zimbra.cs.mime.handler;

import org.apache.lucene.document.Document;

import com.zimbra.cs.convert.AttachmentInfo;
import com.zimbra.cs.mime.MimeHandler;

/**
 * @author schemers
 *
 *  class that creates a Lucene document from a Java Mail Message
 */
public class UnknownTypeHandler extends MimeHandler {

    private String mContentType;

    @Override
    protected boolean runsExternally() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.zimbra.cs.mime.MimeHandler#populate(org.apache.lucene.document.Document)
     */
    @Override
    public void addFields(Document doc) {
        // do nothing
    }

    /* (non-Javadoc)
     * @see com.zimbra.cs.mime.MimeHandler#getContent()
     */
    @Override
    protected String getContentImpl() {
        return "";
    }
    
    @Override
    public boolean isIndexingEnabled() {
        return true;
    }
    
    @Override
    public String getContentType() {
        return mContentType;
    }

    /* (non-Javadoc)
     * @see com.zimbra.cs.mime.MimeHandler#convert(com.zimbra.cs.convert.AttachmentInfo, java.lang.String)
     */
    @Override
    public String convert(AttachmentInfo doc, String baseURL) {
        throw new IllegalStateException("conversion not allowed for content of unknown type");
    }

    /* (non-Javadoc)
     * @see com.zimbra.cs.mime.MimeHandler#doConversion()
     */
    @Override
    public boolean doConversion() {
        return false;
    }
    
    /**
     * @see com.zimbra.cs.mime.MimeHandler#setContentType(String)
     */
    @Override
    protected void setContentType(String ct) {
        mContentType = ct;
    }
}
