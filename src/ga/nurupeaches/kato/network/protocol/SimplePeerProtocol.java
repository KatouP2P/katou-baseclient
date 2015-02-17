package ga.nurupeaches.kato.network.protocol;

import ga.nurupeaches.kato.network.Peer;
import ga.nurupeaches.kato.network.packets.Packet;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
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
	public Map<SocketAddress, Peer> getConnectedPeers(){
		return connectedPeers;
	}

	@Override
	public void sendPeerPacket(Peer peer, Packet packet) throws IOException {
		ByteChannel channel = peer.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(packet.size());
		packet.write(buffer);
		channel.write(buffer);
	}

}