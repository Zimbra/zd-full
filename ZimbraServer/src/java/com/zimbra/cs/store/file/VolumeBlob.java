/*
 * 
 */
package com.zimbra.cs.store.file;

import java.io.File;

import com.zimbra.cs.store.Blob;

class VolumeBlob extends Blob {
    private final short volumeId;

    VolumeBlob(File file, short volumeId) {
        super(file);
        this.volumeId = volumeId;
    }

    short getVolumeId() {
        return volumeId;
    }
}
