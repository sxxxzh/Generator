package Generator.config;

public class DatabaseConfig {
    private String host = "localhost";
    private String port = "3306";
    private String name = "article";
    private String user = "root";
    private String password = "123456";
    
    public DatabaseConfig() {
    }
    
    public DatabaseConfig(String host, String port, String name, String user, String password) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.user = user;
        this.password = password;
    }
    
    public String getUrl() {
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8",
                host, port, name);
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public String getPort() {
        return port;
    }
    
    public void setPort(String port) {
        this.port = port;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
