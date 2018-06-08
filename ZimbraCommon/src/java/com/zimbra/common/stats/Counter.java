/*
 * 
 */
package com.zimbra.common.stats;

/**
 * Tracks a total and count (number of calls to {@link #increment}).
 */
public class Counter {

    private volatile long mCount = 0;
    private volatile long mTotal = 0;
    
    public long getCount() {
        return mCount;
    }
    
    public long getTotal() { 
        return mTotal;
    }

    /**
     * Returns the average since the last
     * call to {@link #reset}.
     */
    public synchronized double getAverage() {
        if (mCount == 0) {
            return 0.0;
        } else {
            return (double) mTotal / (double) mCount;
        }
    }

    /**
     * Increments the total by the specified value.  Increments the count by 1.
     */
    public synchronized void increment(long value) {
        mCount++;
        mTotal += value;
    }

    /**
     * Increments the count and total by 1.  
     */
    public synchronized void increment() {
        increment(1);
    }
    
    public synchronized void reset() {
        mCount = 0;
        mTotal = 0;
    }
}
