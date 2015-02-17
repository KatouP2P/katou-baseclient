package ga.nurupeaches.kato.network.packets;

import ga.nurupeaches.kato.network.KatouMetadata;
import ga.nurupeaches.kato.utils.PacketUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Represents a packet that notifies others of a KatouMetadata
 */
public class PacketStatus extends Packet {

	private KatouMetadata metadata;

	public PacketStatus(KatouMetadata metadata){
		this.metadata = metadata;
	}

	public PacketStatus() {
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
		size += metadata.getName().getBytes(StandardCharsets.UTF_8).length;
		size += metadata.getHash().getBytes(StandardCharsets.UTF_8).length;
		size += Long.BYTES;
		return size;
	}

}