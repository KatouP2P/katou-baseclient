package ga.nurupeaches.katou.network.manager;

import ga.nurupeaches.katou.Configuration;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public interface NetworkManager {

    /**
     * The address to bind to. Currently binds only to "localhost".
     */
    public static final SocketAddress BIND_ADDRESS = new InetSocketAddress(Configuration.getPort());

	/**
	 * The default buffer size, measured in bytes.
	 */
	public static final int DEFAULT_BUFFER_SIZE = 128;

	/**
	 * Requests a closure on this NetworkManager.
	 */
	public void requestClosure();

	/**
	 * Ticks the network manager; running it's connection accepting and general instructions.
	 */
	public void tick();

}
