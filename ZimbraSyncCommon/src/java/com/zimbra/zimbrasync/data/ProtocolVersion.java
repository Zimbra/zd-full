/*
 * 
 */
package com.zimbra.zimbrasync.data;

import com.zimbra.common.util.ZimbraLog;

/**
 * Sync protocol version
 * @author smukhopadhyay
 *
 */
public final class ProtocolVersion implements Comparable<ProtocolVersion> {
    private int major = 0;
    private int minor = 0; 

    public ProtocolVersion(String version) {
        assert version.length() != 0;
        try {
            String[] digits = version.split("\\.");

            if (digits.length > 0)
                major = Integer.parseInt(digits[0]);
            if (digits.length > 1)
                minor = Integer.parseInt(digits[1]);
        } catch (Exception e) {
            ZimbraLog.sync.warn("unknown remote server version: " + version);
        }
    }
    
    /**
     * Retrieves the major component of the protocol version.
     * @return major component of the version
     */
    public int getMajor() {
        return major;
    }

    /**
     * Retrieves the minor component of the protocol version.
     * @return minor component of the version
     */
    public int getMinor() {
        return minor;
    }
    
    @Override
    public int compareTo(ProtocolVersion other) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        
        if ( this == other ) return EQUAL;
        
        if (this.major < other.major) return BEFORE;
        if (this.major > other.major) return AFTER;
        
        if (this.minor < other.minor) return BEFORE;
        if (this.minor > other.minor) return AFTER;
        
        // all comparisons have yielded equality
        // verify that compareTo is consistent with equals (optional)
        assert this.equals(other) : "compareTo inconsistent with equals.";
        
        return EQUAL;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        
        if (!(other instanceof ProtocolVersion))
            return false;
        
        if (this == other)
            return true;

        ProtocolVersion that = (ProtocolVersion)other;
        return (this.major == that.major)
                && (this.minor == that.minor);
    }
    
    /**
     * hashing function to ensure the Version object is treated as expected in hashmaps and sets. 
     * NOTE: any time the equals() is overridden, hashCode() should also be overridden.
     */
    @Override 
    public int hashCode() {
        return major << 8 | minor;
    }
    
    /**
     * Check this Version against another for compliance (compatibility).
     * If this Version is compatible with the specified one, then true is returned, otherwise false.
     * As a rule of thumb higher versions are always complies to lower.
     * @param other
     * @return true if other complies to this
     */
    public boolean complies(ProtocolVersion other) {
        if (other == null)
            return false;

        if (this.major < other.major) return false;
        
        if (this.major == other.major) {
            if (this.minor < other.minor) 
                return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return Integer.toString(major) + "." + Integer.toString(minor);
    }

}
