/*
 * 
 */

/*
 * Created on 2004. 7. 22.
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.redolog.RedoCommitCallback;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

/**
 * @author jhahm
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CommitTxn extends ControlOp {

    private int mTxnOpCode;

    public CommitTxn() {
        mTxnOpCode = OP_UNKNOWN;
	}

    public CommitTxn(RedoableOp changeEntry) {
    	super(changeEntry.getTransactionId());
        setMailboxId(changeEntry.getMailboxId());
        mTxnOpCode = changeEntry.getOpCode();
        mCommitCallback = changeEntry.mCommitCallback;
    }

    public int getOpCode() {
		return OP_COMMIT_TXN;
	}

    public int getTxnOpCode() {
        return mTxnOpCode;
    }

    protected String getPrintableData() {
        StringBuffer sb = new StringBuffer("txnType=");
        sb.append(getOpClassName(mTxnOpCode));
        return sb.toString();
    }

    protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mTxnOpCode);
    }

    protected void deserializeData(RedoLogInput in) throws IOException {
        mTxnOpCode = in.readInt();
    }

    /**
     * Returns the callback object that was passed in at transaction start time.
     * @return
     */
    public RedoCommitCallback getCallback() {
        return mCommitCallback;
    }
}
