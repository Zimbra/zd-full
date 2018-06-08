/*
 * 
 */

package com.zimbra.doc.soap;

import com.zimbra.soap.DocumentService;
import com.zimbra.doc.soap.doclet.*;
import java.util.*;

/**
 * This class represents the root data model for the SOAP API.
 * 
 * @author sposetti
 *
 */
public	abstract	class	DataModelProvider {

	/**
	 * Gets the data model.
	 * 
	 * @return		the data model
	 */
	public	Root	getRoot() {
		Root root = new Root();
		
		return	loadDataModel(root);
	}
	
	/**
	 * Creates a service.
	 * 
	 * @param	root		the root data model
	 * @param	className	the service class name
	 * @param	name		the service name
	 * @return	the newly created service 
	 */
	protected	Service	createService(Root root, String className, String name) {
		return	new Service(root, className, name);
	}

	/**
	 * Creates a command.
	 * 
	 * @param service			the service			
	 * @param className			the command class name
	 * @param name				the command name
	 * @param	namespace		the namespace
	 * @return	the newly created command 
	 */
	protected	Command	createCommand(Service service, String className, String name, String namespace) {
		return	new Command(service, className, name, namespace);
	}

	/**
	 * Loads the data model.
	 * 
	 * @param		root		the root data model
	 * @return		the loaded data model
	 */
	protected		abstract		Root		loadDataModel(Root root);
	
}