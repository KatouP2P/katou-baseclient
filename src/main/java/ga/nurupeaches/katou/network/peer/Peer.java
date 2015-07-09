package ga.nurupeaches.katou.network.peer;

import ga.nurupeaches.katou.Configuration;

import java.nio.ByteBuffer;

public class Peer {

    public final ByteBuffer IN_BUFFER = ByteBuffer.allocate(Configuration.getRecvBufferSize());
    public PeerConnection connection;

}
