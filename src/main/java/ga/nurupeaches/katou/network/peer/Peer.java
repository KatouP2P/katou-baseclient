package ga.nurupeaches.katou.network.peer;

import ga.nurupeaches.katou.Configuration;

import java.nio.ByteBuffer;

public class Peer {

    public ByteBuffer outBuffer = ByteBuffer.allocate(Configuration.getSendBufferSize());
    public ByteBuffer inBuffer = ByteBuffer.allocate(Configuration.getRecvBufferSize());
    public PeerConnection connection;

}
