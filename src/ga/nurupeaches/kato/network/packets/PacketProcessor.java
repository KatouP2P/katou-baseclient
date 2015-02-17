package ga.nurupeaches.kato.network.packets;

import ga.nurupeaches.kato.network.KatouFile;
import ga.nurupeaches.kato.network.KatouMetadata;
import ga.nurupeaches.kato.network.Peer;
import ga.nurupeaches.kato.network.protocol.Protocol;

import java.io.IOException;

public class PacketProcessor {

	/**
	 * Processes an incoming packet based on it's type.
	 * @param packet The packet to process
	 * @throws IOException If the packet was related to an I/O operation.
	 */
	public static void process(Packet packet) throws IOException {
		// TODO: Find alternative to if-then-else statements
		Peer peer = Protocol.PROTOCOL.getPeer(packet.getOrigin());

		if(peer == null){
			// No peer; so why process it?
			return;
		}

		if(packet instanceof PacketVersion){
			peer.setVersion(((PacketVersion)packet).getVersion());
		} else if(packet instanceof PacketStatus){
			KatouMetadata metadata = ((PacketStatus)packet).getMetadata();
			if(!peer.hasFile(metadata)){
				peer.registerFile(new KatouFile(metadata));
			}
		}

	}

}