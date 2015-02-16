package ga.nurupeaches.kato.network.protocol;

import ga.nurupeaches.kato.network.Peer;
import ga.nurupeaches.kato.network.packets.Packet;

import java.net.SocketAddress;
import java.nio.channels.ByteChannel;
import java.util.HashMap;
import java.util.Map;

public class SimplePeerProtocol implements Protocol {

	/**
	 * Mapping of connected peers by their address.
	 */
	private Map<SocketAddress, Peer> connectedPeers = new HashMap<>();

	/**
	 * Defines a simple peer to peer protocol.
	 */
	public SimplePeerProtocol(){}

	@Override
	public Map<SocketAddress, Peer> getConnectedPeers() {
		return connectedPeers;
	}

	@Override
	public void parsePeerMessage(Packet packet) {

	}

	@Override
	public void sendPeerMessage(Peer peer, Message message) {
		ByteChannel channel = peer.getChannel();

	}

}