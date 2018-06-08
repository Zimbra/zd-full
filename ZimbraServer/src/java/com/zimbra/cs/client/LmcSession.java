/*
 * 
 */

package com.zimbra.cs.client;

import com.zimbra.common.auth.ZAuthToken;

/**
 * Encapsulate the notion of a session, including auth token, session ID, and
 * whatever else is desired...
 */
public class LmcSession {

	private ZAuthToken mAuthToken;

	private String mSessionID;

	public ZAuthToken getAuthToken() {
		return mAuthToken;
	}

	public String getSessionID() {
		return mSessionID;
	}

	public void setAuthToken(ZAuthToken a) {
		mAuthToken = a;
	}

	public void setSessionID(String s) {
		mSessionID = s;
	}

	public LmcSession(ZAuthToken authToken, String sessionID) {
		mAuthToken = authToken;
		mSessionID = sessionID;
	}
}