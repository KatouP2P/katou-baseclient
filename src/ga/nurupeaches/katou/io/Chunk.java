package ga.nurupeaches.katou.io;

/**
 * Represents a chunk.
 */
public interface Chunk {

	/**
	 * The default size of a chunk. Measured in KB (kilobytes).
	 */
	public static final int DEFAULT_CHUNK_SIZE = 1000;

	/**
	 * Returns the position for this chunk.
	 * @return The position for file reading/writing.
	 */
	public long getPosition();

	/**
	 * Returns the Id of this chunk.
	 * @return The Id.
	 */
	public int getId();

	/**
	 * Validates the chunk and returns whether or not if it was valid.
	 * Uses CRC32 to validate. Might want to switch to something more reliable and fast like MD5.
	 * @return <code>true</code> if we've successfully validated; <code>false</code> otherwise.
	 */
	public boolean validate(long crc32);

}
