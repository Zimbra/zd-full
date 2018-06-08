/*
 * 
 */

package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;
import com.zimbra.cs.store.file.Volume;

public class ModifyVolume extends RedoableOp {

    private short mId;
    private short mType;
    private String mName;
    private String mRootPath;

    private short mMboxGroupBits;
    private short mMboxBits;
    private short mFileGroupBits;
    private short mFileBits;
    
    private boolean mCompressBlobs;
    private long mCompressionThreshold;

    public ModifyVolume() {
    }

    public ModifyVolume(short id, short type, String name, String rootPath,
                        short mboxGroupBits, short mboxBits,
                        short fileGroupBits, short fileBits,
                        boolean compressBlobs, long compressionThreshold) {
        mId = id;
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

    public int getOpCode() {
        return OP_MODIFY_VOLUME;
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
    }

    public void redo() throws Exception {
        Volume.getById(mId);  // make sure it exists
        Volume.update(mId, mType, mName, mRootPath,
                      mMboxGroupBits, mMboxBits,
                      mFileGroupBits, mFileBits,
                      mCompressBlobs, mCompressionThreshold,
                      getUnloggedReplay());
    }
}
