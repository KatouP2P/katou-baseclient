package ga.nurupeaches.katou.network.packets;

import ga.nurupeaches.katou.KatouClient;
import ga.nurupeaches.katou.network.Metadata;
import ga.nurupeaches.katou.network.Peer;
import ga.nurupeaches.katou.network.KatouFile;

import java.io.IOException;

public class PacketProcessor {

	/**
	 * Processes an incoming packet based on it's type.
	 * @param packet The packet to process
	 * @throws IOException If the packet was related to an I/O operation.
	 */
	public static void process(Packet packet) throws IOException {
		// TODO: Find alternative to if-then-else statements
		Peer peer = KatouClient.getProtocol().getPeer(packet.getOrigin());

		if(peer == null){
			// No peer; so why process it?
			return;
		}

		if(packet instanceof PacketVersion){
			peer.setVersion(((PacketVersion)packet).getVersion());
			System.out.println("recv: Version(" + peer.getVersion() + ")");
		} else if(packet instanceof PacketStatus){
			Metadata metadata = ((PacketStatus)packet).getMetadata();
			if(!peer.hasFile(metadata)){
				peer.registerFile(new KatouFile(metadata));
			}

			System.out.println("recv: Metadata(name=" + metadata.getName() + ",size=" + metadata.getSize() + ",hash=" + metadata.getHash() + ")");
		}

	}

}