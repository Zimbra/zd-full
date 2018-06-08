/*
 * 
 */
package com.zimbra.cs.nio;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NioThreadFactory implements ThreadFactory {
    private final ThreadGroup threadGroup;
    private final AtomicInteger threadCount = new AtomicInteger(1);
    private final String namePrefix;

    public NioThreadFactory(String prefix) {
        SecurityManager sm = System.getSecurityManager();
        namePrefix = prefix + "-";
        threadGroup = sm != null ?
            sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    public Thread newThread(Runnable r) {
        String name = namePrefix + threadCount.getAndIncrement();
        Thread t = new Thread(threadGroup, r, name);
        t.setDaemon(false);
        t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
