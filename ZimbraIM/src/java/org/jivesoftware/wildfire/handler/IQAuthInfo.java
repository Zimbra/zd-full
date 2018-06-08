/*
 * 
 */
package org.jivesoftware.wildfire.handler;

/**
 * Information for controlling the authentication options for the server.
 *
 * @author Iain Shigeoka
 */
public interface IQAuthInfo {

    /**
     * Returns true if anonymous authentication is allowed.
     *
     * @return true if anonymous logins are allowed
     */
    public boolean isAnonymousAllowed();
}