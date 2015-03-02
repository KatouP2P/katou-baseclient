package ga.nurupeaches.katou.network.manager.tcp;

import ga.nurupeaches.katou.KatouClient;
import ga.nurupeaches.katou.network.Peer;
import ga.nurupeaches.katou.network.manager.NetworkManager;
import ga.nurupeaches.katou.network.manager.SocketType;
import ga.nurupeaches.katou.network.manager.SocketWrapper;
import ga.nurupeaches.katou.network.packets.Packet;
import ga.nurupeaches.katou.network.packets.PacketProcessor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

// TODO: Use an UPnP library to punch through.
public class TCPNetworkManager implements NetworkManager {

	private final AtomicBoolean CLOSE_REQUESTED = new AtomicBoolean(false);
	private ServerSocketChannel serverSocket;

	public TCPNetworkManager() throws IOException {
		serverSocket = ServerSocketChannel.open();
		serverSocket.socket().bind(NetworkManager.BIND_ADDRESS);
		serverSocket.configureBlocking(false);
	}

	@Override
	public void tick(){
		if(!CLOSE_REQUESTED.get()){
			try{
				// Accepts any new connection. Doesn't block.
				handleNewConnection(serverSocket.accept());
				peerTick();
			} catch (IOException e) {
				KatouClient.LOGGER.log(Level.SEVERE, "Failed to handle connection!", e);
			}
		}
	}

	public void handleNewConnection(SocketChannel socket) throws IOException {
		if(socket != null){
			KatouClient.getProtocol().registerPeer(new SocketWrapper(socket));
//			KatouClient.LOGGER.log(Level.INFO, "Accepted new TCP connection from " + socket.socket().getRemoteSocketAddress() + ".");
			socket.configureBlocking(false);
			// TODO: Call event saying a new peer has connected.
		}
	}

	public void peerTick(){
		Iterator<Peer> peers = KatouClient.getProtocol().getConnectedPeers().values().iterator();
		while(peers.hasNext()){
			Peer peer = peers.next();

			if(peer.getSocket().getType() == SocketType.TCP){
				SocketChannel socket = (SocketChannel)peer.getSocket().getRawSocket();

				try {
					ByteBuffer buffer = peer.getBuffer();

					int read = socket.read(buffer);
					if(read == 0){ // No data read.
						continue;
					} else if(read == -1){ // Dead/disconnected stream.
						peers.remove();
					}

					buffer.flip();

					// According to TCPNMTest, this is supposed to be 157.
					// This is returning 152. java pls.
					// I"m such an idiot; I needed to allocate 5 extra bytes.
					int packetSize = buffer.getInt();
					if(packetSize > buffer.capacity()){
//						KatouClient.LOGGER.log(Level.INFO, "Received packet larger than buffer. Got " + packetSize);

						ByteBuffer extendedBuffer = ByteBuffer.allocate(packetSize + 5);
						extendedBuffer.put((ByteBuffer)buffer.position(0));

						int extRead = socket.read(extendedBuffer);
						extendedBuffer.flip();

						if(extRead == 0){
							KatouClient.LOGGER.log(Level.WARNING, "Client sent a packet larger than buffer but " +
									"never sent the rest of the packet.");
						} else if(extRead == -1){
							KatouClient.LOGGER.log(Level.WARNING, "Client disconnected midway into I/O operation.");
							peers.remove();
						}

						buffer = extendedBuffer;
					}

					buffer.position(4);
					handleBuffer(buffer, peer);

					if(buffer != peer.getBuffer()){ // We passed an extended buffer; fix the old one!
						peer.getBuffer().compact();
					}
				} catch (IOException e){
					// We still want to process the other peers without halting execution.
					KatouClient.LOGGER.log(Level.SEVERE, "Failed to handle peer", e);
				}
			}
		}
	}

	public void handleBuffer(ByteBuffer buffer, Peer peer) throws IOException {
		Packet packet = Packet.convertPacket(buffer.get());
		packet.setOrigin(peer.getSocket().getAddress());
		packet.read(buffer);

		buffer.rewind();
		PacketProcessor.process(packet);
	}

	@Override
	public void requestClosure(){
		CLOSE_REQUESTED.compareAndSet(false, true);
	}

}