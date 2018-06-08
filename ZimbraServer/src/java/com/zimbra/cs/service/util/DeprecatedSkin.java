/*
 * 
 */
package com.zimbra.cs.service.util;

/**
 * bug 88988
 */
public enum DeprecatedSkin {
    lake,
    oasis,
    pebble,
    tree,
    twilight;

    public static boolean isDeprecated(String skin) {
        try {
            DeprecatedSkin.valueOf(skin.toLowerCase());
            return true;
        } catch (IllegalArgumentException e1) {
            return false;
        }
    }
}
