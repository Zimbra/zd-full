/*
 * 
 */


package com.zimbra.cs.client.soap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.DomUtil;
import com.zimbra.common.soap.SoapParseException;
import com.zimbra.cs.client.*;

public class LmcSaveWikiRequest extends LmcSoapRequest {

	private LmcWiki mWiki;
    
    public void setWiki(LmcWiki wiki) { mWiki = wiki; }
    
    public LmcWiki getWiki() { return mWiki; }
    
	protected Element getRequestXML() throws LmcSoapClientException {
		Element request = DocumentHelper.createElement(MailConstants.SAVE_WIKI_REQUEST);
        Element w = DomUtil.add(request, MailConstants.E_WIKIWORD, "");
        LmcSoapRequest.addAttrNotNull(w, MailConstants.A_NAME, mWiki.getWikiWord());
        LmcSoapRequest.addAttrNotNull(w, MailConstants.A_FOLDER, mWiki.getFolder());
        w.addText(mWiki.getContents());
        return request;
    }

	protected LmcSoapResponse parseResponseXML(Element responseXML)
			throws SoapParseException, ServiceException, LmcSoapClientException {
		
        LmcSaveDocumentResponse response = new LmcSaveDocumentResponse();
        LmcDocument doc = parseDocument(DomUtil.get(responseXML, MailConstants.E_WIKIWORD));
        response.setDocument(doc);
        return response;
	}

}
