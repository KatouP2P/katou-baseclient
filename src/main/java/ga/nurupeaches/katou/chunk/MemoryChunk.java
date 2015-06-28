package ga.nurupeaches.katou.chunk;

import ga.nurupeaches.katou.network.peer.Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * Represents a chunk of a file in memory.
 */
public class MemoryChunk extends RepresentableChunk {

	/**
	 * The buffer for the data downloaded/read from a peer or file.
	 */
	private ByteBuffer buffer;

	/**
	 * Constructs a MemoryChunk with the given size.
	 * @param id The ID of this chunk.
	 * @param size Size of chunk in bytes
	 */
	public MemoryChunk(int id, int size){
		super(id, size);
		this.buffer = ByteBuffer.allocateDirect(size); // Direct buffers are more efficient at I/O.
	}

	/**
	 * Gets the current buffer.
	 * @return The buffer.
	 */
	public ByteBuffer getBuffer(){
		return buffer;
	}

	@Override
	public void transferFrom(Peer peer) throws IOException{
		peer.connection.readBytes(buffer, getSize());
	}

	@Override
	public void transferTo(Peer peer) throws IOException{
		peer.connection.writeBytes(buffer, getSize());
	}

	@Override
	public boolean validate(long crc32){
		CRC32 checksum = new CRC32();
		// THIS WILL ERROR: TODO: DO SOMETHING
		checksum.update(buffer);
		return checksum.getValue() == crc32;
	}

}