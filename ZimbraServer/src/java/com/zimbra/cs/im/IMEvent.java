/*
 * 
 */
package com.zimbra.cs.im;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;

public abstract class IMEvent implements Runnable {
    
    protected List<IMAddr> mTargets;
    
    protected IMEvent(IMAddr target) {
        mTargets = new ArrayList<IMAddr>(1);
        mTargets.add(target);
    }
    
    /**
     * Asynchronous IM event which gets run by the IM Router thread.
     * 
     * To avoid deadlocks, Events MUST NOT hold more than one mailbox lock
     * at any time. 
     * 
     * @throws ServiceException
     */
    public void run() {
        for (IMAddr addr : mTargets) {
            try {
                if (addr.getAddr().indexOf('@') > 0) {
                    IMPersona persona  = IMRouter.getInstance().findPersona(null, addr);
                    if (persona != null) {
                        synchronized (persona.getLock()) {
                            handleTarget(persona);
                        }
                    } else {
                        ZimbraLog.im.debug("Ignoring IMEvent for "+addr.toString()+" (could not find Mailbox): "+
                                    this.toString());
                    }
                } else {
                    ZimbraLog.im.debug("Ignoring IMEvent for "+addr.toString()+" (addr has no domain): "+
                                this.toString());
                }
            } catch (Exception e) {
                ZimbraLog.im.debug("Caught exception running event: "+this+" except="+e);
                e.printStackTrace();
            }
        }
    }
        
    protected void handleTarget(IMPersona persona) throws ServiceException {}
}
