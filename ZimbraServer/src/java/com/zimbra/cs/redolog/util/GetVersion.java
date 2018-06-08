/*
 * 
 */

package com.zimbra.cs.redolog.util;

import com.zimbra.cs.redolog.Version;

/**
 * Print the current redolog version to stdout.  Used by upgrade script.
 * @author jhahm
 *
 */
public class GetVersion {
    public static void main(String[] args) {
        System.out.println(Version.latest());
    }
}
