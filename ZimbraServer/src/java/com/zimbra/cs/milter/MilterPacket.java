/*
 * 
 */
package com.zimbra.cs.milter;

class MilterPacket {
    private int len;
    private byte cmd;
    private byte[] data;
    
    MilterPacket(int len, byte cmd, byte[] data) {
        this.len = len;
        this.cmd = cmd;
        this.data = data;
    }
 
    int getLength() {
        return len;
    }
    
    byte getCommand() {
        return cmd;
    }
    
    byte[] getData() {
        return data;
    }
    
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(len);
        sb.append(':');
        sb.append((char)cmd);
        sb.append(':');
        if (data != null) {           
            for (byte b : data) {
                if (b > 32 &&  b < 127) {
                    sb.append((char)b);
                } else {
                    sb.append("\\");
                    sb.append(b & 0xFF); // make unsigned
                }
                sb.append(' ');
            }
        }
        return sb.toString();
    }
}
