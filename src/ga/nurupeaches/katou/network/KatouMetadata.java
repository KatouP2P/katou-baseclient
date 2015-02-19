package ga.nurupeaches.katou.network;

/**
 * Represents metadata that is associated with a KatouFile
 */
public class KatouMetadata {

	/**
	 * Size of the metadata
	 */
	private long size;

	/**
	 * Name of the metadata
	 */
	private String name;

	/**
	 * A hash of a file.
	 * TODO: Decide on a good hashing alg. Currently looking at SHA256.
	 */
	private String hash;

	public KatouMetadata(){}

	/**
	 * Returns the size of the metadata.
	 * @return A long representing the size.
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Returns the name of the metadata
	 * @return A string representing the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the hash of the metadata.
	 * @return A SHA256 hash of the metadata (subject to change)
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * Sets the file size.
	 * @param size The size of the metadata
	 */
	public KatouMetadata setSize(long size) {
		this.size = size;
		return this;
	}

	/**
	 * Sets the name of the metadata
	 * @param name The name of the metadata
	 */
	public KatouMetadata setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Sets the hash of the metadata
	 * @param hash The hash of the file
	 */
	public KatouMetadata setHash(String hash) {
		this.hash = hash;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		// instanceof statements also check for nulls.
		if(!(obj instanceof KatouMetadata)){
			return false;
		}

		KatouMetadata other = (KatouMetadata)obj;
		return other.name.equals(name) && other.hash.equals(hash) && other.size == size;
	}

}