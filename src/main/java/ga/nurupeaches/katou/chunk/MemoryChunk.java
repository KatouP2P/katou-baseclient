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
	private ByteBuffer data;

	/**
	 * Constructs a MemoryChunk with the given size.
	 * @param id The ID of this chunk.
	 * @param size Size of chunk in bytes
	 */
	public MemoryChunk(int id, int size){
		super(id, size);
		this.data = ByteBuffer.allocateDirect(size); // Direct buffers are more efficient at I/O.
	}

	/**
	 * Gets the current data.
	 * @return The data.
	 */
	public ByteBuffer getData(){
		return data;
	}

	@Override
	public void transferFrom(Peer peer) throws IOException{
		ByteBuffer len = ByteBuffer.allocate(Integer.BYTES);
		peer.connection.recv(len);

		this.data = ByteBuffer.allocateDirect(data.getInt());
		peer.connection.recv(this.data);
	}

	@Override
	public void transferTo(Peer peer) throws IOException{
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + getSize());
		buffer.putInt(getSize());
		buffer.put(this.data);

		peer.connection.send(buffer);
	}

	@Override
	public boolean validate(long crc32){
		CRC32 checksum = new CRC32();
		checksum.update(data);
		return checksum.getValue() == crc32;
	}

}