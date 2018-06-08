/*
 * 
 */
package com.zimbra.cs.im.interop;

public class UserStatus {
    public String username;
    public String password;
    public InteropSession.State state;
    public long nextConnectAttemptTime;
}