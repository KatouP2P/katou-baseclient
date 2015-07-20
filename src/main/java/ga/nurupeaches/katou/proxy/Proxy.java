package ga.nurupeaches.katou.proxy;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.network.peer.iochannel.IOChannel;
import ga.nurupeaches.katou.network.peer.iochannel.TCPChannel;
import ga.nurupeaches.katou.network.peer.iochannel.UDPChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.DatagramChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Proxy {

    public static final Logger PROXY_LOGGER = Logger.getLogger("Katou-Proxy");

    // Keys are peers that are using us as a proxy.
    // Values are the peers that the keys are targeting.
    private final Map<Peer, Session> activeSessions = new ConcurrentHashMap<>(5);

    public Proxy(){}

    public void newProxySession(ProxyRequest request){
        if(request.getOrigin().get() == null) throw new IllegalStateException("Peer expired and was GC'd");
        // TODO: Implement black-list.

        SocketAddress address = request.getDest();
        Session session = new Session();
        IOChannel channel = null;

        if(request.isUDP()){
            DatagramChannel bridge = null;
            try {
                bridge = DatagramChannel.open();
                bridge.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                bridge.bind(new InetSocketAddress(Configuration.getPort()));
                channel = new UDPChannel(bridge, address);
            } catch (IOException e){
                PROXY_LOGGER.log(Level.WARNING, "Failed to create proxy session for peer", e);

                if(bridge != null){
                    try {
                        bridge.close();
                    } catch (IOException e1){
                        PROXY_LOGGER.log(Level.SEVERE, "Failed to close proxy channel", e1);
                    }
                }
            }
        } else {
            AsynchronousSocketChannel bridge = null;
            try {
                bridge = AsynchronousSocketChannel.open();
                bridge.connect(address);
                channel = new TCPChannel(bridge);
            } catch (IOException e){
                PROXY_LOGGER.log(Level.WARNING, "Failed to create proxy session for peer", e);

                if(bridge != null){
                    try {
                        bridge.close();
                    } catch (IOException e1){
                        PROXY_LOGGER.log(Level.SEVERE, "Failed to close proxy channel", e1);
                    }
                }
            }
        }

        if(channel == null) throw new IllegalArgumentException("Proxy channel creation failed");
        session.channel = channel;
        activeSessions.put(request.getOrigin().get(), session);
    }

    public void forwardData(ProxyForwardData toProxy){
        Session session;
        if(toProxy.getOrigin().get() != null && (session = activeSessions.get(toProxy.getOrigin().get())) != null){
            try {
                session.channel.write(toProxy.getData());
            } catch (IOException e){
                PROXY_LOGGER.log(Level.WARNING, "Failed to transfer proxy data", e);
            }
        }
    }

}
