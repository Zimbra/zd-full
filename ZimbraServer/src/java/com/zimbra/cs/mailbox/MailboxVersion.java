/*
 * 
 */
package com.zimbra.cs.mailbox;

import com.zimbra.common.service.ServiceException;

public class MailboxVersion {
    // These should be incremented with changes to serialization format.
    private static final short CURRENT_MAJOR = 1;  // range: 0 - Short.MAX_VALUE
    private static final short CURRENT_MINOR = 10; // range: 0 - Short.MAX_VALUE

    private short mMajorVer;
    private short mMinorVer;

    public static MailboxVersion getCurrent() { return CURRENT(); }

    static MailboxVersion CURRENT() {
        return new MailboxVersion();
    }
                    
    MailboxVersion() {
        mMajorVer = CURRENT_MAJOR;
        mMinorVer = CURRENT_MINOR;
    }
    
    public MailboxVersion(short major, short minor) {
        mMajorVer = major;
        mMinorVer = minor;
    }
    
    MailboxVersion(MailboxVersion other) {
        mMajorVer = other.mMajorVer;
        mMinorVer = other.mMinorVer;
    }

    public short getMajor() { return mMajorVer; }
    public short getMinor() { return mMinorVer; }

    static MailboxVersion fromMetadata(Metadata md) throws ServiceException {
        // unknown version are set to 1.0
        short majorVer = 1;
        short minorVer = 0;

        if (md != null) {
            majorVer = (short) md.getLong("vmaj", 1);
            minorVer = (short) md.getLong("vmin", 0);
        }

        return new MailboxVersion(majorVer, minorVer);
    }

    Metadata writeToMetadata(Metadata md) {
        md.put("vmaj", mMajorVer);
        md.put("vmin", mMinorVer);
        return md;
    }
    
    /**
     * Returns if this version is at least as high as the version specified
     * by major and minor.
     * @param major
     * @param minor
     * @return true if this version is higher than or equal to major/minor,
     *         false if this version is lower
     */
    public boolean atLeast(int major, int minor) {
        return (mMajorVer > major ||
                (mMajorVer == major && mMinorVer >= minor));
    }

    /**
     * Returns if this version is at least as high as version b.
     * @param b
     * @return true if this version is higher than or equal to version b,
     *         false if this version is lower than version b
     */
    public boolean atLeast(MailboxVersion b) {
        return atLeast(b.mMajorVer, b.mMinorVer);
    }

    public boolean isLatest() {
        return (mMajorVer == CURRENT_MAJOR && mMinorVer == CURRENT_MINOR);
    }

    /**
     * Returns if this version is higher than latest known code version.
     * @return
     */
    public boolean tooHigh() {
        return (mMajorVer > CURRENT_MAJOR ||
                (mMajorVer == CURRENT_MAJOR && mMinorVer > CURRENT_MINOR));
    }

    @Override public String toString() {
        return Integer.toString(mMajorVer) + "." + Integer.toString(mMinorVer);
    }
}
