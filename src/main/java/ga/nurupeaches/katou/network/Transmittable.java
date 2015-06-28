package ga.nurupeaches.katou.network;

import ga.nurupeaches.katou.network.peer.Peer;

import java.io.IOException;

public interface Transmittable {

    public void transferTo(Peer peer) throws IOException;

    public void transferFrom(Peer peer) throws IOException;

}
