/*
 * 
 */
package com.zimbra.cs.util.yauth;

public interface Auth {
    String getAppId();
    String getWSSID();
    String getCookie();
    boolean isExpired();
}
