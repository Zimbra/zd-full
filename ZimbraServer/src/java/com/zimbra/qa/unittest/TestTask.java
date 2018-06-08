/*
 * 
 */
package com.zimbra.qa.unittest;

import com.zimbra.cs.mailbox.ScheduledTask;

/**
 * Task used by {@link TestScheduledTaskManager}.
 * 
 * @author bburtin
 *
 */
public class TestTask
extends ScheduledTask {

    int mNumCalls = 0;
    
    public TestTask() {
    }
    
    public String getName() {
        return TestScheduledTaskManager.TASK_NAME;
    }
    
    public int getNumCalls() {
        return mNumCalls;
    }
    
    public Void call() {
        mNumCalls++;
        return null;
    }
}