package ga.nurupeaches.kato.network.manager.tcp;

import ga.nurupeaches.kato.KatouClient;
import ga.nurupeaches.kato.network.manager.NetworkManager;
import ga.nurupeaches.kato.network.protocol.Protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

// TODO: Use an UPnP library to punch through.
public class TCPNetworkManager implements NetworkManager {

	private final AtomicBoolean CLOSE_REQUESTED = new AtomicBoolean(false);
	private ServerSocketChannel channel;

	public TCPNetworkManager(int port) throws IOException {
		channel = ServerSocketChannel.open();
		channel.configureBlocking(false);
		channel.bind(new InetSocketAddress(port));
	}

	@Override
	public void tick() {
		if(!CLOSE_REQUESTED.get()){
			try{
				// Accepts any new connection. Doesn't block.
				handleNewConnection(channel.accept());
				peerTick();
			} catch (IOException e) {
				KatouClient.LOGGER.log(Level.SEVERE, "Failed to handle connection!", e);
			}
		}
	}

	public void handleNewConnection(SocketChannel newConnection) throws IOException {
		// Don't process null connections!
		if(newConnection != null){
			KatouClient.LOGGER.log(Level.INFO, "Accepted new TCP connection from " + newConnection.getRemoteAddress());
			Protocol.PROTOCOL.registerPeer(newConnection);
			// TODO: Call event saying a new peer has connected.
		}
	}

	public void peerTick() {
		Protocol.PROTOCOL.getConnectedPeers().values().forEach((peer) -> {

			if(peer.getChannel() instanceof SocketChannel){
				SocketChannel channel = (SocketChannel)peer.getChannel();

				try{
					ByteBuffer buffer = peer.getBuffer();
					if(channel.read(buffer) == 128){
//						return;
					}

					String str = new String(buffer.array()).trim();
					if(!str.isEmpty()){
						System.out.println(str);
					}

//					Packet packet = Packet.convertPacket(buffer.get());
//					packet.setOrigin(channel.getRemoteAddress());
//					packet.read(buffer);
//
//					PacketProcessor.process(packet);

//					buffer.clear();
				} catch (IOException e) {
					KatouClient.LOGGER.log(Level.SEVERE, "Failed to handle peer", e);
				}
			}

		});
	}

	@Override
	public AbstractSelectableChannel getChannel() {
		return channel;
	}

	@Override
	public void requestClosure() {
		CLOSE_REQUESTED.compareAndSet(false, true);
	}

}