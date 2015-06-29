package ga.nurupeaches.katou.network.peer;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeerManager {

    private Map<SocketAddress, Peer> map = new ConcurrentHashMap<>(25);
    private static final PeerManager SINGLETON = new PeerManager();

    private PeerManager(){}

    public static PeerManager get(){
        return SINGLETON;
    }

    public Peer getPeer(SocketAddress address){
        return map.get(address);
    }

    public boolean hasPeer(SocketAddress address){
        return map.containsKey(address);
    }

    public void registerPeer(Peer peer){
        if(peer.connection == null){
            throw new IllegalArgumentException("Peer is not connected!");
        }

        map.put(peer.connection.getAddress(), peer);
    }

}
