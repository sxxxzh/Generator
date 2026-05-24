package Generator.util;

import java.security.*;
import java.util.Base64;

public class KeyPairGeneratorUtil {
    
    public static class KeyPairResult {
        private final String privateKey;
        private final String publicKey;
        
        public KeyPairResult(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }
        
        public String getPrivateKey() {
            return privateKey;
        }
        
        public String getPublicKey() {
            return publicKey;
        }
    }
    
    public static KeyPairResult generateECKeyPair() throws NoSuchAlgorithmException {
        java.security.KeyPairGenerator keyPairGenerator = java.security.KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(256);
        
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        
        return new KeyPairResult(privateKey, publicKey);
    }
}
