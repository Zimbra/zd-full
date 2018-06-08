/*
 * 
 */
/*
 * Created on Nov 12, 2005
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class SetConfig extends RedoableOp {

    private String mSection;
    private String mConfig;

    public SetConfig() {
        mSection = "";
        mConfig = "";
    }

    public SetConfig(int mailboxId, String section, Metadata config) {
        setMailboxId(mailboxId);
        mSection = section == null ? "" : section;
        mConfig = config == null ? "" : config.toString();
    }

    @Override public int getOpCode() {
        return OP_SET_CONFIG;
    }

    @Override protected String getPrintableData() {
        StringBuffer sb = new StringBuffer("section=").append(mSection);
        sb.append(", config=").append(mConfig.equals("") ? "null" : mConfig);
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeUTF(mSection);
        out.writeUTF(mConfig);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mSection = in.readUTF();
        mConfig = in.readUTF();
    }

    @Override public void redo() throws Exception {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mbox.setConfig(getOperationContext(), mSection, mConfig.equals("") ? null : new Metadata(mConfig));
    }
}
