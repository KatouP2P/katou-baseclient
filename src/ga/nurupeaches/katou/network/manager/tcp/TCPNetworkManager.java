package ga.nurupeaches.katou.network.manager.tcp;

import ga.nurupeaches.katou.KatouClient;
import ga.nurupeaches.katou.network.Peer;
import ga.nurupeaches.katou.network.manager.NetworkManager;
import ga.nurupeaches.katou.network.manager.SocketType;
import ga.nurupeaches.katou.network.manager.SocketWrapper;
import ga.nurupeaches.katou.network.packets.Packet;
import ga.nurupeaches.katou.network.packets.PacketProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

// TODO: Use an UPnP library to punch through.
public class TCPNetworkManager implements NetworkManager {

	private final AtomicBoolean CLOSE_REQUESTED = new AtomicBoolean(false);
	private ServerSocket serverChannel;

	public TCPNetworkManager(int port) throws IOException {
		serverChannel = new ServerSocket();
		serverChannel.bind(new InetSocketAddress(port));
	}

	@Override
	public void tick() {
		if(!CLOSE_REQUESTED.get()){
			try{
				// Accepts any new connection. Blocks.
				handleNewConnection(serverChannel.accept());
				peerTick();
			} catch (IOException e) {
				KatouClient.LOGGER.log(Level.SEVERE, "Failed to handle connection!", e);
			}
		}
	}

	public void handleNewConnection(Socket socket)  {
		KatouClient.LOGGER.log(Level.INFO, "Accepted new TCP connection from " + socket.getRemoteSocketAddress() + ".");
		// If your IDE complains about the line below, ignore it. We properly validate if it's compatible with T.
		// TODO: Make code that doesn't make IDE weep tears of warnings.
		KatouClient.getProtocol().registerPeer(new SocketWrapper(socket));
		KatouClient.LOGGER.log(Level.INFO, "Accepted new TCP connection from " + socket.getRemoteSocketAddress() + ".");
		// TODO: Call event saying a new peer has connected.
	}

	public void peerTick() {
		Iterator<Peer> peers = KatouClient.getProtocol().getConnectedPeers().values().iterator();
		while(peers.hasNext()){
			Peer peer = peers.next();

			if(peer.getSocket().getType() == SocketType.TCP){
				Socket socket = (Socket)peer.getSocket().getRawSocket();
				if(socket.isClosed()){
					// Remove dead/closed connections from the connected peers list.
					peers.remove();
				}

				InputStream stream;
				try {
					stream = socket.getInputStream();

					// Attempt at "non-blocking" reading.
					int available = stream.available();
					if(available != 0){
						byte[] buffer = new byte[available];
						int read = stream.read(buffer);

						if(read != available){
							KatouClient.LOGGER.log(Level.WARNING, "Read bytes didn't match available bytes");
						}

						ByteBuffer wrapper = ByteBuffer.wrap(buffer); // Create a heap-based buffer.
						Packet packet = Packet.convertPacket(wrapper.get());
						packet.setOrigin(socket.getRemoteSocketAddress());
						packet.read(wrapper);

						PacketProcessor.process(packet);
					}
				} catch (IOException e){
					// We still want to process the other peers without halting execution.
					KatouClient.LOGGER.log(Level.SEVERE, "Failed to handle peer", e);
				}

			}
		}
	}

	@Override
	public void requestClosure() {
		CLOSE_REQUESTED.compareAndSet(false, true);
	}

}