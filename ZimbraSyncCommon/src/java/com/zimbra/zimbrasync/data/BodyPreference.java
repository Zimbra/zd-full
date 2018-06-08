/*
 * 
 */
package com.zimbra.zimbrasync.data;

public class BodyPreference {
    
    public static final int TRUNCATION_ALL = 16*1024*1024; // limit the body to 16M even if the device asks for all
    
    private int truncationSize = TRUNCATION_ALL;
    private BodyType type = BodyType.PlainText;
    private int allOrNone = 0;
    
    public int getTruncationSize() {
        return truncationSize;
    }
    public void setTruncationSize(int truncationSize) {
        this.truncationSize = truncationSize;
    }
    public BodyType getType() {
        return type;
    }
    public void setType(BodyType type) {
        this.type = type;
    }
    public int getAllOrNone() {
        return allOrNone;
    }
    public void setAllOrNone(int allOrNone) {
        this.allOrNone = allOrNone;
    }
}
