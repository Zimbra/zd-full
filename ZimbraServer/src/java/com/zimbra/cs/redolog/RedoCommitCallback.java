/*
 * 
 */

package com.zimbra.cs.redolog;

public interface RedoCommitCallback {
    public void callback(CommitId cid);
}
