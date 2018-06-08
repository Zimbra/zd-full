/*
 * 
 */

/*
 * Created on Nov 8, 2004
 */
package com.zimbra.cs.filter.jsieve;

import org.apache.jsieve.mail.Action;

public class ActionFlag implements Action {
    private boolean set;
    private int flagId;
    private String name;
    
    public ActionFlag(int flagId, boolean set, String name) {
        setFlag(flagId, set, name);
    }
    
    public int getFlagId() {
        return flagId;
    }
    
    public boolean isSetFlag() {
        return set;
    }
    
    public String getName() {
        return name;
    }
    
    public void setFlag(int flagId, boolean set, String name) {
        this.flagId = flagId;
        this.set = set;
        this.name = name;
    }
    
    public String toString() {
        return "ActionFlag, " + (set ? "set" : "reset") + " flag " + name;
    }
}
