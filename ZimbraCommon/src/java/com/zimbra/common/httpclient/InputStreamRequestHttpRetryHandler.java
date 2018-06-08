/*
 * 
 */

package com.zimbra.common.httpclient;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;


public class InputStreamRequestHttpRetryHandler extends DefaultHttpMethodRetryHandler {

    /**
     * Same as default, but returns false if method is an unbuffered input stream request 
     * This avoids HttpMethodDirector masking real IO exception with bogus 'Unbuffered content cannot be retried' exception
     */
    @Override
    public boolean retryMethod(HttpMethod method, IOException exception,
            int executionCount) {
        boolean canRetry = super.retryMethod(method, exception, executionCount);
        if (canRetry && method instanceof EntityEnclosingMethod) {
            RequestEntity reqEntity = ((EntityEnclosingMethod) method).getRequestEntity();
            if (reqEntity instanceof InputStreamRequestEntity) {
                return ((InputStreamRequestEntity) reqEntity).isRepeatable();
            }
        }
        return canRetry;
    }
}
