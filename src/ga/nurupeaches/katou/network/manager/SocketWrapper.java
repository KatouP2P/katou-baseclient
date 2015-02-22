package ga.nurupeaches.katou.network.manager;

import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to wrap a TCP (Socket) or UDP (DatagramSocket) socket into a neater, more accessible class.
 */
public class SocketWrapper {

	private final static List<Class<?>> SUPPORTED_CLASSES = new ArrayList<Class<?>>(Arrays.asList(new Class<?>[]{

			DatagramSocket.class, Socket.class

	}));

	private final Object socket;

	/**
	 * Constructs a SocketWrapper with a supported socket type
	 * @param socket The socket; must be a supported socket type
	 * @throws IllegalArgumentException If the socket was not a supported socket type
	 */
	public SocketWrapper(Object socket){
		if(!SUPPORTED_CLASSES.contains(socket.getClass())){
			throw new IllegalArgumentException("Not supported: " + socket.getClass() + ". " +
					"ChannelWrapper only supports: " + Arrays.toString(SUPPORTED_CLASSES.toArray()));
		}

		this.socket = socket;
	}

	/**
	 * Returns the socket's address.
	 * @return The SocketAddress of the socket.
	 * @throws IllegalStateException If the socket wasn't supported
	 */
	public SocketAddress getAddress(){
		// TODO: Figure out a way to do this without casting.
		if(socket instanceof DatagramSocket){
			return ((DatagramSocket)socket).getRemoteSocketAddress();
		} else if(socket instanceof Socket){
			return ((Socket)socket).getRemoteSocketAddress();
		} else {
			throw new IllegalStateException("The given channel was " + socket.getClass().getName() + ", which is not supported!");
		}
	}

	public SocketType getType(){
		if(socket instanceof Socket){
			return SocketType.TCP;
		} else if(socket instanceof DatagramSocket){
			return SocketType.UDP;
		} else {
			return SocketType.UNKNOWN;
		}
	}

	public Object getRawSocket(){
		return socket;
	}

}