/*
 * 
 */
package com.zimbra.zimbrasync.data;

import com.zimbra.common.service.ServiceException;

public abstract class PriorityItem {
    protected int itemId;
    
    protected PriorityItem(int itemId) {
        this.itemId = itemId;
    }
    
    public int getItemId() {
        return itemId;
    }
    
    public abstract boolean equals(Object other);
    public abstract String encode();
    public abstract void decode(String metaData) throws ServiceException;
}
