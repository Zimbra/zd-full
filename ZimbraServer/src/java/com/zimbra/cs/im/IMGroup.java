/*
 * 
 */
package com.zimbra.cs.im;

public class IMGroup {
    private String mName;
    
    public IMGroup(String name) {
        mName = name;
    }
    
    public String getName() { return mName; }
    public String toString() { return "GROUP: "+mName; }
}
