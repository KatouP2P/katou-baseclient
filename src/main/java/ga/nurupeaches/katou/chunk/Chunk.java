package ga.nurupeaches.katou.chunk;

import ga.nurupeaches.katou.network.Transmittable;

/**
 * Represents a chunk.
 */
public interface Chunk extends Transmittable {

	/**
	 * Returns the Id of this chunk.
	 * @return The Id.
	 */
	public int getId();

	public long getSize();

	/**
	 * Validates the chunk and returns whether or not if it was valid.
	 * Uses CRC32 to validate. Might want to switch to something more reliable and fast like MD5.
	 * @return <code>true</code> if we've successfully validated; <code>false</code> otherwise.
	 */
	public boolean validate(long crc32);

}