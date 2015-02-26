package ga.nurupeaches.katou.network.manager;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

/**
 * Class to wrap a TCP (Socket) or UDP (DatagramSocket) socket into a neater, more accessible class.
 */
public class SocketWrapper {

    /**
     * Raw socket object. Must be casted to be used.
     */
	private final Object socket;

    /**
     * The remote address of the socket.
     */
    private final SocketAddress address;

	/**
	 * Constructs a SocketWrapper with a SocketChannel
	 * @param socket A SocketChannel given from ServerSocketChannel.
	 */
	public SocketWrapper(SocketChannel socket){
		this.socket = socket;
        this.address = socket.socket().getRemoteSocketAddress();
	}

    /**
     * Constructs a SocketWrapper with a DatagramChannel; setting the address to the given address.
     * @param address The address to "bind" this wrapper to.
     * @param socket The socket to use (typically the one referenced in UDPNetworkManager).
     */
    public SocketWrapper(SocketAddress address, DatagramChannel socket){
        this.socket = socket;
        this.address = address;
    }

	/**
	 * Returns the socket's address.
	 * @return The SocketAddress of the socket.
	 * @throws IllegalStateException If the socket wasn't supported
	 */
	public SocketAddress getAddress(){
		return address;
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

			datagramSocket.send(buffer, address);
		}
	}

}