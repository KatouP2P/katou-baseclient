package ga.nurupeaches.kato.network.packets;

import ga.nurupeaches.kato.network.KatouFile;
import ga.nurupeaches.kato.network.KatouMetadata;
import ga.nurupeaches.kato.network.Peer;
import ga.nurupeaches.kato.network.protocol.Protocol;

public class PacketProcessor {

	public static void process(Packet packet){
		// TODO: Find alternative to if-then-else statements
		Peer peer = Protocol.PROTOCOL.getPeer(packet.getOrigin());

		if(peer == null){
			// No peer; so why process it?
			return;
		}

		if(packet instanceof PacketStatus){
			KatouMetadata metadata = ((PacketStatus)packet).getMetadata();
			if(!peer.hasFile(metadata)){
				KatouFile file = new KatouFile(metadata);
			}
		}

	}

}