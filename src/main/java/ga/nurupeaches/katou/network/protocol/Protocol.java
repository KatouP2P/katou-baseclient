package ga.nurupeaches.katou.network.protocol;

import ga.nurupeaches.katou.network.Peer;
import ga.nurupeaches.katou.network.manager.SocketWrapper;
import ga.nurupeaches.katou.network.packets.Packet;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Map;

/**
 * A protocol; can be used to implement a different protocol other than the simple one.
 */
public interface Protocol {

	// TODO: Make this dynamic and/or rewrite this part

	/**
	 * Fetches a mapping of connected peers; regardless of the KatouFile we're downloading.
	 * @return A map of peers.
	 */
	public Map<SocketAddress, Peer> getConnectedPeers();

	/**
	 * Registers a peer with the given SocketWrapper
	 * @param socket The socket of the peer
	 * @return The peer object
	 */
	public Peer registerPeer(SocketWrapper socket);

	/**
	 * Sends a packet to a peer.
	 * @param peer The desired peer
	 * @param packet The packet we want to send.
	 */
	public void sendPeerPacket(Peer peer, Packet packet) throws IOException;

	/**
	 * Retrieves a peer based on the given address.
	 * @param address The peer's address
	 * @return A peer, or <code>null</code> if none was found.
	 */
	public Peer getPeer(SocketAddress address);

}