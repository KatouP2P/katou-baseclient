package ga.nurupeaches.kato.network.manager.udp;

import ga.nurupeaches.kato.KatouClient;
import ga.nurupeaches.kato.network.Peer;
import ga.nurupeaches.kato.network.manager.NetworkManager;
import ga.nurupeaches.kato.network.packets.Packet;
import ga.nurupeaches.kato.network.protocol.Protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * A UDP-based networking manager.
 * Note to Self (2/6/16): UDP is connectionless, so what the hell was I doing trying to manage connections like TCP.
 */
public class UDPNetworkManager implements NetworkManager {

	private DatagramChannel channel;
	private Selector selector;
	private final AtomicBoolean CLOSE_REQUESTED = new AtomicBoolean(false);

	public UDPNetworkManager(int port){
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
	public DatagramChannel getChannel() {
		return channel;
	}

	@Override
	public void requestClosure(){
		CLOSE_REQUESTED.compareAndSet(false, true);
	}

	@Override
	public void run(){
		while(!CLOSE_REQUESTED.get()){
			// Default to 0.
			int readyChannels;
			try{
				readyChannels = selector.select();
			} catch (IOException e){
				KatouClient.LOGGER.log(Level.SEVERE, "Failed to retrieve a number of ready channels!", e);
				continue;
			}

			// No ready channels = no one notices us.
			// Notice me, senpais~
			if(readyChannels == 0){
				continue;
			}

			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iter = keys.parallelStream().iterator();
			while(iter.hasNext()){
				handleKey(iter.next());
				iter.remove();
			}
		}
	}

	/**
	 * Handles the given key as a UDP channel.
	 * @param key - The key to handle.
	 */
	public void handleKey(SelectionKey key){
		if(!key.isValid()){
			return;
		}

		if(key.attachment() == null){
			key.attach(ByteBuffer.allocate(NetworkManager.DEFAULT_BUFFER_SIZE));
		}

		try{
			if(key.isReadable()){
				// Read the key.
				processReadableKey(key);
			} else if(key.isWritable()){
				// Write? to key.
				processWritableKey(key);
			}
		} catch (IOException e){
			KatouClient.LOGGER.log(Level.SEVERE, "Failed to handle selection key " + key, e);
		}
	}

	public void processReadableKey(SelectionKey key) throws IOException {
		// Double check. Who knows what happens with method calling.
		if(!key.isReadable()){
			throw new IllegalStateException("Attempted to process a non-readable key as readable!");
		}

		DatagramChannel channel = (DatagramChannel)key.channel();
		ByteBuffer buffer = (ByteBuffer)key.attachment();

		// Prepare a buffer and read into it; flip the buffer to reset the position.
		channel.read(buffer);
		buffer.flip();

		// Increment 1 and get the packet ID.
		byte id = buffer.get();

		// Construct a new packet, skipping constructors. sun.misc.Unsafe OP!
		Packet packet = Packet.convertPacket(id);
		// Pass the buffer into the packet.
		packet.read(buffer);

		// TODO: DO SOMETHING WITH PACKET AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

		buffer.flip();
	}

	public void processWritableKey(SelectionKey key) throws IOException {
		if(!key.isWritable()){
			throw new IllegalStateException("Attempted to process a non-writable key as writable!");
		}

		DatagramChannel channel = (DatagramChannel)key.channel();
		// Get the peer from a list of registered peers.
		Peer peer = Protocol.PROTOCOL.getPeer(channel.getRemoteAddress());

		if(peer != null){

		}
	}

}