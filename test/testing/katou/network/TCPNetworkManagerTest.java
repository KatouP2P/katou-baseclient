package testing.katou.network;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.network.Metadata;
import ga.nurupeaches.katou.network.manager.tcp.TCPNetworkManager;
import ga.nurupeaches.katou.network.packets.Packet;
import ga.nurupeaches.katou.network.packets.PacketStatus;
import ga.nurupeaches.katou.network.packets.PacketVersion;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

public class TCPNetworkManagerTest {

	private Socket externalSocket;
	private TCPNetworkManager manager;

	@Test
	public void testTick() throws Exception {
		Configuration.loadDefaults();
		manager = new TCPNetworkManager(6800);
		Thread networkThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					manager.tick();
				}
			}
		});

		networkThread.start();

		externalSocket = new Socket();
		externalSocket.connect(new InetSocketAddress(6800));

		System.out.println("TRYING RANDOM PACKET SENDING - PLEASE WAIT WARMLY FOR CELEBRATION :^)");

		SecureRandom srng = new SecureRandom();
		Random random = new Random();
		Metadata metadata = new Metadata();
		PacketStatus statusPacket = new PacketStatus(metadata);
		PacketVersion versionPacket;
		ByteBuffer buffer;
		for(int i=0; i < 50; i++){
			if(random.nextBoolean()){
				versionPacket = new PacketVersion(randomString(random, 1));

				buffer = ByteBuffer.allocate(versionPacket.size() + 1);
				buffer.put(versionPacket.getID());
				versionPacket.write(buffer);
			} else {
				metadata.setSize(Math.abs(random.nextLong()));
				metadata.setName(randomString(random, 3));
				metadata.setHash(randomHash(srng));

				buffer = ByteBuffer.allocate(statusPacket.size() + 1);
				buffer.put(statusPacket.getID());
				statusPacket.write(buffer);
			}

			externalSocket.getOutputStream().write(buffer.array());
		}

		Thread.sleep(2000);
	}

	private String[] randomStrings = {
		"Sejuani", "Tenshi", "Frau", "Xenovia", "Katou"
	};

	public String randomString(Random random, int length){
		StringBuilder builder = new StringBuilder();

		for(int i=0; i < length; i++){
			builder.append(randomStrings[random.nextInt(randomStrings.length)]);
		}

		return builder.toString();
	}


	public String randomHash(SecureRandom random) {
		return new BigInteger(130, random).toString(16);
	}

	public ByteBuffer packetToBuffer(Packet packet) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(packet.size() + 1); // + 1 for the ID + 4 for packet size (Int)
		buffer.put(packet.getID());
		packet.write(buffer);
		buffer.flip();

		return buffer;
	}

}