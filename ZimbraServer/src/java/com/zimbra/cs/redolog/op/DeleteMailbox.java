/*
 * 
 */

/*
 * Created on 2005. 4. 4.
 */
package com.zimbra.cs.redolog.op;

import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class DeleteMailbox extends RedoableOp {

    public DeleteMailbox() {
    }

    public DeleteMailbox(int mailboxId) {
        setMailboxId(mailboxId);
    }

    /* (non-Javadoc)
     * @see com.zimbra.cs.redolog.op.RedoableOp#getOpCode()
     */
    @Override public int getOpCode() {
        return OP_DELETE_MAILBOX;
    }

    /* (non-Javadoc)
     * @see com.zimbra.cs.redolog.op.RedoableOp#redo()
     */
    @Override public void redo() throws Exception {
    }

    /* (non-Javadoc)
     * @see com.zimbra.cs.redolog.op.RedoableOp#getPrintableData()
     */
    @Override protected String getPrintableData() {
        // no members to print
        return null;
    }

    /* (non-Javadoc)
     * @see com.zimbra.cs.redolog.op.RedoableOp#serializeData(java.io.RedoLogOutput)
     */
    @Override protected void serializeData(RedoLogOutput out) {
        // no members to serialize
    }

    /* (non-Javadoc)
     * @see com.zimbra.cs.redolog.op.RedoableOp#deserializeData(java.io.RedoLogInput)
     */
    @Override protected void deserializeData(RedoLogInput in) {
        // no members to deserialize
    }

    @Override public boolean isDeleteOp() {
        return true;
    }
}
