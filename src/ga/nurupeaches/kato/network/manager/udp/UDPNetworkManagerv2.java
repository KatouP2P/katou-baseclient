package ga.nurupeaches.kato.network.manager.udp;

import ga.nurupeaches.kato.KatouClient;
import ga.nurupeaches.kato.network.manager.NetworkManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class UDPNetworkManagerv2 implements NetworkManager {

	private final AtomicBoolean CLOSE_REQUESTED = new AtomicBoolean(false);
	private DatagramChannel channel;
	private Selector selector;

	public UDPNetworkManagerv2(int port){
		try{
			channel = DatagramChannel.open();
			channel.configureBlocking(false);
			selector = Selector.open();
			channel.bind(new InetSocketAddress(port));
			channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		} catch (IOException e){
			KatouClient.LOGGER.log(Level.SEVERE, "Failed to open a new UDP channel!", e);
		}
	}

	@Override
	public void run(){
		try {
			// Block while we get keys.
			int selectedCount = selector.selectNow();

			// Check if we have any new keys
			if(selectedCount == 0){
				return;
			}

			// Process the keys
			Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
			while(keys.hasNext()){
				SelectionKey key = keys.next();
				keys.remove(); // Remove the new client.

				
			}
		} catch (IOException e){
			e.printStackTrace();
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