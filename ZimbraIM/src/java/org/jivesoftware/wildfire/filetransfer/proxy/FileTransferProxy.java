/*
 * 
 */
package org.jivesoftware.wildfire.filetransfer.proxy;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jivesoftware.util.IMConfig;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.*;
import org.jivesoftware.wildfire.filetransfer.FileTransferManager;
import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.jivesoftware.wildfire.container.BasicModule;
import org.jivesoftware.wildfire.disco.*;
import org.jivesoftware.wildfire.forms.spi.XDataFormImpl;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Manages the transfering of files between two remote entities on the jabber network.
 * This class acts independtly as a Jabber component from the rest of the server, according to
 * the Jabber <a href="http://www.jabber.org/jeps/jep-0065.html">SOCKS5 bytestreams protocol</a>.
 *
 * @author Alexander Wenckus
 */
public class FileTransferProxy extends BasicModule
        implements ServerItemsProvider, DiscoInfoProvider, DiscoItemsProvider,
        RoutableChannelHandler {
    
    /**
     * Whether or not the file transfer proxy is enabled by default.
     */
    public static final boolean DEFAULT_IS_PROXY_ENABLED = true;

    /**
     * The default port of the file transfer proxy
     */
    public static final int DEFAULT_PORT = 7777;

    private String proxyServiceName;

    private IQHandlerInfo info;
    private RoutingTable routingTable;
    private PacketRouter router;
    private String proxyIP;
    private ProxyConnectionManager connectionManager;

    private InetAddress bindInterface;


    public FileTransferProxy() {
        super("SOCKS5 file transfer proxy");

        info = new IQHandlerInfo("query", FileTransferManager.NAMESPACE_BYTESTREAMS);
    }

    public boolean handleIQ(IQ packet) throws UnauthorizedException {
        Element childElement = packet.getChildElement();
        String namespace = null;

        // ignore errors
        if (packet.getType() == IQ.Type.error) {
            return true;
        }
        if (childElement != null) {
            namespace = childElement.getNamespaceURI();
        }

        if ("http://jabber.org/protocol/disco#info".equals(namespace)) {
            try {
                IQ reply = XMPPServer.getInstance().getIQDiscoInfoHandler().handleIQ(packet);
                router.route(reply);
                return true;
            }
            catch (UnauthorizedException e) {
                // Do nothing. This error should never happen
            }
        }
        else if ("http://jabber.org/protocol/disco#items".equals(namespace)) {
            try {
                // a component
                IQ reply = XMPPServer.getInstance().getIQDiscoItemsHandler().handleIQ(packet);
                router.route(reply);
                return true;
            }
            catch (UnauthorizedException e) {
                // Do nothing. This error should never happen
            }
        }
        else if (FileTransferManager.NAMESPACE_BYTESTREAMS.equals(namespace)) {
            if (packet.getType() == IQ.Type.get) {
                IQ reply = IQ.createResultIQ(packet);
                Element newChild = reply.setChildElement("query",
                        FileTransferManager.NAMESPACE_BYTESTREAMS);
                Element response = newChild.addElement("streamhost");
                response.addAttribute("jid", getServiceDomain());
                response.addAttribute("host", proxyIP);
                response.addAttribute("port", String.valueOf(connectionManager.getProxyPort()));
                router.route(reply);
                return true;
            }
            else if (packet.getType() == IQ.Type.set && childElement != null) {
                String sid = childElement.attributeValue("sid");
                JID from = packet.getFrom();
                JID to = new JID(childElement.elementTextTrim("activate"));

                IQ reply = IQ.createResultIQ(packet);
                try {
                    connectionManager.activate(from, to, sid);
                }
                catch (IllegalArgumentException ie) {
                    Log.error("Error activating connection", ie);
                    reply.setType(IQ.Type.error);
                    reply.setError(new PacketError(PacketError.Condition.not_allowed));
                }

                router.route(reply);
                return true;
            }
        }
        return false;
    }

    public IQHandlerInfo getInfo() {
        return info;
    }

    public void initialize(XMPPServer server) {
        super.initialize(server);

        proxyServiceName = IMConfig.XMPP_PROXY_SERVICE_NAME.getString();
        routingTable = server.getRoutingTable();
        router = server.getPacketRouter();

//        // Load the external IP and port information
//        String interfaceName = JiveGlobals.getXMLProperty("network.interface");
//        bindInterface = null;
//        if (interfaceName != null) {
//            if (interfaceName.trim().length() > 0) {
//                try {
//                    bindInterface = InetAddress.getByName(interfaceName);
//                }
//                catch (UnknownHostException e) {
//                    Log.error("Error binding to network.interface", e);
//                }
//            }
//        }

        try {
            proxyIP = IMConfig.XMPP_PROXY_EXTERNALIP.getString();
            if (proxyIP == null || proxyIP.length() == 0)
                proxyIP = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            Log.error("Couldn't discover local host", e);
        }
        
        connectionManager = new ProxyConnectionManager(getFileTransferManager(server));
    }

    private FileTransferManager getFileTransferManager(XMPPServer server) {
        return server.getFileTransferManager();
    }

    public void start() {
        super.start();

        if (isEnabled()) {
            startProxy();
        }
        else {
            XMPPServer.getInstance().getIQDiscoItemsHandler().removeServerItemsProvider(this);
        }
    }

    private void startProxy() {
        connectionManager.processConnections(bindInterface, getProxyPort());
        routingTable.addRoute(getAddress(), this);
        XMPPServer server = XMPPServer.getInstance();

        server.getIQDiscoItemsHandler().addServerItemsProvider(this);
    }

    public void stop() {
        super.stop();

        XMPPServer.getInstance().getIQDiscoItemsHandler()
                .removeComponentItem(getAddress().toString());
        routingTable.removeRoute(getAddress());
        connectionManager.disable();
    }

    public void destroy() {
        super.destroy();

        connectionManager.shutdown();
    }

    /**
     * Returns true if the file transfer proxy is currently enabled and false if it is not.
     *
     * @return Returns true if the file transfer proxy is currently enabled and false if it is not.
     */
    public boolean isProxyEnabled() {
        return (connectionManager.isRunning() && isEnabled());
    }

    private boolean isEnabled() {
        return IMConfig.XMPP_PROXY_ENABLED.getBoolean();        
    }

    /**
     * Returns the port that the file transfer proxy is opertating on.
     *
     * @return Returns the port that the file transfer proxy is opertating on.
     */
    public int getProxyPort() {
        return IMConfig.XMPP_PROXY_PORT.getInt();
    }

    /**
     * Returns the fully-qualifed domain name of this chat service.
     * The domain is composed by the service name and the
     * name of the XMPP server where the service is running.
     *
     * @return the file transfer server domain (service name + host name).
     */
    public String getServiceDomain() {
        return proxyServiceName + "." + XMPPServer.getInstance().getServerInfo().getDefaultName();
    }

    public JID getAddress() {
        return new JID(null, getServiceDomain(), null);
    }

    public Iterator<DiscoServerItem> getItems() {
        List<DiscoServerItem> items = new ArrayList<DiscoServerItem>();
        if(!isEnabled()) {
            return items.iterator();
        }

        items.add(new DiscoServerItem() {
            public String getJID() {
                return getServiceDomain();
            }

            public String getName() {
                return "Socks 5 Bytestreams Proxy";
            }

            public String getAction() {
                return null;
            }

            public String getNode() {
                return null;
            }

            public DiscoInfoProvider getDiscoInfoProvider() {
                return FileTransferProxy.this;
            }

            public DiscoItemsProvider getDiscoItemsProvider() {
                return FileTransferProxy.this;
            }
        });
        return items.iterator();
    }

    public Iterator<Element> getIdentities(String name, String node, JID senderJID) {
        List<Element> identities = new ArrayList<Element>();
        // Answer the identity of the proxy
        Element identity = DocumentHelper.createElement("identity");
        identity.addAttribute("category", "proxy");
        identity.addAttribute("name", "SOCKS5 Bytestreams Service");
        identity.addAttribute("type", "bytestreams");

        identities.add(identity);

        return identities.iterator();
    }

    public Iterator<String> getFeatures(String name, String node, JID senderJID) {
        return Arrays.asList(FileTransferManager.NAMESPACE_BYTESTREAMS,
                "http://jabber.org/protocol/disco#info").iterator();
    }

    public XDataFormImpl getExtendedInfo(String name, String node, JID senderJID) {
        return null;
    }

    public boolean hasInfo(String name, String node, JID senderJID) {
        return true;
    }

    public Iterator<Element> getItems(String name, String node, JID senderJID) {
        // A proxy server has no items
        return new ArrayList<Element>().iterator();
    }

    public void process(Packet packet) throws UnauthorizedException, PacketException {
        // Check if the packet is a disco request or a packet with namespace iq:register
        if (packet instanceof IQ) {
            if (handleIQ((IQ) packet)) {
                // Do nothing
            }
            else {
                IQ reply = IQ.createResultIQ((IQ) packet);
                reply.setChildElement(((IQ) packet).getChildElement().createCopy());
                reply.setError(PacketError.Condition.feature_not_implemented);
                router.route(reply);
            }
        }
    }
}
