/*
 * 
 */

package com.zimbra.doc.soap.util;

import java.util.*;
import com.zimbra.doc.soap.*;
import com.zimbra.doc.soap.template.*;

/**
 * This class represents a utility for string manipulation.
 * 
 * @author sposetti
 *
 */
public	class	StringUtil {


	/**
	 * Creates a string from the array.
	 * 
	 * @param	strs		a string array
	 * @param	startIdx	the starting idx
	 * @param	delim		the string delimiter
	 * @return	the resulting string
	 */
	public	static	String	createString(String[] strs, int startIdx, String delim) {
		StringBuffer buf = new StringBuffer();
		
		if (strs == null || startIdx > (strs.length-1))
			return	buf.toString();
		
		for (int i=startIdx; i < strs.length; i++) {
			buf.append(strs[i]);
			if (i < (strs.length-1))
				buf.append(delim);
		}
		
		return	buf.toString();
	}
	
	/**
	 * Gets the class name from a FQCN.
	 * 
	 * @return	the class name
	 */
	public	static	String	getClassName(String className) {
		String[]	strs = className.split("\\.");
		
		String	cname = strs[strs.length-1];
		
		if (cname.endsWith("Service"))
			cname = cname.substring(0, cname.length() - "Service".length());
		
		return	cname;
	}

}