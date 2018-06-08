/*
 * 
 */

package com.zimbra.cs.client.soap;

import com.zimbra.cs.client.*;

public class LmcSaveDocumentResponse extends LmcSoapResponse {

    private LmcDocument mDoc;

    public LmcDocument getDocument() { return mDoc; }

    public void setDocument(LmcDocument doc) { mDoc = doc; }
}
