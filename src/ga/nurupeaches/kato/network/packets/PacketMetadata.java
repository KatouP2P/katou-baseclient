package ga.nurupeaches.kato.network.packets;

import ga.nurupeaches.kato.Configuration;
import ga.nurupeaches.kato.network.KatouFile;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class PacketMetadata extends Packet {

	private KatouFile file;

	public PacketMetadata(KatouFile file){
		this.file = file;
	}

	public void read(ByteBuffer buffer){
		byte[] nameBytes = new byte[buffer.getInt()];
		buffer.get(nameBytes);

		String name = new String(nameBytes, StandardCharsets.UTF_8);
		long size = buffer.getLong();

		file = new KatouFile(name, Paths.get(Configuration.getNode("defaultSaveLocation"), name), size);
	}

	public void write(ByteBuffer buffer){
		buffer.putInt(file.getName().length());
		buffer.put(file.getName().getBytes(StandardCharsets.UTF_8));
		buffer.putLong(file.getSize());
	}

	public KatouFile getFile(){
		return file;
	}

}