package Generator.config;

public class JwtConfig {
    private boolean autoGenerateKeyPair = true;
    private String privateKey = "";
    private String publicKey = "";
    private long expiration = 86400000L;
    private String tokenHeader = "Authorization";
    private String tokenPrefix = "Bearer ";
    private String algorithm = "EC";
    
    public JwtConfig() {
    }
    
    public boolean isAutoGenerateKeyPair() {
        return autoGenerateKeyPair;
    }
    
    public void setAutoGenerateKeyPair(boolean autoGenerateKeyPair) {
        this.autoGenerateKeyPair = autoGenerateKeyPair;
    }
    
    public String getPrivateKey() {
        return privateKey;
    }
    
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    
    public String getPublicKey() {
        return publicKey;
    }
    
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    
    public long getExpiration() {
        return expiration;
    }
    
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
    
    public String getTokenHeader() {
        return tokenHeader;
    }
    
    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
    }
    
    public String getTokenPrefix() {
        return tokenPrefix;
    }
    
    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
