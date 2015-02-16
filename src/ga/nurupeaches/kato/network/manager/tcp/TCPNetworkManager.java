package ga.nurupeaches.kato.network.manager.tcp;

import ga.nurupeaches.kato.KatouClient;
import ga.nurupeaches.kato.network.Peer;
import ga.nurupeaches.kato.network.manager.NetworkManager;
import ga.nurupeaches.kato.network.packets.Packet;
import ga.nurupeaches.kato.network.protocol.Protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.charset.StandardCharsets;
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

			// Read it in a raw way; there's no point in creating a new ByteBuffer for just one integer.
			int size = newConnection.socket().getInputStream().read();

			// Initialize a buffer to retrieve the length of the version name.
			ByteBuffer buffer = ByteBuffer.allocate(size);
			newConnection.read(buffer);

			Peer peer = Protocol.PROTOCOL.registerPeer(newConnection);
			// Finally, convert the bytes from the buffer to a string and give it to the new peer.
			peer.setVersion(new String(buffer.array(), StandardCharsets.UTF_8));

			// TODO: Call event saying a new peer has connected.
		}
	}

	public void peerTick() {
		Protocol.PROTOCOL.getConnectedPeers().values().parallelStream().forEach((peer) -> {

			if(peer.getChannel() instanceof SocketChannel){
				SocketChannel channel = (SocketChannel)peer.getChannel();
				try{
					ByteBuffer buffer = peer.getBuffer();
					channel.read(buffer);

					Packet packet = Packet.convertPacket(buffer.get());
					packet.read(buffer);

					// TODO: Process packet data

					buffer.clear();
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