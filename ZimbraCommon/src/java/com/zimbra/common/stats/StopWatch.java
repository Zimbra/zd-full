/*
 * 
 */

package com.zimbra.common.stats;

/**
 * A <code>Counter</code> that supports <code>start()</code>
 * and <code>stop()</code> methods for conveniently timing events.
 */
public class StopWatch
extends Counter {
    
    public long start() {
        return System.currentTimeMillis();
    }
    
    public void stop(long startTime) {
        increment(System.currentTimeMillis() - startTime);
    }
}