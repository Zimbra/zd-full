/*
 * 
 */

package com.zimbra.qa.unittest;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.google.common.collect.Lists;

public class TestResults extends RunListener {

    public static class Result {
        public final String className;
        public final String methodName;
        public final long execMillis;
        public final boolean success;
        public final String errorMessage;
        
        private Result(String className, String methodName, long execMillis, boolean success, String errorMessage) {
            this.className = className;
            this.methodName = methodName;
            this.execMillis = execMillis;
            this.success = success;
            this.errorMessage = errorMessage;
        }
    }

    private long lastTestStartTime;
    private boolean lastTestSucceeded;
    private String lastErrorMessage;
    
    private List<Result> results = Lists.newArrayList();

    public List<Result> getResults(boolean success) {
        List<Result> list = Lists.newArrayList();
        for (Result result : this.results) {
            if (result.success == success) {
                list.add(result);
            }
        }
        return list;
    }
    
    @Override
    public void testStarted(Description description) throws Exception {
        lastTestStartTime = System.currentTimeMillis();
        lastTestSucceeded = true;
        lastErrorMessage = null;
    }

    @Override
    public void testFinished(Description desc) throws Exception {
        results.add(new Result(desc.getClassName(), desc.getMethodName(),
            System.currentTimeMillis() - lastTestStartTime, lastTestSucceeded, lastErrorMessage));
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        lastTestSucceeded = false;
        lastErrorMessage = failure.getMessage();
    }
}
