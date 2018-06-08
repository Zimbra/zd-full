/*
 * 
 */
package com.zimbra.cs.offline;

import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.util.ZimbraApplication;


public class ExchangeExtension implements ZimbraExtension {
    
    public void init() {
        ZimbraApplication.getInstance().addExtensionName(OfflineConstants.EXTENSION_XSYNC);
    }

    public void destroy() {}
    
    public String getName() {
        return OfflineConstants.EXTENSION_XSYNC;
    }
}
