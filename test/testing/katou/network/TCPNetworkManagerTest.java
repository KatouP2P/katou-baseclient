package testing.katou.network;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.network.KatouMetadata;
import ga.nurupeaches.katou.network.manager.tcp.TCPNetworkManager;
import ga.nurupeaches.katou.network.packets.PacketStatus;
import ga.nurupeaches.katou.network.packets.PacketVersion;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

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

		System.out.println("test");
		PacketVersion testPacket = new PacketVersion("KatouP2PClient vTest");
		ByteBuffer buffer = ByteBuffer.allocate(testPacket.size() + 1);
		buffer.put(testPacket.getID());
		testPacket.write(buffer);
		externalSocket.getOutputStream().write(buffer.array());
		Thread.sleep(2000);

		System.out.println("test2");
		PacketStatus testPacket2 = new PacketStatus(new KatouMetadata().setName("KatouTestMeta").setSize(1).setHash("1234567890abcdef"));
		ByteBuffer buffer2 = ByteBuffer.allocate(testPacket2.size() + 1);
		buffer2.put(testPacket2.getID());
		testPacket2.write(buffer2);
		externalSocket.getOutputStream().write(buffer2.array());
		Thread.sleep(2000);

		System.out.println(Arrays.toString(networkThread.getStackTrace()));
	}

}