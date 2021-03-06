/*
 * 
 */

package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;
import com.zimbra.cs.store.file.Volume;

public class SetCurrentVolume extends RedoableOp {

    private short mType;
    private short mId;

    public SetCurrentVolume() {
    }

    public SetCurrentVolume(short type, short id) {
        mType = type;
        mId = id;
    }

    public int getOpCode() {
        return OP_SET_CURRENT_VOLUME;
    }

    protected String getPrintableData() {
        StringBuffer sb = new StringBuffer("type=").append(mType);
        sb.append(", id=").append(mId);
        return sb.toString();
    }

    protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeShort(mType);
        out.writeShort(mId);
    }

    protected void deserializeData(RedoLogInput in) throws IOException {
        mType = in.readShort();
        mId = in.readShort();
    }

    public void redo() throws Exception {
        Volume.setCurrentVolume(mType, mId, getUnloggedReplay());
    }
}
