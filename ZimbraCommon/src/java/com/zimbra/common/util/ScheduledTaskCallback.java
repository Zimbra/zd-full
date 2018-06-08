/*
 * 
 */
package com.zimbra.common.util;

import java.util.concurrent.Callable;

public interface ScheduledTaskCallback<V> {

    void afterTaskRun(Callable<V> task, V lastResult);
}
