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
	public Peer registerPeer(SocketWrapper channel){
		Peer peer = new Peer(channel);
		getConnectedPeers().put(channel.getAddress(), peer);
		return peer;
	}

	@Override
	public void sendPeerPacket(Peer peer, Packet packet) throws IOException {
		Object channel = peer.getSocket().getRawSocket();
		ByteBuffer buffer = ByteBuffer.allocate(packet.size() + 1); // + 1 for the ID
		buffer.put(packet.getID());
		packet.write(buffer);
		buffer.flip();

		//TODO: Re-implement this entire method to match the getRawSocket() method.
//		while(buffer.hasRemaining()){
//			channel.write(buffer);
//		}
	}
	
	@Override
	public Peer getPeer(SocketAddress address) {
		return getConnectedPeers().get(address);
	}

}