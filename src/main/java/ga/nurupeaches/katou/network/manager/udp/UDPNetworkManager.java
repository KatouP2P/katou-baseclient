package ga.nurupeaches.katou.network.manager.udp;

import ga.nurupeaches.katou.KatouClient;
import ga.nurupeaches.katou.network.Peer;
import ga.nurupeaches.katou.network.manager.NetworkManager;
import ga.nurupeaches.katou.network.manager.SocketType;
import ga.nurupeaches.katou.network.manager.SocketWrapper;
import ga.nurupeaches.katou.network.packets.Packet;
import ga.nurupeaches.katou.network.packets.PacketProcessor;
import ga.nurupeaches.katou.network.protocol.Protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class UDPNetworkManager implements NetworkManager {

	private final AtomicBoolean CLOSE_REQUESTED = new AtomicBoolean(false);
	private DatagramChannel serverSocket;
    private ByteBuffer buffer = ByteBuffer.allocate(128);
    
	public UDPNetworkManager(int port){
		try{
			serverSocket = DatagramChannel.open();
			serverSocket.socket().bind(new InetSocketAddress(port));
			serverSocket.configureBlocking(false);
		} catch (IOException e){
			KatouClient.LOGGER.log(Level.SEVERE, "Failed to open a new UDP socket!", e);
		}
	}

	@Override
	public void tick(){
		if(!CLOSE_REQUESTED.get()){
			try{
				// Accepts any new connection. Doesn't block.
				checkData();
				peerTick();
			} catch (IOException e) {
				KatouClient.LOGGER.log(Level.SEVERE, "Failed to handle connection!", e);
			}
		}
	}

	public void checkData() throws IOException {
        SocketAddress address = serverSocket.receive(buffer);
        if(address == null){ // No data recieved.
            return;
        } else {
			System.out.println(address);
		}
        
        Protocol protocol = KatouClient.getProtocol();
        if(protocol.getPeer(address) == null){ // Probably easy to DoS clients now. TODO: Fix DoS vuln!
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.connect(address);
            protocol.registerPeer(new SocketWrapper(channel));
        }
	}

	public void peerTick(){
	    Iterator<Peer> peers = KatouClient.getProtocol().getConnectedPeers().values().iterator();
		while(peers.hasNext()){
			Peer peer = peers.next();

			if(peer.getSocket().getType() == SocketType.UDP){
			    DatagramChannel socket = (DatagramChannel)peer.getSocket().getRawSocket();

				try {
					ByteBuffer buffer = peer.getBuffer();

					int read = socket.read(buffer);
					if(read == 0){ // No data read.
						continue;
					} else if(read == -1){ // Dead/disconnected stream.
						peers.remove();
					}

					buffer.flip();

					Packet packet = Packet.convertPacket(buffer.get());
					packet.setOrigin(peer.getSocket().getAddress());
					packet.read(buffer);

					buffer.compact();
					PacketProcessor.process(packet);
				} catch (IOException e){
					// We still want to process the other peers without halting execution.
					KatouClient.LOGGER.log(Level.SEVERE, "Failed to handle peer", e);
				}
			}
		}
	}

	@Override
	public void requestClosure(){
		CLOSE_REQUESTED.compareAndSet(false, true);
	}

}