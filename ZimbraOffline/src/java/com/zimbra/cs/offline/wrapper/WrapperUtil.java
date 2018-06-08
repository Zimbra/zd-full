/*
 * 
 */
package com.zimbra.cs.offline.wrapper;

import java.io.*;

public class WrapperUtil {
    public static void redirect(String file) {
        try {
            System.setOut(new PrintStream(new FileOutputStream(file), true));
        } catch (IOException e) {}
    }

    public static void shutdown() {
        System.exit(0);
    }
}
