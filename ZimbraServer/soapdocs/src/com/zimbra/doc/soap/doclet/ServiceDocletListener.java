/*
 * 
 */

package com.zimbra.doc.soap.doclet;

import com.zimbra.doc.soap.Service;
import java.util.*;
import com.sun.javadoc.*;

/**
 * 
 * @author sposetti
 *
 */
public 	class ServiceDocletListener	extends	DocletListener {
	
	public	static	final	String			TAG_SERVICE_DESCRIPTION = "@zm-service-description";

	private	Service		service = null;
	
	/**
	 * Constructor.
	 * 
	 * @param		service		the service
	 */
	public	ServiceDocletListener(Service service) {
		super(service.getClassName());
		this.service = service;
	}

	/**
	 * Called when a registered class is found.
	 * 
	 * @param	tags		the tags
	 */
	public	void		tagsEvent(Tag[] tags) {
		String description = getTagText(tags, TAG_SERVICE_DESCRIPTION);
		this.service.setDescription(description);
	}
	
} // end ServiceDocletListener class