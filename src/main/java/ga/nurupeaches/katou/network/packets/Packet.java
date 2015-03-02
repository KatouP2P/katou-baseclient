package ga.nurupeaches.katou.network.packets;

import ga.nurupeaches.katou.utils.UnsafeUtils;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public abstract class Packet {

	/**
	 * An ID lookup field; uses bytes.
	 */
	private static final HashBimap<Byte, Class<? extends Packet>> ID_LOOKUP = new HashBimap<Byte, Class<? extends Packet>>();

	/**
	 * The origin of the packet.
	 */
	private SocketAddress origin;

	/**
	 * Register the packets related to Katou. You could register your own packets if you want.
	 */
	static {
		ID_LOOKUP.put((byte) 0x01, PacketVersion.class);
		ID_LOOKUP.put((byte) 0x02, PacketStatus.class);
	}

	/**
	 * Matches an ID with a new, empty packet.
	 * @param id - The byte id from a buffer.
	 * @return A new packet from the buffer. Does not fill the packet with information and increments the buffer.
	 * @throws IllegalArgumentException If a class was not found by the ID.
	 */
	public static Packet convertPacket(byte id){
		Class<? extends Packet> packetClass = ID_LOOKUP.getByKey(id);
		if(packetClass == null){
			throw new IllegalArgumentException("Invalid ID#" + id);
		}

		try{
			Packet packet = (Packet)UnsafeUtils.getUnsafe().allocateInstance(packetClass);
			packet.init();
			return packet;
		} catch (InstantiationException e){
			// Theoretically, this should only happen if the user is running anything but OpenJDK or Oracle's JRE/JDK.
			// TODO: Support other JVMs.
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sets the origin of this packet.
	 * @param address - The Original sender of the packet.
	 */
	public void setOrigin(SocketAddress address){
		this.origin = address;
	}

	/**
	 * Returns the origin of this packet.
	 */
	public SocketAddress getOrigin(){
		return origin;
	}

	/**
	 * Returns the packet's ID
	 * @return The ID (byte)
	 */
	public byte getID(){
		return ID_LOOKUP.getByValue(getClass());
	}

	/**
	 * Initializes the packet after creation without parameters.
	 */
	public abstract void init();

	/**
	 * Returns the size of the packet.
	 * @return The size of the packet.
	 */
	public abstract int size();

	/**
	 * Reads data from the given buffer.
	 * @param buffer Buffer to read from.
	 */
	public abstract void read(ByteBuffer buffer) throws IOException;

	/**
	 * Writes data to the given buffer.
	 * @param buffer Buffer to write to.
	 */
	public abstract void write(ByteBuffer buffer) throws IOException;


	private static class HashBimap<K, V> {

		private Map<K, V> keyToValue = new HashMap<K, V>();
		private Map<V, K> valueToKey = new HashMap<V, K>();

		public void put(K key, V value){
			keyToValue.put(key, value);
			valueToKey.put(value, key);
		}

		public V getByKey(K key){
			return keyToValue.get(key);
		}

		public K getByValue(V value){
			return valueToKey.get(value);
		}

		public boolean containsKey(K key){
			return keyToValue.containsKey(key);
		}

		public boolean containsValue(V value){
			return valueToKey.containsKey(value);
		}

		@Override
		public String toString(){
			return keyToValue.toString() + ";" + valueToKey.toString();

		}

	}

}