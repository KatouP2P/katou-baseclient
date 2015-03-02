package ga.nurupeaches.katou.network.packets;

import ga.nurupeaches.katou.io.Chunk;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PacketChunk extends Packet {

	private Chunk chunk;

	@Override
	public void init(){

	}

	@Override
	public int size(){
		return 4 + 8 + (Chunk.DEFAULT_CHUNK_SIZE * 1000); // Length of chunk, position to start at, data.
	}

	@Override
	public void read(ByteBuffer buffer) throws IOException {

	}

	@Override
	public void write(ByteBuffer buffer) throws IOException {

	}

}