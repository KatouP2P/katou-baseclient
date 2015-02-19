package ga.nurupeaches.katou.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Basic utility class for manipulating data in or for Packets
 */
public class PacketUtils {

	/**
	 * Reads a string from a buffer. Increments the buffer and doesn't reset it to the beginning.
	 * @param buffer Buffer to read from.
	 * @return The string read from the buffer.
	 */
	public static String readString(ByteBuffer buffer){
		byte[] stringBytes = new byte[buffer.getInt()];
		buffer.get(stringBytes);
		return new String(stringBytes, StandardCharsets.UTF_8);
	}

	/**
	 * Writes a string to the buffer, the length first and then the string's bytes.
	 * @param string The string to write
	 * @param buffer The buffer to write into
	 */
	public static void writeString(String string, ByteBuffer buffer){
		byte[] stringBytes = string.getBytes(StandardCharsets.UTF_8);
		buffer.putInt(stringBytes.length);
		buffer.put(stringBytes);
	}

}