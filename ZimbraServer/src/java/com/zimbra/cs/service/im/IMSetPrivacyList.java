/*
 * 
 */
package com.zimbra.cs.service.im;

import java.util.Iterator;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.cs.im.IMAddr;
import com.zimbra.cs.im.IMPersona;
import com.zimbra.cs.im.PrivacyList;
import com.zimbra.cs.im.PrivacyListEntry;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * 
 */
public class IMSetPrivacyList extends IMDocumentHandler {

    /* @see com.zimbra.soap.DocumentHandler#handle(com.zimbra.common.soap.Element, java.util.Map) */
    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        
        Element list = request.getElement(IMConstants.E_LIST);
        
        PrivacyList pl = new PrivacyList(list.getAttribute(IMConstants.A_NAME, null));
        
        for (Iterator<Element> iter = list.elementIterator(IMConstants.E_ITEM); iter.hasNext();) {
            Element item = iter.next();
            
            String actionStr = item.getAttribute(IMConstants.A_ACTION);
            PrivacyListEntry.Action action = PrivacyListEntry.Action.valueOf(actionStr);
            int order = (int)item.getAttributeLong(IMConstants.A_ORDER);
            if (order <= 0)
                throw ServiceException.INVALID_REQUEST("Order must be >= 0 in item "+item.toString(), null);
            String addr = item.getAttribute(IMConstants.A_ADDRESS);
            
            PrivacyListEntry entry = new PrivacyListEntry(new IMAddr(addr), order, action, PrivacyListEntry.BLOCK_ALL);
            
            try { 
                pl.addEntry(entry);
            } catch (PrivacyList.DuplicateOrderException e) {
                throw ServiceException.INVALID_REQUEST("Order must be unique for every item within list.  Problem at entry: "+item, e);
            }
        }
        
        IMPersona persona = getRequestedPersona(zsc);
        synchronized (persona.getLock()) {
            persona.setPrivacyList(pl);
        }
        
        return zsc.createElement(IMConstants.IM_SET_PRIVACY_LIST_RESPONSE);
    }
}
