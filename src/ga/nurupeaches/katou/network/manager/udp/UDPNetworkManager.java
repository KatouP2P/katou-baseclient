package ga.nurupeaches.katou.network.manager.udp;

import ga.nurupeaches.katou.KatouClient;
import ga.nurupeaches.katou.network.manager.NetworkManager;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class UDPNetworkManager implements NetworkManager {

	private final AtomicBoolean CLOSE_REQUESTED = new AtomicBoolean(false);
	private DatagramSocket socket;

	public UDPNetworkManager(int port){
		try{
			socket = new DatagramSocket(port);
		} catch (IOException e){
			KatouClient.LOGGER.log(Level.SEVERE, "Failed to open a new UDP socket!", e);
		}
	}

	@Override
	public void tick(){
		// TODO: Find out how the hell to get sockets from users ;_;
	}

	@Override
	public void requestClosure(){
		CLOSE_REQUESTED.compareAndSet(false, true);
	}

}