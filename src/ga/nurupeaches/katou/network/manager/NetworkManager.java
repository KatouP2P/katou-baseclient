package ga.nurupeaches.katou.network.manager;

import java.nio.channels.spi.AbstractSelectableChannel;

public interface NetworkManager {

	/**
	 * The default buffer size, measured in bytes.
	 */
	public static final int DEFAULT_BUFFER_SIZE = 128;

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
