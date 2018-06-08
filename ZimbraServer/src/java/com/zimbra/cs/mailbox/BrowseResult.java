/*
 * 
 */

/*
 * Created on Jun 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.zimbra.cs.mailbox;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.cs.index.BrowseTerm;

/**
 * @author schemers
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class BrowseResult {
    private List<BrowseTerm> mResult;

    public BrowseResult() {
        mResult = new ArrayList<BrowseTerm>();
    }
    
    public List<BrowseTerm> getResult() {
        return mResult;
    }
    
    public static class DomainItem extends BrowseTerm {
        static final int F_FROM = 0x1;
        static final int F_TO = 0x2;
        static final int F_CC = 0x4;
        private static final String[] sHeaderFlags = {"", "f", "t", "ft", "c", "fc", "tc", "ftc"}; 

        int mFlags;
        
        DomainItem(BrowseTerm domain) {
            super(domain.term, domain.freq);
        }
        
        public String getDomain() {
            return term;
        }
        
        void addFlag(int flag) {
            mFlags |= flag;
        }
        
        public String getHeaderFlags() {
            return sHeaderFlags[mFlags];
        }
        
        public boolean isFrom() {
            return (mFlags & F_FROM) != 0;
        }
        
        public boolean isTo() {
            return (mFlags & F_TO) != 0;
        }
        
        public boolean isCc() {
            return (mFlags & F_CC) != 0;            
        }        
    }
}
