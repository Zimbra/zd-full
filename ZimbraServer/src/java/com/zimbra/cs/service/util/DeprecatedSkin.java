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
        // bug:106100 Don't deprecate any skin
        return false;

        /*try {
            DeprecatedSkin.valueOf(skin.toLowerCase());
            return true;
        } catch (IllegalArgumentException e1) {
            return false;
        }*/
    }
}
