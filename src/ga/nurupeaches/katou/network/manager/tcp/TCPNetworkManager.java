package ga.nurupeaches.katou.network.manager.tcp;

import ga.nurupeaches.katou.KatouClient;
import ga.nurupeaches.katou.network.manager.NetworkManager;
import ga.nurupeaches.katou.network.packets.Packet;
import ga.nurupeaches.katou.network.packets.PacketProcessor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

// TODO: Use an UPnP library to punch through.
public class TCPNetworkManager implements NetworkManager {

	private final AtomicBoolean CLOSE_REQUESTED = new AtomicBoolean(false);
	private AsynchronousServerSocketChannel channel;

	public TCPNetworkManager(int port) throws IOException {
		channel = AsynchronousServerSocketChannel.open();
		channel.bind(new InetSocketAddress(port));
	}

	@Override
	public void tick() {
		if(!CLOSE_REQUESTED.get()){
			try{
				// Accepts any new connection. Blocks.
				handleNewConnection(channel.accept());
				peerTick();
			} catch (IOException e) {
				KatouClient.LOGGER.log(Level.SEVERE, "Failed to handle connection!", e);
			}
		}
	}

	public void handleNewConnection(Future<AsynchronousSocketChannel> futureConnection) throws IOException, InterruptedException, ExecutionException {
		AsynchronousSocketChannel conn = futureConnection.get();
		KatouClient.LOGGER.log(Level.INFO, "Accepted new TCP connection from " + conn.getRemoteAddress() + ".");
		KatouClient.getProtocol().registerPeer(conn);
		// TODO: Call event saying a new peer has connected.
	}

	public void peerTick() {
		KatouClient.getProtocol().getConnectedPeers().values().forEach((peer) -> {

			if(peer.getChannel() instanceof SocketChannel){
				SocketChannel channel = (SocketChannel)peer.getChannel();

				try{
					ByteBuffer buffer = peer.getBuffer();
					buffer.clear();
					int readBytes = channel.read(buffer);
					if(readBytes == 0){
						return;
					}

					Packet packet = Packet.convertPacket(buffer.get());
					packet.setOrigin(channel.getRemoteAddress());
					packet.read(buffer);
					PacketProcessor.process(packet);
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