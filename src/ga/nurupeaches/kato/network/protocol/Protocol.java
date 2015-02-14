package ga.nurupeaches.kato.network.protocol;

import ga.nurupeaches.kato.network.Peer;
import ga.nurupeaches.kato.network.packets.Packet;

import java.net.SocketAddress;
import java.util.Map;

/**
 * A protocol; can be used to implement a different protocol other than the simple one.
 */
public interface Protocol {

	// TODO: Make this dynamic and/or rewrite this part
	/**
	 * A reference to the protocol; by default, it's a SimplePeerProtocol.
	 */
	public static final Protocol PROTOCOL = new SimplePeerProtocol();

	/**
	 * Fetches a mapping of connected peers; regardless of the KatouFile we're downloading.
	 * @return A map of peers.
	 */
	public Map<SocketAddress, Peer> getConnectedPeers();

	/**
	 * Registers a peer with the given InetSocketAddress
	 * @param address - The address of the peer
	 * @return The peer object
	 */
	public default Peer registerPeer(SocketAddress address){
		Peer peer = new Peer(address);
		getConnectedPeers().put(address, peer);
		return peer;
	}

	/**
	 * Sends a message to a peer.
	 * @param peer - The desired peer
	 * @param message - The message we want to send.
	 */
	public void sendPeerMessage(Peer peer, Message message);

	/**
	 * Parses a peer message (packet).
	 * @param packet - The message to process.
	 */
	public void parsePeerMessage(Packet packet);

	/**
	 * Retrieves a peer based on the given address.
	 * @param address - The peer's address
	 * @return A peer, or <code>null</code> if none was found.
	 */
	public default Peer getPeer(SocketAddress address){
		return getConnectedPeers().get(address);
	}

}
