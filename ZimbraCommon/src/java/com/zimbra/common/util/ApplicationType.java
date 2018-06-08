package com.zimbra.common.util;

public class ApplicationType {
    private static boolean isZDesktop;

    public static boolean isZDesktop() {
        return isZDesktop;
    }

    public static void setZDesktop(boolean isZDesktop) {
        ApplicationType.isZDesktop = isZDesktop;
    }
}
