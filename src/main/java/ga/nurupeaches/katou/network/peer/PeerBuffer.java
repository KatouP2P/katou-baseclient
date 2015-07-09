package ga.nurupeaches.katou.network.peer;

import ga.nurupeaches.katou.Configuration;

import java.nio.ByteBuffer;
import java.util.concurrent.CopyOnWriteArrayList;

public class PeerBuffer {

    // this kills the performance
    private final CopyOnWriteArrayList<ByteBuffer> buffers = new CopyOnWriteArrayList<>();

    public PeerBuffer(){
        // Add a single buffer
        buffers.add(ByteBuffer.allocate(Configuration.getRecvBufferSize()));
    }



}
