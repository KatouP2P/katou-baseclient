package ga.nurupeaches.katou.network.manager;

public interface NetworkManager {

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
