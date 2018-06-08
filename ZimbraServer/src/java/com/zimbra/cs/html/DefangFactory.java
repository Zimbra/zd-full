/*
 * 
 */
package com.zimbra.cs.html;

import com.zimbra.common.mime.MimeConstants;

/**
 * This factory is used to determine the proper defanger based on content type for
 * content that can be natively displayed in most browsers and can have unsavory things
 * added to it (mostly xss script issues).
 * @author jpowers
 *
 */
public class DefangFactory {
    /**
     * The instance of the html defanger 
     */
    private static HtmlDefang htmlDefang = new HtmlDefang();
    
    /**
     * The xml defanger, used for xhtml and svg 
     */
    private static XHtmlDefang xhtmlDefang = new XHtmlDefang();
    
    /**
     * This defanger does nothing. Here for
     * backwards compatibility
     */
    private static NoopDefang noopDefang = new NoopDefang();
    
    /**
     * 
     * @param contentType
     * @return
     */
    public static BrowserDefang getDefanger(String contentType){
        if(contentType == null) {
            return noopDefang;
        }
        
        if(contentType.startsWith(MimeConstants.CT_TEXT_HTML)) {
            return htmlDefang;
        }
        
        if(contentType.startsWith(MimeConstants.CT_TEXT_XML) ||
           contentType.startsWith(MimeConstants.CT_APPLICATION_XHTML) ||
           contentType.startsWith(MimeConstants.CT_IMAGE_SVG)){
            return xhtmlDefang;            
        }
        return noopDefang;
    }
}
