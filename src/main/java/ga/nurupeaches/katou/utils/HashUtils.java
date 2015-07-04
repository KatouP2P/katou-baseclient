package ga.nurupeaches.katou.utils;

import ga.nurupeaches.katou.KatouClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

public final class HashUtils {

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	private static final String DEFAULT_HASH = "SHA-256";
	private static MessageDigest DIGEST;

	private HashUtils(){}

	static {
		try {
			DIGEST = MessageDigest.getInstance(DEFAULT_HASH);
		} catch (NoSuchAlgorithmException e){
			KatouClient.LOGGER.log(Level.WARNING, "Failed to obtain a digester for algorithm " + DEFAULT_HASH, e);
		}
	}

	public static byte[] computeHash(byte... data){
		try{
			DIGEST.update(data);
			return DIGEST.digest();
		} finally {
			DIGEST.reset();
		}
	}

	/**
	 * Computes a string's hash.
	 * @param string The string to hash
	 * @return The digest in a byte array
	 */
	public static byte[] computeHash(String string){
		return computeHash(string.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Computes a hash for a path (uses streams as opposed to in-memory byte arrays).
	 * @param file Path (file) to compute
	 * @return The digest in a byte array.
	 */
	public static byte[] computeHash(File file){
		DigestInputStream digestingStream = null;
		FileInputStream stream = null;

		try {
			stream = new FileInputStream(file);
			digestingStream = new DigestInputStream(stream, DIGEST);
			while(digestingStream.read() != -1);
			return DIGEST.digest();
		} catch (IOException e){
			KatouClient.LOGGER.log(Level.WARNING, "Failed to calculate hash for file " + file, e);
			return new byte[0];
		} finally {
			DIGEST.reset();

			if(digestingStream != null){
				try{
					digestingStream.close();
				} catch (IOException e){
					KatouClient.LOGGER.log(Level.WARNING, "Failed to close digest stream! We're leaking resources!", e);
				}
			}

			if(stream != null){
				try{
					stream.close();
				} catch (IOException e){
					KatouClient.LOGGER.log(Level.WARNING, "Failed to close file stream! We're leaking resources!", e);
				}
			}
		}
	}

	public static String hexifyArray(byte... array){
		char[] hexified = new char[array.length * 2];
		for(int i = 0; i < array.length; i++){
			int v = array[i] & 0xFF;
			hexified[i * 2] = HEX_ARRAY[v >>> 4];
			hexified[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}

		return new String(hexified);
	}

}