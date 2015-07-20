package ga.nurupeaches.katou.network.peer;

import ga.nurupeaches.serichan.Transmittable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Peer {

    /**
     * Files that the peer has offered.
     */
    public final Map<String, Transmittable> OFFERED = new ConcurrentHashMap<>(1);

    /**
     * The current file we're working on.
     */
    public Transmittable workingFile;

    /**
     * The peer's connection.
     */
    public PeerConnection connection;

}
