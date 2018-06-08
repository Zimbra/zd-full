/*
 * 
 */
package com.zimbra.cs.store;

import java.io.IOException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;

public abstract class StorageCallback {

    private static Integer sDiskStreamingThreshold;

    public static int getDiskStreamingThreshold() throws ServiceException {
        if (sDiskStreamingThreshold == null)
            loadSettings();
        return sDiskStreamingThreshold;
    }

    public static void loadSettings() throws ServiceException {
        Server server = Provisioning.getInstance().getLocalServer(); 
        sDiskStreamingThreshold = server.getMailDiskStreamingThreshold();
    }


    public void wrote(Blob blob, byte[] data, int numBytes) throws IOException {
        wrote(blob, data, 0, numBytes);
    }

    public abstract void wrote(Blob blob, byte[] data, int offset, int numBytes) throws IOException;

}
