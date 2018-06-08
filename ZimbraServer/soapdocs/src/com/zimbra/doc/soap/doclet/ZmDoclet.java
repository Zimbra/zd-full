/*
 * 
 */

package com.zimbra.doc.soap.doclet;

import java.util.*;
import com.sun.javadoc.*;

/**
 * 
 * @author sposetti
 *
 */
public class ZmDoclet {

	private	static		Map<String,DocletListener>		listeners = new HashMap<String,DocletListener>();
	
	/**
	 * Registers the listener.
	 * 
	 * @param	listener		the listener
	 */
	public	static	void	registerListener(DocletListener listener) {
		listeners.put(listener.getClassName(), listener);
	}
	
	/**
	 * Starts processing the classes at the root document
	 * 
	 * @param	root		the root document
	 */
	public static boolean start(RootDoc root) {
		processContents(root.classes());
		return true;
	}

	/**
	 * Processes the content.
	 * 
	 * @param	classes		the classes to process
	 */
	private static void processContents(ClassDoc[] classes) {
		for (int i=0; i < classes.length; i++) {
			DocletListener listener = checkListener(classes[i]);
			if (listener != null) {
				Tag[] tags = classes[i].tags();
				listener.tagsEvent(tags);				
			}
		}
	}
	
	/**
	 * Checks the registered listeners for a given class.
	 * 
	 * @param	doc		the class
	 * @return	the listener or <code>null</code> if not listener registered for that class
	 */
	private	static		DocletListener	checkListener(ClassDoc doc) {
		String	docClassName = doc.toString();
		return	(DocletListener)listeners.get(docClassName);
	}

} // end ZmDoclet class
