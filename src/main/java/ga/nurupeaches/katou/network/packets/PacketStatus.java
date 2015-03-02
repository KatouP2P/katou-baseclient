package ga.nurupeaches.katou.network.packets;

import ga.nurupeaches.katou.network.Metadata;
import ga.nurupeaches.katou.utils.PacketUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Represents a packet that notifies others of a Metadata
 */
public class PacketStatus extends Packet {

	private Metadata metadata;

	public PacketStatus(Metadata metadata){
		this.metadata = metadata;
	}

	@Override
	public void init() {
		metadata = new Metadata();
	}

	public Metadata getMetadata() {
		return metadata;
	}

	@Override
	public void read(ByteBuffer buffer) throws IOException {
		String name = PacketUtils.readString(buffer);
		String hash = PacketUtils.readString(buffer);
//		long size = buffer.getLong();
		metadata.setName(name);
		metadata.setHash(hash);
		metadata.setSize(0);
	}

	@Override
	public void write(ByteBuffer buffer) throws IOException {
		PacketUtils.writeString(metadata.getName(), buffer);
		PacketUtils.writeString(metadata.getHash(), buffer);
		buffer.putLong(metadata.getSize());
	}

	@Override
	public int size(){
		int size = 0;
		size += PacketUtils.stringSize(metadata.getName());
		size += PacketUtils.stringSize(metadata.getHash());
		size += 8; // Long is 8 bytes long.
		return size;
	}

}