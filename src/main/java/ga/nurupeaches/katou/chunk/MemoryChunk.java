package ga.nurupeaches.katou.chunk;

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

//	@Override
//	public void transferFrom(Peer peer) throws IOException{
//		setId(peer.IN_BUFFER.getInt());
//		setSize(peer.IN_BUFFER.getInt());
//		data = ByteBuffer.allocateDirect(getSize());
//        data.put(Arrays.copyOfRange(peer.IN_BUFFER.array(), Integer.BYTES * 2, getSize()));
//	}
//
//	@Override
//	public void transferTo(Peer peer) throws IOException{
//		ByteBuffer buffer = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES * 3 + getSize());
//        buffer.put((byte)0x01);
//        buffer.putInt(getSize() + Integer.BYTES * 2);
//		buffer.putInt(getId());
//		buffer.putInt(getSize());
//		buffer.put(data);
//        peer.connection.send(buffer);
//	}

	@Override
	public boolean validate(long crc32){
		CRC32 checksum = new CRC32();
		checksum.update(data);
		return checksum.getValue() == crc32;
	}


    @Override
    public String toString(){
        return "MemoryChunk{id=" + getId() + ",size=" + getSize() + '}';
    }
}