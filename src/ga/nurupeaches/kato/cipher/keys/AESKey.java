package ga.nurupeaches.kato.cipher.keys;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AESKey implements Key {

    private SecretKey key;
    private IvParameterSpec iv;

    public AESKey(SecretKey key, IvParameterSpec iv){
        setKey(key);
        setIv(iv);
    }

    public SecretKey getKey(){
        return key;
    }
    public IvParameterSpec getIv(){
        return iv;
    }

    public void setKey(SecretKey key){
        if(!key.getAlgorithm().startsWith("AES")){
            throw new IllegalArgumentException("Attempted to pass non-AES key to AESKey!");
        }

        this.key = key;
    }

    public void setIv(IvParameterSpec iv){
        this.iv = iv;
    }

}