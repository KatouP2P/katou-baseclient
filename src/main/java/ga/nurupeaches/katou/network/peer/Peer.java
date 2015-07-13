package ga.nurupeaches.katou.network.peer;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.network.Transmittable;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Peer {

    /**
     * Receving buffer
     */
    public ByteBuffer IN_BUFFER = ByteBuffer.allocate(Configuration.getRecvBufferSize());

    /**
     * Files that the peer has offered.
     */
    public final Map<char[], Transmittable> OFFERED = new ConcurrentHashMap<>(1);

    /**
     * The current file we're working on.
     */
    public Transmittable workingFile;

    /**
     * The peer's connection.
     */
    public PeerConnection connection;

}
