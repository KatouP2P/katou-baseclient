package ga.nurupeaches.kato.network.manager.tcp;

import ga.nurupeaches.katou.KatouClient;
import ga.nurupeaches.katou.network.Peer;
import ga.nurupeaches.katou.network.manager.tcp.TCPNetworkManager;
import ga.nurupeaches.katou.network.packets.PacketVersion;
import junit.framework.TestCase;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class TCPNetworkManagerTest extends TestCase {

	private SocketChannel externalConnection;
	private TCPNetworkManager manager;
	private Thread tickingThread;

	@Override
	public void setUp() throws Exception {
		manager = new TCPNetworkManager(6800);

		externalConnection = SocketChannel.open();
		externalConnection.bind(new InetSocketAddress(6801));

		tickingThread = new Thread(() -> {

			while(externalConnection.isConnected()){
				manager.tick();
			}

		});
	}

	@Override
	public void tearDown() throws Exception {
		externalConnection.close();
		manager.requestClosure();
	}

	public void testTick() throws Exception {
		externalConnection.connect(new InetSocketAddress(6800));

		tickingThread.start();

		PacketVersion testPacket = new PacketVersion("KatouClientv0.1");
		Peer peer;

		while((peer = KatouClient.getProtocol().getPeer(externalConnection.getLocalAddress())) == null);

		KatouClient.getProtocol().sendPeerPacket(peer, testPacket);
	}

}