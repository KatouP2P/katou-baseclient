package ga.nurupeaches.katou.network.manager;

import ga.nurupeaches.katou.KatouClient;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

/**
 * Class to wrap a TCP (SocketChannel) or UDP (DatagramChannel) into a neater, more accessible class.
 */
public class ChannelWrapper<T extends ByteChannel & NetworkChannel> {

	private final T channel;

	/**
	 * Constructs a ChannelWrapper with a SocketChannel or DatagramChannel
	 * @param channel The channel; must be a SocketChannel or DatagramChannel
	 * @throws IllegalArgumentException If the channel was not a SocketChannel or DatagramChannel
	 */
	public ChannelWrapper(T channel){
		if(!(channel instanceof SocketChannel) && !(channel instanceof DatagramChannel)){
			throw new IllegalArgumentException("ChannelWrapper only supports SocketChannel and DatagramChannel!");
		}

		this.channel = channel;
	}

	/**
	 * Returns the socket's address.
	 * @return The SocketAddress of the channel.
	 * @throws IllegalStateException If the channel was somehow a non-Socket/Datagram channel.
	 */
	public SocketAddress getAddress(){
		try{
			if(channel instanceof SocketChannel){
				return ((SocketChannel)channel).getRemoteAddress();
			} else if(channel instanceof DatagramChannel){
				return ((DatagramChannel)channel).getRemoteAddress();
			} else {
				throw new IllegalStateException("The given channel was " + channel.getClass().getName() + ", which is not supported!");
			}
		} catch (IOException e){
			KatouClient.LOGGER.log(Level.WARNING, "Failed to retrieve peer's remote address!", e);
			return null;
		}
	}

	/**
	 * Returns the channel as a ByteChannel
	 * @return A ByteChannel representation of the channel.
	 */
	public ByteChannel getAsByteChannel(){
		return channel;
	}

	/**
	 * Returns the channel as a NetworkChannel
	 * @return A NetworkChannel representation of the channel.
	 */
	public NetworkChannel getAsNetworkChannel(){
		return channel;
	}

}