/*
 * 
 */
package com.zimbra.cs.im;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapProtocol;

/**
 * 
 */
public class PrivacyList implements Iterable<PrivacyListEntry> {
    
    private String mName;
    private List<PrivacyListEntry> mList = new ArrayList<PrivacyListEntry>();
    private Set<Integer> mOrders = new HashSet<Integer>();

    public static final class DuplicateOrderException extends Exception {
        public String toString() {
            return "The privacy list entry's Order value must be unique to the list. " + super.toString();
        }
    }

    public PrivacyList(String name) {
        mName = name;
    }
    
    public String getName() { return mName; }
    
    public String toString() { 
        try { 
            return toXml(null).toString(); 
        } catch (ServiceException ex) {
            ex.printStackTrace();
            return ex.toString(); 
        } 
    }
    
    public Element toXml(Element parent) throws ServiceException {
        Element list;
        if (parent != null)
            list = parent.addElement("list");
        else 
            list = Element.create(SoapProtocol.Soap12, "list");
        
        list.addAttribute("name", mName);
        for (PrivacyListEntry entry : mList) {
            entry.toXml(list);
        }
        return list;
    }
    

    public void addEntry(PrivacyListEntry entry) throws DuplicateOrderException
    {
        if (mOrders.contains(entry.getOrder()))
            throw new DuplicateOrderException();
        
        mOrders.add(entry.getOrder());
        mList.add(entry);
    }
    
    public Iterator<PrivacyListEntry> iterator() { return mList.iterator(); }
}
