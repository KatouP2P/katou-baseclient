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

	public MemoryChunk(){}

	/**
	 * Gets the current data.
	 * @return The data.
	 */
	public ByteBuffer getData(){
		return data;
	}

	@Override
	public void transferFrom(Peer peer) throws IOException{
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2);
		peer.connection.recv(buffer);

		setId(buffer.getInt());
		setSize(buffer.getInt());
		data = ByteBuffer.allocateDirect(getSize());

		peer.connection.recv(this.data);
	}

	@Override
	public void transferTo(Peer peer) throws IOException{
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2);
		buffer.putInt(getId());
		buffer.putInt(getSize());
		peer.connection.send(buffer);
		peer.connection.send(data);
	}

	@Override
	public boolean validate(long crc32){
		CRC32 checksum = new CRC32();
		checksum.update(data);
		return checksum.getValue() == crc32;
	}

}