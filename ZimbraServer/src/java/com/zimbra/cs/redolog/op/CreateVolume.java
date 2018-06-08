/*
 * 
 */

package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;
import com.zimbra.cs.store.file.Volume;
import com.zimbra.cs.store.file.VolumeServiceException;

public class CreateVolume extends RedoableOp {

    private short mId = Volume.ID_NONE;
    private short mType;
    private String mName;
    private String mRootPath;

    private short mMboxGroupBits;
    private short mMboxBits;
    private short mFileGroupBits;
    private short mFileBits;
    
    private boolean mCompressBlobs;
    private long mCompressionThreshold;

    public CreateVolume() {
    }

    public CreateVolume(short type, String name, String rootPath,
                        short mboxGroupBits, short mboxBits,
                        short fileGroupBits, short fileBits,
                        boolean compressBlobs, long compressionThreshold) {
        mType = type;
        mName = name;
        mRootPath = rootPath;
        
        mMboxGroupBits = mboxGroupBits;
        mMboxBits = mboxBits;
        mFileGroupBits = fileGroupBits;
        mFileBits = fileBits;
        mCompressBlobs = compressBlobs;
        mCompressionThreshold = compressionThreshold;
    }

    public void setId(short id) {
        mId = id;
    }

    public int getOpCode() {
        return OP_CREATE_VOLUME;
    }

    protected String getPrintableData() {
        Volume v = new Volume(mId, mType, mName, mRootPath,
                              mMboxGroupBits, mMboxBits,
                              mFileGroupBits, mFileBits,
                              mCompressBlobs, mCompressionThreshold);
        return v.toString();
    }

    protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeShort(mId);
        out.writeShort(mType);
        out.writeUTF(mName);
        out.writeUTF(mRootPath);
        out.writeShort(mMboxGroupBits);
        out.writeShort(mMboxBits);
        out.writeShort(mFileGroupBits);
        out.writeShort(mFileBits);
        out.writeBoolean(mCompressBlobs);
    }

    protected void deserializeData(RedoLogInput in) throws IOException {
        mId = in.readShort();
        mType = in.readShort();
        mName = in.readUTF();
        mRootPath = in.readUTF();
        mMboxGroupBits = in.readShort();
        mMboxBits = in.readShort();
        mFileGroupBits = in.readShort();
        mFileBits = in.readShort();
        mCompressBlobs = in.readBoolean();
    }

    public void redo() throws Exception {
        try {
            Volume vol = Volume.getById(mId);
            if (vol != null) {
                mLog.info("Volume " + mId + " already exists");
                return;
            }
        } catch (VolumeServiceException e) {
            if (e.getCode() != VolumeServiceException.NO_SUCH_VOLUME)
                throw e;
        }
        try {
            Volume.create(mId, mType, mName, mRootPath,
                          mMboxGroupBits, mMboxBits,
                          mFileGroupBits, mFileBits,
                          mCompressBlobs, mCompressionThreshold,
                          getUnloggedReplay());
        } catch (VolumeServiceException e) {
            if (e.getCode() == VolumeServiceException.ALREADY_EXISTS)
                mLog.info("Volume " + mId + " already exists");
            else
                throw e;
        }
    }
}
