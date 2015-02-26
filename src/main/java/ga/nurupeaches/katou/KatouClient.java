package ga.nurupeaches.katou;

import ga.nurupeaches.katou.network.manager.NetworkManager;
import ga.nurupeaches.katou.network.manager.SocketType;
import ga.nurupeaches.katou.network.manager.tcp.TCPNetworkManager;
import ga.nurupeaches.katou.network.manager.udp.UDPNetworkManager;
import ga.nurupeaches.katou.network.protocol.Protocol;
import ga.nurupeaches.katou.network.protocol.SimplePeerProtocol;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Root class for the Kato client.
 */
public class KatouClient {

	/**
	 * Global logger.
	 */
	public static final Logger LOGGER = Logger.getLogger("Katou");

	/**
	 * Default port to listen on
	 */
	// The port number was decided merely by adding the letters in "katou" as their index in the alphabet
	// followed by slapping on two 0's to get it over the first 1024 ports restriction.
	public static final int DEFAULT_PORT = 6800;

	/**
	 * The network manager. Currently set to TCP.
	 * TODO: Make this switachable or allowed to be combined with UDP.
	 */
	private static NetworkManager networkManager;

	/**
	 * A reference to the protocol; by default, it's a SimplePeerProtocol.
	 */
	private static Protocol protocol = new SimplePeerProtocol();

	/**
	 * Main method. Initializes the basics.
	 * @param arguments Arguments to pass to the application
	 * @throws Throwable
	 */
	public static void main(String[] arguments) throws IOException {
		System.out.println("KatouCLI - Hello, world!");
		KatouClient client = new KatouClient();
		client.initializeNetworking();


	}

	/**
	 * Constrcutor for Katou. Currently nothing.
	 */
	public KatouClient(){}

	/**
	 * Initializes networking for Katou.
	 */
	public void initializeNetworking() throws IOException {
		if(networkManager == null){
			SocketType networkType = Configuration.getSocketType();
			int port = Configuration.getPort();

			switch(networkType){
				case TCP:
					networkManager = new TCPNetworkManager(port);
					break;

				case UDP:
					networkManager = new UDPNetworkManager(port);
					break;
			}
		}
	}

	public static NetworkManager getNetworkManager(){
		return networkManager;
	}

	public static Protocol getProtocol(){
		return protocol;
	}

}