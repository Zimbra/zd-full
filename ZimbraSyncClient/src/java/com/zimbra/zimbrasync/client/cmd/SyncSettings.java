/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import org.apache.commons.codec.binary.Base64;

/**
 * @author JJ Zhuang
 */
public class SyncSettings {
	String userAgent;
	String deviceId;
	
	String host;
	int port;
	boolean useSSL;
	
	private String domain;
	String username;
	private String password;
	
	public SyncSettings(String userAgent, String deviceId, String host, int port, boolean useSSL, String domain, String username, String password) {
		this.userAgent = userAgent;
		this.deviceId = deviceId;
		this.host = host;
		this.port = port;
		this.useSSL = useSSL;
		this.domain = domain;
		this.username = username;
		this.password = password;
	}
	
	public String getHostUri() {
	    return (useSSL ? "https" : "http") + "://" + host + ((port == 0 || useSSL && port == 443 || !useSSL && port == 80) ? "" : ":" + port);
	}
	
	public String getBasicAuthString() {
		return "Basic " + new String(Base64.encodeBase64(((domain == null || domain.equals("") ? "" : domain + "\\") + username + ":" + password).getBytes())); 
	}
}
