/*
 * 
 */

package com.zimbra.cs.account;

public class ZimbraAuthTokenEncoded extends ZimbraAuthToken {
    private String encoded;
    
    public ZimbraAuthTokenEncoded(String encoded) {
        this.encoded = encoded;
    }
    
    @Override
    public String getEncoded() {
        return encoded;
    }
}
