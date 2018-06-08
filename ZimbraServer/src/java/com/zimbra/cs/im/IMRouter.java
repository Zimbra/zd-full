/*
 * 
 */
package com.zimbra.cs.im;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.wildfire.PacketRouter;
import org.jivesoftware.wildfire.XMPPServer;
import org.xmpp.packet.Packet;

import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.common.service.ServiceException;

public class IMRouter {
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor(); 
    private static final IMRouter sRouter = new IMRouter();
    private IMRouter() { }
    
    public static IMRouter getInstance() { return sRouter; }

    /**
     * @param octxt
     * @param addr
     * @return
     * @throws ServiceException
     */
    public IMPersona findPersona(OperationContext octxt, IMAddr addr) throws ServiceException 
    {
        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccount(Provisioning.getInstance().get(AccountBy.name, addr.getAddr()));
        
        return mbox.getPersona();
    }
    
    /**
     * Post an event to the router's asynchronous execution queue.  The event
     * will happen at a later time and will be run without any locks held, to
     * avoid deadlock issues.
     * 
     * @param event
     */
    public void postEvent(IMEvent event) {
        mExecutor.execute(event);
    }
    
    private static final class PostPacket implements Runnable {
        Packet mPacket = null;
        PostPacket(Packet packet) { mPacket = packet; }
        public void run() { 
            XMPPServer server = XMPPServer.getInstance();
            if (server != null){
                PacketRouter router = server.getPacketRouter();
                if (router != null)
                    router.route(mPacket);
            }
        }
    }
    
    /**
     * Post an event to the router's asynchronous execution queue.  The event
     * will happen at a later time and will be run without any locks held, to
     * avoid deadlock issues.
     * 
     * @param event
     */
    public void postEvent(Packet packet) {
        mExecutor.execute(new PostPacket(packet)); 
    }
    
    /**
     * 
     */
    public void shutdown() {
        mExecutor.shutdownNow();
    }
}
