package ga.nurupeaches.katou.network.manager.udp;

import ga.nurupeaches.katou.KatouClient;
import ga.nurupeaches.katou.network.manager.NetworkManager;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class UDPNetworkManager implements NetworkManager {

	private final AtomicBoolean CLOSE_REQUESTED = new AtomicBoolean(false);
	private DatagramChannel channel;

	public UDPNetworkManager(int port){
		try{
			channel = DatagramChannel.open();
			channel.configureBlocking(false);
		} catch (IOException e){
			KatouClient.LOGGER.log(Level.SEVERE, "Failed to open a new UDP channel!", e);
		}
	}

	@Override
	public void tick(){
		// TODO: Find out how the hell to get channels from users ;_;
	}

	@Override
	public void requestClosure(){
		CLOSE_REQUESTED.compareAndSet(false, true);
	}

}