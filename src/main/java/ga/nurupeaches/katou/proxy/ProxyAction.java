package ga.nurupeaches.katou.proxy;

import ga.nurupeaches.katou.network.peer.Peer;

import java.lang.ref.SoftReference;

public interface ProxyAction {

    public SoftReference<Peer> getOrigin();

}
