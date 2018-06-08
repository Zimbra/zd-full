/*
 * 
 */
package com.zimbra.cs.im;

import org.xmpp.packet.JID;

public class IMAddr {
    private String mAddr;
    
    public IMAddr(String addr) {
        assert(addr != null);
        assert(addr.indexOf('/') < 0);
        mAddr = addr;
    }

    public IMAddr(JID jid) {
        mAddr = jid.toBareJID();
    }
    
    public String getNode() { return makeJID().getNode(); } 
    
    public String getAddr() { return mAddr; }
    
    public String toString() { return mAddr; }
    
    public String getDomain() { return makeJID().getDomain();}
    
    public JID makeJID() {
        int domainSplit = mAddr.indexOf('@');
        
        if (domainSplit > 0) {
            String namePart = mAddr.substring(0, domainSplit);
            String domainPart = mAddr.substring(domainSplit+1);
            return new JID(namePart, domainPart, "");
        } else {
            return new JID(mAddr);
        }            
    }
    
    public JID makeFullJID(String resource) {
        int domainSplit = mAddr.indexOf('@');
        
        if (domainSplit > 0) {
            String namePart = mAddr.substring(0, domainSplit);
            String domainPart = mAddr.substring(domainSplit+1);
            return new JID(namePart, domainPart, resource);
        } else {
            return new JID(mAddr);
        }            
    }
    
    public static IMAddr fromJID(JID jid) {
        return new IMAddr(jid.toBareJID());
    }
    
    public boolean equals(Object other) {
        return (((IMAddr)other).mAddr).equals(mAddr);
    }
    
    public int hashCode() {
        return mAddr.hashCode();
    }
}
