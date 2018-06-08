/*
 * 
 */
package com.zimbra.cs.zimlet;

/**
 * 
 * @author jylee
 *
 */
public interface ZimletConf {
	public String getGlobalConf(String key);
	public String getSiteConf(String key);
}
