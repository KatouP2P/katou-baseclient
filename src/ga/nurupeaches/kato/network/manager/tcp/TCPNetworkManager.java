package ga.nurupeaches.kato.network.manager.tcp;

import ga.nurupeaches.kato.KatouClient;
import ga.nurupeaches.kato.network.Peer;
import ga.nurupeaches.kato.network.manager.NetworkManager;
import ga.nurupeaches.kato.network.protocol.Message;
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
	public void tick(){
		if(!CLOSE_REQUESTED.get()){
			try{
				// Accept the new connection. Doesn't block.
				SocketChannel newConnection = channel.accept();

				// Don't process null connections!
				if(newConnection == null){
					return;
				}

				KatouClient.LOGGER.log(Level.INFO, "Accepted new TCP connection from " + newConnection.getRemoteAddress());

				// Initialize a buffer to retrieve the length of the version name.
				ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
				newConnection.read(buffer);
				// Reasign the buffer variable to a buffer for the version string.
				buffer = ByteBuffer.allocate(buffer.getInt());
				newConnection.read(buffer);

				Peer peer = Protocol.PROTOCOL.registerPeer(newConnection.getRemoteAddress());
				// Finally, convert the bytes from the buffer to a string and give it to the new peer.
				peer.setVersion(new String(buffer.array(), StandardCharsets.UTF_8));

				Protocol.PROTOCOL.sendPeerMessage(peer, Message.REQUEST_STATUS);
			} catch (IOException e){
				KatouClient.LOGGER.log(Level.SEVERE, "Failed to handle connection!", e);
			}
		}
	}

	@Override
	public AbstractSelectableChannel getChannel(){
		return channel;
	}

	@Override
	public void requestClosure(){
		CLOSE_REQUESTED.compareAndSet(false, true);
	}

}