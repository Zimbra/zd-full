/*
 * 
 */
package com.zimbra.cs.offline.util.yc.oauth;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.util.Xml;
import com.zimbra.cs.offline.util.yc.YContactException;

public class OAuthGetContactsResponse extends OAuthResponse {

    private static DocumentBuilder docBuilder = Xml.newDocumentBuilder();

    public OAuthGetContactsResponse(String resp) throws YContactException {
        super(resp);
    }

    @Override
    protected void handleResponse() throws YContactException {
        try {
            Document doc = docBuilder.parse(new InputSource(new StringReader(getRawResponse())));
            Element root = doc.getDocumentElement();

            OfflineLog.yab.info("client rev: %s, server rev: %s", root.getAttribute("yahoo:clientrev"),
                    root.getAttribute("yahoo:rev"));

        } catch (Exception e) {
            throw new YContactException("error while creating xml document", "", false, e, null);
        }
    }

}
