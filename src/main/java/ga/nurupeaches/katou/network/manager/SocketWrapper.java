package ga.nurupeaches.katou.network.manager;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to wrap a TCP (Socket) or UDP (DatagramSocket) socket into a neater, more accessible class.
 */
public class SocketWrapper {

	private final static List<Class<?>> SUPPORTED_CLASSES = new ArrayList<Class<?>>(Arrays.asList(new Class<?>[]{

			DatagramChannel.class, SocketChannel.class

	}));

	private final Object socket;

	/**
	 * Constructs a SocketWrapper with a supported socket type
	 * @param socket The socket; must be a supported socket type
	 * @throws IllegalArgumentException If the socket was not a supported socket type
	 */
	public SocketWrapper(Object socket){
		if(!SUPPORTED_CLASSES.contains(socket.getClass().getSuperclass())){
			throw new IllegalArgumentException("Not supported: " + socket.getClass() + ". " +
					"SocketWrapper only supports: " + Arrays.toString(SUPPORTED_CLASSES.toArray()));
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
		if(socket instanceof DatagramChannel){
			return ((DatagramChannel)socket).socket().getRemoteSocketAddress();
		} else if(socket instanceof SocketChannel){
			return ((SocketChannel)socket).socket().getRemoteSocketAddress();
		} else {
			throw new IllegalStateException("The given socket was " + socket.getClass().getName() + ", which is not supported!");
		}
	}

	/**
	 * Returns the type of socket we rely inside the wrapper.
	 * @return TCP, UDP, or UNKNOWN.
	 */
	public SocketType getType(){
		if(socket instanceof SocketChannel){
			return SocketType.TCP;
		} else if(socket instanceof DatagramChannel){
			return SocketType.UDP;
		} else {
			return SocketType.UNKNOWN;
		}
	}

	/**
	 * Returns the raw socket as an Object.
	 * @return An Object representation of the socket.
	 */
	public Object getRawSocket(){
		return socket;
	}

	/**
	 * Writes a ByteBuffer to the socket.
	 * @param buffer The buffer to write
	 * @throws IOException If the operation failed for whatever reason.
	 */
	public void write(ByteBuffer buffer) throws IOException {
		if(socket instanceof SocketChannel){
			((SocketChannel)socket).write(buffer);
		} else if(socket instanceof DatagramChannel){
			DatagramChannel datagramSocket = (DatagramChannel)socket;
			// Be careful; we create a new DatagramSocket that may have not been connected for some reason.
			if(!datagramSocket.isConnected()){
				throw new IllegalStateException("Socket isn't connected!");
			}

			datagramSocket.write(buffer);
		}
	}

}