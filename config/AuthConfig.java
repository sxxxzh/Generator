package Generator.config;

import java.util.HashMap;
import java.util.Map;

public class AuthConfig {
    private String tableName = "user";
    private String roleField = "role";
    private String roleAdmin = "ADMIN";
    private String roleUser = "USER";
    private String roleGuest = "GUEST";
    private boolean enableAllTableAuth = true;
    private Map<String, String> userOwnedTables = new HashMap<>(){{
        put("user_detail", "user_id");
    }};
    
    public AuthConfig() {
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getRoleField() {
        return roleField;
    }
    
    public void setRoleField(String roleField) {
        this.roleField = roleField;
    }
    
    public String getRoleAdmin() {
        return roleAdmin;
    }
    
    public void setRoleAdmin(String roleAdmin) {
        this.roleAdmin = roleAdmin;
    }
    
    public String getRoleUser() {
        return roleUser;
    }
    
    public void setRoleUser(String roleUser) {
        this.roleUser = roleUser;
    }
    
    public String getRoleGuest() {
        return roleGuest;
    }
    
    public void setRoleGuest(String roleGuest) {
        this.roleGuest = roleGuest;
    }
    
    public boolean isEnableAllTableAuth() {
        return enableAllTableAuth;
    }
    
    public void setEnableAllTableAuth(boolean enableAllTableAuth) {
        this.enableAllTableAuth = enableAllTableAuth;
    }
    
    public Map<String, String> getUserOwnedTables() {
        return userOwnedTables;
    }
    
    public void setUserOwnedTables(Map<String, String> userOwnedTables) {
        this.userOwnedTables = userOwnedTables;
    }
    
    public void addUserOwnedTable(String tableName, String userField) {
        this.userOwnedTables.put(tableName, userField);
    }
    
    public String getUserOwnedField(String tableName) {
        return this.userOwnedTables.get(tableName);
    }
    
    public boolean isUserOwnedTable(String tableName) {
        return this.userOwnedTables.containsKey(tableName);
    }
}
