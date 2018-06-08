/*
 * 
 */

package com.zimbra.cs.memcached;

// list of all memcached key prefixes used by ZCS
public class MemcachedKeyPrefix {

    private static final String DELIMITER = ":";

    public static final String CALENDAR_LIST        = "zmCalsList" + DELIMITER;
    public static final String CTAGINFO             = "zmCtagInfo" + DELIMITER;
    public static final String CALDAV_CTAG_RESPONSE = "zmCtagResp" + DELIMITER;
    public static final String CAL_SUMMARY          = "zmCalSumry" + DELIMITER;

    public static final String EFFECTIVE_FOLDER_ACL = "zmEffFolderACL" + DELIMITER;

    public static final String MBOX_FOLDERS_TAGS    = "zmFldrsTags" + DELIMITER;
}
