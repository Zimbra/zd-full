/*
 * 
 */

package com.zimbra.common.localconfig;

public class ConfigException extends Exception {
    public ConfigException(String key) {
        super(key);
    }
}
