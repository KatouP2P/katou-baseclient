package ga.nurupeaches.katou.utils;

import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PacketUtilsTest extends TestCase {

	private String testString = "Megumi Katou";
	private ByteBuffer testBuffer;

	public void testReadString() throws Exception {
		byte[] testStringBytes = testString.getBytes(StandardCharsets.UTF_8);
		testBuffer = ByteBuffer.allocate(testStringBytes.length + Integer.BYTES);
		testBuffer.putInt(testString.length());
		testBuffer.put(testStringBytes);
		testBuffer.flip();

		PacketUtils.readString(testBuffer);

		assert Arrays.equals(Arrays.copyOfRange(testBuffer.array(), Integer.BYTES, testBuffer.array().length), testStringBytes);
	}

	public void testWriteString() throws Exception {
		byte[] testStringBytes = testString.getBytes(StandardCharsets.UTF_8);
		testBuffer = ByteBuffer.allocate(testStringBytes.length + Integer.BYTES);

		PacketUtils.writeString(testString, testBuffer);
		System.out.println(Arrays.toString(testBuffer.array()));
	}

}