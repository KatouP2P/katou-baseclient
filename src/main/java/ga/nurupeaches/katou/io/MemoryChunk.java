package ga.nurupeaches.katou.io;

import ga.nurupeaches.katou.network.Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * Represents a chunk of a file in memory.
 */
public class MemoryChunk implements TransferableChunk {

	/**
	 * The buffer for the data downloaded/read from a peer or file.
	 */
	private ByteBuffer buffer;

	/**
	 * Size of this chunk.
	 */
	private int chunkSize;

	/**
	 * The reference point in where the chunk starts.
	 */
	private long chunkStart;

	/**
	 * ID of this chunk.
	 */
	private int id;

	/**
	 * Constructs a FileChunk with the given size.
	 * @param id - The ID of this chunk.
	 * @param size - Size of chunk in KB (kilobytes)
	 */
	public MemoryChunk(int id, int size, long chunkStart){
		this.id = id;
		this.chunkSize = size;
		this.buffer = ByteBuffer.allocateDirect(size); // Direct buffers are more efficient at I/O.
		this.chunkStart = chunkStart;
	}

	/**
	 * Constructs a FileChunk with the default chunk size.
	 * @param id - The ID of this chunk.
	 */
	public MemoryChunk(int id, long chunkStart){
		this(id, DEFAULT_CHUNK_SIZE, chunkStart);
	}

	/**
	 * Gets the current (read-only) buffer.
	 * @return A read-only buffer.
	 */
	public ByteBuffer getBuffer(){
		return buffer.asReadOnlyBuffer();
	}

	@Override
	public long getPosition(){
		return chunkStart;
	}

	@Override
	public int getId(){
		return id;
	}

	@Override
	public void transferFromPeer(Peer peer) throws IOException {

	}

	@Override
	public void transferToPeer(Peer peer) throws IOException {

	}

	@Override
	public boolean validate(long crc32){
		CRC32 checksum = new CRC32();
		// THIS WILL ERROR: TODO: DO SOMETHING
		checksum.update(buffer.array());
		return checksum.getValue() == crc32;
	}

}