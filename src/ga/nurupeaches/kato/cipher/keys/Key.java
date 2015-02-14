package ga.nurupeaches.kato.cipher.keys;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public interface Key {

    public SecretKey getKey();
    public void setKey(SecretKey key);

    public IvParameterSpec getIv();
    public void setIv(IvParameterSpec iv);

}
