/*
 * 
 */
package org.jivesoftware.wildfire.sasl;

import org.jivesoftware.wildfire.auth.UnauthorizedException;

/**
 * Provider interface for authorization policy. Users that wish to integrate with
 * their own authorization system must implement this class and then register
 * the implementation with Wildfire in the <tt>wildfire.xml</tt>
 * file. An entry in that file would look like the following:
 *
 * <pre>
 *   &lt;provider&gt;
 *     &lt;authorizationpolicy&gt;
 *       &lt;className&gt;com.foo.auth.CustomPolicyProvider&lt;/className&gt;
 *     &lt;/authorizationpolicy&gt;
 *   &lt;/provider&gt;</pre>
 *
 * @author Jay Kline
 */
public interface AuthorizationPolicyProvider {

    /**
     * Returns if the principal is explicity authorized to the JID, throws 
     * an UnauthorizedException otherwise
     *
     * @param username The username requested.
     * @param principal The principal requesting the username.
     * @throws UnauthorizedException
     */
    public void authorize(String username, String principal) throws UnauthorizedException;
    
    
}