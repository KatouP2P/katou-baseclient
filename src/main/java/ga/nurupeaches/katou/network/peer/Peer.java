package ga.nurupeaches.katou.network.peer;

import ga.nurupeaches.katou.network.Transmittable;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Peer {


    public ByteBuffer IN_BUFFER;

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
