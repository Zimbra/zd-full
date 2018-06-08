/*
 * 
 */

package com.zimbra.doc.soap;

import java.util.*;

/**
 * This class represents the root data model for the SOAP API.
 * 
 * @author sposetti
 *
 */
public	class	Root {
		
	private	List<Service>	services = new LinkedList<Service>();

	/**
	 * Constructor.
	 * 
	 */
    Root() {
    	
    }

    /**
     * Adds the service.
     * 
     * @param	service		the service
     */
    public	void		addService(Service service) {
    	this.services.add(service);
    }

    /**
     * Gets the services.
     * 
     * @return	a list of {@link Service} objects
     */
    public		List<Service>	getServices() {
    	return	Collections.unmodifiableList(this.services);
    }

    /**
     * Gets a list of all commands in all services.
     * 
     * @return	a list of {@link Command} objects
     */
    public	List<Command>	getAllCommands() {
    	List<Command>	allCommands = new LinkedList<Command>();
    	
    	Iterator sit = this.getServices().iterator();
    	while (sit.hasNext()) {
    		Service	s = (Service)sit.next();
        	Iterator cit = s.getCommands().iterator();
        	while (cit.hasNext()) {
        		Command	c = (Command)cit.next();
        		allCommands.add(c);
        	}
    	}

    	Collections.sort(allCommands, new Command.CommandComparator());
    	
    	return	allCommands;
    }
    
    /**
     * Dumps the contents to <code>System.out.println</code>
     * 
     */
    public	void	dump() {
    	this.dump(false);
    }

    /**
     * Dumps the contents to <code>System.out.println</code>
     * 
     * @param	commands		if <code>true</code>, dump commands
     */
    public	void	dump(boolean commands) {
		System.out.println("Dump doc root...");
		System.out.println(this);

		System.out.println("Dump services...");
    	Iterator it = this.services.iterator();
    	while (it.hasNext()) {
    		Service s = (Service)it.next();
    		s.dump(commands);
    	}
    }
    
    /**
     * Returns a string representation of this object.
     * 
     * @return	a string representation of this object
     */
    public	String	toString() {
    	StringBuffer buf = new StringBuffer();

		buf.append("[docroot");
		buf.append(";hashCode=");
		buf.append(hashCode());
		buf.append(";serviceCount=");
		buf.append(this.services.size());
		buf.append("]");

		return	buf.toString();
    }
    
    
} // end Root class