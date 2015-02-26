package ga.nurupeaches.katou.network;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.network.manager.udp.UDPNetworkManager;
import ga.nurupeaches.katou.network.packets.PacketStatus;
import ga.nurupeaches.katou.network.packets.PacketVersion;
import org.junit.Test;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

public class UDPNetworkManagerTest {

	private UDPNetworkManager manager;

	@Test
	public void testTick() throws Exception {
		Configuration.loadDefaults();
		manager = new UDPNetworkManager();
		Thread networkThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					manager.tick();
				}
			}
		});

		networkThread.start();

		DatagramSocket externalSocket = new DatagramSocket();
		externalSocket.connect(new InetSocketAddress("localhost", 6800));

		System.out.println("TRYING RANDOM UDP PACKET SENDING - PLEASE WAIT WARMLY FOR CELEBRATION :^)");

		SecureRandom srng = new SecureRandom();
		Random random = new Random();
		Metadata metadata = new Metadata();
		PacketStatus statusPacket = new PacketStatus(metadata);
		PacketVersion versionPacket;
		ByteBuffer buffer;
		DatagramPacket packet = new DatagramPacket(new byte[0], 0);
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

			packet.setData(buffer.array());
			packet.setLength(buffer.array().length);
			externalSocket.send(packet);
		}

		Thread.sleep(1000);
	}

	// Not a list of waifus, I swear!
	private String[] randomStrings = {
			"Sejuani", "Tenshi", "Frau", "Xenovia", "Katou", "Aqua", "Yurippe", "Origami",
			"Tohsaka", "Pepperoni", "Schokolade", "Sarasvati", "Maki", "Chizuru", "Galil"
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

}