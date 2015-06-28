package ga.nurupeaches.katou.network.peer;

import ga.nurupeaches.katou.Configuration;

import java.nio.ByteBuffer;

public class Peer {

    public ByteBuffer buffer = ByteBuffer.allocate(Configuration.getBufferSize());
    public PeerConnection connection;

}
