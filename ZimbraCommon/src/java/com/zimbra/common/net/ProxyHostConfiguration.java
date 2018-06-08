/*
 * 
 */
package com.zimbra.common.net;

import org.apache.commons.httpclient.HostConfiguration;
/**
 * HostConfiguration that includes proxy username/password
 */
public class ProxyHostConfiguration extends HostConfiguration {
    private String username;
    private String password;
    public ProxyHostConfiguration(HostConfiguration hc) {
        super(hc);
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
