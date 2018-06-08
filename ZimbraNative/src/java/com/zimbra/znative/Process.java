/*
 * 
 */
package com.zimbra.znative;

public class Process {
    private static native int getuid0();
    
    public static int getuid() {
        if (Util.haveNativeCode()) {
            return getuid0();
        } else {
            return -1;
        }
    }

    private static native int geteuid0();

    public static int geteuid() {
        if (Util.haveNativeCode()) {
            return geteuid0();
        } else {
            return -1;
        }
    }
    
    private static native int getgid0();
    
    public static int getgid() {
        if (Util.haveNativeCode()) {
            return getgid0();
        } else {
            return -1;
        }
    }
    
    private static native int getegid0();
    
    public static int getegid() {
        if (Util.haveNativeCode()) {
            return getegid0();
        } else {
            return -1;
        }
    }

    private static native void setPrivileges0(byte[] username, int uid, int gid);

    public static void setPrivileges(String username, int uid, int gid)
        throws OperationFailedException
    {
        if (Util.haveNativeCode()) {
            setPrivileges0(username.getBytes(), uid, gid);
        }
    }
}
