/*
 * 
 */
package com.zimbra.cs.offline.backup;

import java.io.File;

public class BackupInfo {
    private File file;
    private long timestamp; //not necessarily the modified time on file

    public BackupInfo(File file) {
        super();
        this.file = file;
    }

    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
