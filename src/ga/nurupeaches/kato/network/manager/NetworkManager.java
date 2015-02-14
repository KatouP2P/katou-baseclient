package ga.nurupeaches.kato.network.manager;

import ga.nurupeaches.kato.io.Chunk;

import java.nio.channels.spi.AbstractSelectableChannel;

public interface NetworkManager {

	/**
	 * The default header size, measured in bytes.
	 */
	public static final int HEADER_SIZE = 1;

	/**
	 * The default buffer size, measured in bytes.
	 */
	public static final int DEFAULT_BUFFER_SIZE = Chunk.DEFAULT_CHUNK_SIZE + HEADER_SIZE;

	/**
	 * Get the channel associated with this NetworkManager
	 * @return The channel.
	 */
	public AbstractSelectableChannel getChannel();

	/**
	 * Requests a closure on this NetworkManager.
	 */
	public void requestClosure();

	/**
	 * Ticks the network manager; running it's connection accepting and general instructions.
	 */
	public void tick();

}
