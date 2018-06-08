/*
 * 
 */

package com.zimbra.cs.rmgmt;

public class RemoteResult {
    byte[] mStdout;
    byte[] mStderr;
    int mExitStatus;
    String mExitSignal;
    
    public String getMExitSignal() {
        return mExitSignal;
    }
    
    public int getMExitStatus() {
        return mExitStatus;
    }
    
    public byte[] getMStderr() {
        return mStderr;
    }
    
    public byte[] getMStdout() {
        return mStdout;
    }    
}