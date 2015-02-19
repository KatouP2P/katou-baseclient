package ga.nurupeaches.kato.network.manager.tcp;

import ga.nurupeaches.katou.KatouClient;
import ga.nurupeaches.katou.network.KatouMetadata;
import ga.nurupeaches.katou.network.Peer;
import ga.nurupeaches.katou.network.manager.tcp.TCPNetworkManager;
import ga.nurupeaches.katou.network.packets.PacketStatus;
import junit.framework.TestCase;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class TCPNetworkManagerTest extends TestCase {

	private SocketChannel externalConnection;
	private TCPNetworkManager manager;

	@Override
	public void setUp() throws Exception {
		manager = new TCPNetworkManager(6800);

		externalConnection = SocketChannel.open();
		externalConnection.bind(new InetSocketAddress(6801));
	}

	@Override
	public void tearDown() throws Exception {
		externalConnection.close();
		manager.requestClosure();
	}

	public void testTick() throws Exception {
		externalConnection.connect(new InetSocketAddress(6800));
		manager.tick();

		PacketStatus testPacket = new PacketStatus(new KatouMetadata().setName("KatouTestFile").setSize(1).setHash("1234567890ABCDEF"));
		Peer peer = KatouClient.getProtocol().getPeer(externalConnection.getLocalAddress());
		KatouClient.getProtocol().sendPeerPacket(peer, testPacket);
		System.out.println(peer.getChannel().getClass().getName());

		manager.tick();
	}

}