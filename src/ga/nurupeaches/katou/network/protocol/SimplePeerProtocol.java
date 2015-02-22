package ga.nurupeaches.katou.network.protocol;

import ga.nurupeaches.katou.network.Peer;
import ga.nurupeaches.katou.network.manager.SocketWrapper;
import ga.nurupeaches.katou.network.packets.Packet;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class SimplePeerProtocol implements Protocol {

	/**
	 * Mapping of connected peers by their address.
	 */
	private Map<SocketAddress, Peer> connectedPeers = new HashMap<SocketAddress, Peer>();

	/**
	 * Defines a simple peer to peer protocol.
	 */
	public SimplePeerProtocol(){}

	@Override
	public Map<SocketAddress, Peer> getConnectedPeers(){
		return connectedPeers;
	}

	@Override
	public Peer registerPeer(SocketWrapper socket){
		Peer peer = new Peer(socket);
		getConnectedPeers().put(socket.getAddress(), peer);
		return peer;
	}

	@Override
	public void sendPeerPacket(Peer peer, Packet packet) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(packet.size() + 1); // + 1 for the ID
		buffer.put(packet.getID());
		packet.write(buffer);
		buffer.flip();
		peer.getSocket().write(buffer);
	}
	
	@Override
	public Peer getPeer(SocketAddress address) {
		return getConnectedPeers().get(address);
	}

}