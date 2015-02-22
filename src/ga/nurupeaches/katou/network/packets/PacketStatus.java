package ga.nurupeaches.katou.network.packets;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.network.KatouMetadata;
import ga.nurupeaches.katou.utils.PacketUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Represents a packet that notifies others of a KatouMetadata
 */
public class PacketStatus extends Packet {

	private KatouMetadata metadata;

	public PacketStatus(KatouMetadata metadata){
		this.metadata = metadata;
	}

	@Override
	public void init() {
		metadata = new KatouMetadata();
	}

	public KatouMetadata getMetadata() {
		return metadata;
	}

	@Override
	public void read(ByteBuffer buffer) throws IOException {
		String name = PacketUtils.readString(buffer);
		String hash = PacketUtils.readString(buffer);
		long size = buffer.getLong();

		metadata.setName(name);
		metadata.setHash(hash);
		metadata.setSize(size);
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
		size += metadata.getName().getBytes(Configuration.getCharset()).length;
		size += metadata.getHash().getBytes(Configuration.getCharset()).length;
		size += 8; // Long is 8 bytes long.
		size += (4 * 2); // Integer is 4 bytes long.
		return size;
	}

}