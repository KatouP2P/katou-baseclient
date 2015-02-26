package ga.nurupeaches.katou.network.manager.udp;

import ga.nurupeaches.katou.KatouClient;
import ga.nurupeaches.katou.network.manager.NetworkManager;
import ga.nurupeaches.katou.network.manager.SocketWrapper;
import ga.nurupeaches.katou.network.packets.Packet;
import ga.nurupeaches.katou.network.packets.PacketProcessor;
import ga.nurupeaches.katou.network.protocol.Protocol;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class UDPNetworkManager implements NetworkManager {

	private final AtomicBoolean CLOSE_REQUESTED = new AtomicBoolean(false);
	private DatagramChannel serverSocket;
    private ByteBuffer buffer = ByteBuffer.allocate(NetworkManager.DEFAULT_BUFFER_SIZE);
    
	public UDPNetworkManager(){
		try{
			serverSocket = DatagramChannel.open();
            serverSocket.socket().setReuseAddress(true);
            serverSocket.socket().bind(NetworkManager.BIND_ADDRESS);
			serverSocket.configureBlocking(false);
		} catch (IOException e){
			KatouClient.LOGGER.log(Level.SEVERE, "Failed to open a new UDP socket!", e);
		}
	}

	@Override
	public void tick(){
		if(!CLOSE_REQUESTED.get()){
			try{
				// Accepts any new connection and processes data. Doesn't block.
				checkData();
			} catch (IOException e) {
				KatouClient.LOGGER.log(Level.SEVERE, "Failed to handle connection!", e);
			}
		}
	}

	public void checkData() throws IOException {
        SocketAddress address = serverSocket.receive(buffer);
        if(address == null){ // No data recieved.
            return;
        }
        
        Protocol protocol = KatouClient.getProtocol();
        if(protocol.getPeer(address) == null){ // Probably easy to DoS clients now. TODO: Fix DoS vuln!
            protocol.registerPeer(new SocketWrapper(address, serverSocket));
        }

        buffer.flip();

        Packet packet = Packet.convertPacket(buffer.get());
        packet.setOrigin(protocol.getPeer(address).getSocket().getAddress());
        packet.read(buffer);

        buffer.compact();
        PacketProcessor.process(packet);
	}

	@Override
	public void requestClosure(){
		CLOSE_REQUESTED.compareAndSet(false, true);
	}

}