package ga.nurupeaches.kato.cipher;

import ga.nurupeaches.kato.KatouClient;
import ga.nurupeaches.kato.cipher.keys.AESKey;
import ga.nurupeaches.kato.cipher.keys.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.logging.Level;

public class CipherUtils {

    private static Cipher cipher;
    private static SecureRandom rng;
    private static KeyGenerator generator;
    private static final byte[] EMPTY = new byte[0];

    static {
        try {
            cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
            generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            rng = new SecureRandom();
        } catch (GeneralSecurityException e) {
            KatouClient.LOGGER.log(Level.SEVERE, "Failed to initialize cipher for AES256!", e);
        }
    }

    public static Key generateKey(){
        byte[] iv = new byte[16];
        rng.nextBytes(iv);
        return new AESKey(generator.generateKey(), new IvParameterSpec(iv));
    }

    public static byte[] encryptContent(byte[] content, Key key){
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key.getKey(), key.getIv());
            return cipher.doFinal(content);
        } catch (GeneralSecurityException e){
            KatouClient.LOGGER.log(Level.SEVERE, "Failed to encrypt content!", e);
            return EMPTY; // See kids? This is called being safe.
        }
    }

    public static byte[] decryptContent(byte[] content, Key key){
        try {
            cipher.init(Cipher.DECRYPT_MODE, key.getKey(), key.getIv());
            return cipher.doFinal(content);
        } catch (GeneralSecurityException e){
            KatouClient.LOGGER.log(Level.SEVERE, "Failed to decrypt content!", e);
            return EMPTY;
        }
    }

}