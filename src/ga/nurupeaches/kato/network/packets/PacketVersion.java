package ga.nurupeaches.kato.network.packets;

import ga.nurupeaches.kato.utils.PacketUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PacketVersion extends Packet {

	private String version;

	public PacketVersion(String version){
		this.version = version;
	}

	public PacketVersion(){}

	public String getVersion(){
		return version;
	}

	@Override
	public void read(ByteBuffer buffer){
		version = PacketUtils.readString(buffer);
	}

	@Override
	public void write(ByteBuffer buffer){
		PacketUtils.writeString(version, buffer);
	}

	@Override
	public int size(){
		return version.getBytes(StandardCharsets.UTF_8).length;
	}

}