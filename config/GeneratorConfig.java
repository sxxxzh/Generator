package Generator.config;

import java.util.HashSet;
import java.util.Set;

public class GeneratorConfig {
    private String basePackage = "cn.example";
    private String outputPath = "src/main/java";
    private String applicationName = "ExampleApplication";
    private String apiBase = "http://localhost:8080/api";
    private boolean useDTO = true;
    private Set<String> excludedFields = new HashSet<>();
    private Set<String> selectedTables = new HashSet<>();
    
    public GeneratorConfig() {
        excludedFields.add("password");
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationPackage() {
        return basePackage;
    }
    
    public String getEntityPackage() {
        return basePackage + ".entity";
    }
    
    public String getRepositoryPackage() {
        return basePackage + ".repository";
    }
    
    public String getServicePackage() {
        return basePackage + ".service";
    }
    
    public String getServiceImplPackage() {
        return basePackage + ".service.impl";
    }
    
    public String getControllerPackage() {
        return basePackage + ".controller";
    }
    
    public String getCommonPackage() {
        return basePackage + ".common";
    }
    
    public String getDtoPackage() {
        return basePackage + ".dto";
    }
    
    public String getBasePackage() {
        return basePackage;
    }
    
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
    
    public String getOutputPath() {
        return outputPath;
    }
    
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
    
    public boolean isUseDTO() {
        return useDTO;
    }
    
    public void setUseDTO(boolean useDTO) {
        this.useDTO = useDTO;
    }
    
    public Set<String> getExcludedFields() {
        return excludedFields;
    }
    
    public void setExcludedFields(Set<String> excludedFields) {
        this.excludedFields = excludedFields;
    }
    
    public Set<String> getSelectedTables() {
        return selectedTables;
    }
    
    public void setSelectedTables(Set<String> selectedTables) {
        this.selectedTables = selectedTables;
    }
    
    public String getApiBase() {
        return apiBase;
    }
    
    public void setApiBase(String apiBase) {
        this.apiBase = apiBase;
    }
    
    public String getApiPath() {
        try {
            return new java.net.URL(apiBase).getPath().replaceAll("/$", "");
        } catch (Exception e) {
            return apiBase;
        }
    }
}
