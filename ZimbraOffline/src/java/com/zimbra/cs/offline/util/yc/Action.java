/*
 * 
 */
package com.zimbra.cs.offline.util.yc;

public enum Action {
    ADD,
    UPDATE,
    REMOVE;

    public static Action getOp(String op) {
        return Action.valueOf(op.toUpperCase());
    }    
}
