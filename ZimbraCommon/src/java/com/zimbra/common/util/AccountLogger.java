/*
 * 
 */
/**
 * 
 */
package com.zimbra.common.util;

import com.zimbra.common.util.Log.Level;

public class AccountLogger {
    private String mAccountName;
    private String mCategory;
    private Level mLevel;
    
    public AccountLogger(String category, String accountName, Level level) {
        mCategory = category;
        mAccountName = accountName;
        mLevel = level;
    }
    
    public String getAccountName() { return mAccountName; }
    public String getCategory() { return mCategory; }
    public Level getLevel() { return mLevel; }
}