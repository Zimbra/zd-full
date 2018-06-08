/*
 * 
 */
package com.zimbra.cs.zimlet;

/**
 * 
 * @author jylee
 *
 */
public interface ZimletHandler {
	public String[] match(String document, ZimletConf config) throws ZimletException;
}
