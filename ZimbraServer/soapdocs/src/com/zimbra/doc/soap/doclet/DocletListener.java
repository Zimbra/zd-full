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
public abstract	class DocletListener	{
	
	private	String			className = null;
	
	/**
	 * Constructor.
	 * 
	 * @param		className		the class to register
	 */
	public	DocletListener(String className) {
		this.className = className;
	}

	/**
	 * Gets the class name.
	 * 
	 * @return	the class name
	 */
	public	String		getClassName() {
		return	this.className;
	}
	
	/**
	 * Called when a registered tag is found.
	 * 
	 * @param	tag		the tag
	 */
	public	abstract	void		tagsEvent(Tag[] tags);
	
	/**
	 * Gets the tag text for a given tag.
	 * 
	 * @param	tags		an array of tags
	 * @param	tag			the tag
	 * @return	the tag text or <code>null</code> if tag does not exist
	 */
	protected	static	String		getTagText(Tag[] tags, String tag) {
		if (tags.length > 0) {
			for (int k=0; k < tags.length; k++) {
				if (tags[k].name().equalsIgnoreCase(tag))
					return	tags[k].text();
			}
		}
		return	null;
	}

	/**
	 * Dumps the tags to <code>System.out</code>.
	 * 
	 * @param	tags		an array of tags
	 */
	protected	static	void		dumpTags(Tag[] tags) {
		System.out.println("Dumping tags...");
		System.out.println("tags.length = "+tags.length);
		if (tags.length > 0) {
			for (int k=0; k < tags.length; k++) {
				System.out.println("tags["+k+"].name = "+tags[k].name());
			}
			
		}
	}
	
} // end DocletListener class