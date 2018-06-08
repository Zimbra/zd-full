/*
 * 
 */

package com.zimbra.cs.client.soap;

import com.zimbra.cs.client.*;

public class LmcSaveWikiResponse extends LmcSoapResponse {

    private LmcWiki mWiki;

    public LmcWiki getWiki() { return mWiki; }

    public void setWiki(LmcWiki wiki) { mWiki = wiki; }
}
