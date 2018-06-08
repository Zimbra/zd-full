/*
 * 
 */
package org.jivesoftware.util;

/**
 * A shim interface between Zimbra's properties stores
 * (LocalConfig and Provisioning) and the Wildfire stores
 */
public interface PropertyProvider {
    String get(String key);
    String put(String key, String value);
    String remove(String key);
}
