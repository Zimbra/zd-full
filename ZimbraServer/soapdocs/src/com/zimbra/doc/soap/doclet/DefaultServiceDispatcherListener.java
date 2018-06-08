/*
 * 
 */

package com.zimbra.doc.soap.doclet;

import java.util.*;
import com.zimbra.soap.DocumentHandler;
import org.dom4j.QName;

/**
 * 
 * @author sposetti
 *
 */
public class DefaultServiceDispatcherListener implements ServiceDispatcherListener {

	/**
	 * Checks if the command should be registered.
	 * 
	 * @return	<code>true</code> if the command should be registered; <code>false</code> otherwise
	 */
	public boolean	registerCommand(QName qname, DocumentHandler handler) {
		return	true;
	}
 
} // end DefaultServiceDispatcherListener class
