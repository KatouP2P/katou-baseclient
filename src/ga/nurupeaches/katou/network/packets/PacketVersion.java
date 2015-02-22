package ga.nurupeaches.katou.network.packets;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.utils.PacketUtils;

import java.nio.ByteBuffer;

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
		return version.getBytes(Configuration.getCharset()).length + 4;
	}

}