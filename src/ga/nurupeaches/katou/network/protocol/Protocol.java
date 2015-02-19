package ga.nurupeaches.katou.network.protocol;

import ga.nurupeaches.katou.network.Peer;
import ga.nurupeaches.katou.network.manager.ChannelWrapper;
import ga.nurupeaches.katou.network.packets.Packet;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
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
	 * Registers a peer with the given SocketChannel
	 * @param channel The channel of the peer
	 * @return The peer object
	 */
	public default Peer registerPeer(ChannelWrapper channel) throws IOException {
		Peer peer = new Peer(channel);
		getConnectedPeers().put(channel.getAddress(), peer);
		return peer;
	}

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
	public default Peer getPeer(SocketAddress address){
		return getConnectedPeers().get(address);
	}

}
