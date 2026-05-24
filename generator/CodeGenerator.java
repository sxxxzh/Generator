package Generator.generator;

import Generator.config.AuthConfig;
import Generator.config.GeneratorConfig;
import Generator.model.ColumnInfo;
import Generator.model.TableInfo;
import Generator.util.TypeUtils;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class CodeGenerator {
    private final GeneratorConfig generatorConfig;
    private final AuthConfig authConfig;
    private boolean hasAuthTable = false;
    private boolean hasUsernameField = false;
    private boolean hasPasswordField = false;
    private boolean hasRoleField = false;
    
    public CodeGenerator(GeneratorConfig generatorConfig, AuthConfig authConfig) {
        this.generatorConfig = generatorConfig;
        this.authConfig = authConfig;
    }
    
    public void checkAuthTable(java.util.List<TableInfo> tables) {
        for (TableInfo table : tables) {
            if (authConfig.getTableName().equalsIgnoreCase(table.getTableName())) {
                hasAuthTable = true;
                for (ColumnInfo column : table.getColumns()) {
                    if (authConfig.getUsernameField().equalsIgnoreCase(column.getColumnName())) {
                        hasUsernameField = true;
                    }
                    if (authConfig.getPasswordField().equalsIgnoreCase(column.getColumnName())) {
                        hasPasswordField = true;
                    }
                    if (authConfig.getRoleField().equalsIgnoreCase(column.getColumnName())) {
                        hasRoleField = true;
                    }
                }
                break;
            }
        }
        
        if (hasAuthTable && hasRoleField) {
            System.out.println("✓ 检测到 " + authConfig.getTableName() + " 表包含 " + authConfig.getRoleField() + " 字段，将启用权限验证");
        }
        if (hasAuthTable && (!hasUsernameField || !hasPasswordField || !hasRoleField)) {
            System.out.println("! 警告: " + authConfig.getTableName() + " 表缺少认证相关字段，当前配置为 username="
                    + authConfig.getUsernameField() + ", password=" + authConfig.getPasswordField() + ", role=" + authConfig.getRoleField());
        }
    }
    
    public void generateCommonClasses() throws IOException {
        generateApiResponse();
        generateApplicationClass();
    }

    public void generateApplicationClass() throws IOException {
        String className = generatorConfig.getApplicationName();
        String packagePath = generatorConfig.getApplicationPackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/" + className + ".java";

        if (Generator.util.FileUtils.fileExists(filePath)) {
            System.out.println("  启动类 " + className + ".java 已存在，跳过");
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getApplicationPackage()).append(";\n\n");
        content.append("import org.springframework.boot.SpringApplication;\n");
        content.append("import org.springframework.boot.autoconfigure.SpringBootApplication;\n\n");
        content.append("@SpringBootApplication\n");
        content.append("public class ").append(className).append(" {\n\n");
        content.append("    public static void main(String[] args) {\n");
        content.append("        SpringApplication.run(").append(className).append(".class, args);\n");
        content.append("    }\n");
        content.append("}\n");

        Generator.util.FileUtils.writeToFile(filePath, content.toString());
    }

    public void generatePomXml() throws IOException {
        String pomPath = "pom.xml";
        StringBuilder content = new StringBuilder();
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        content.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n");
        content.append("         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        content.append("         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n");
        content.append("    <modelVersion>4.0.0</modelVersion>\n");
        content.append("    <parent>\n");
        content.append("        <groupId>org.springframework.boot</groupId>\n");
        content.append("        <artifactId>spring-boot-starter-parent</artifactId>\n");
        content.append("        <version>4.0.0</version>\n");
        content.append("        <relativePath/>\n");
        content.append("    </parent>\n\n");
        content.append("    <groupId>").append(generatorConfig.getBasePackage()).append("</groupId>\n");
        content.append("    <artifactId>SpringBoot</artifactId>\n");
        content.append("    <version>1.0-SNAPSHOT</version>\n\n");
        content.append("    <properties>\n");
        content.append("        <java.version>21</java.version>\n");
        content.append("        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n");
        content.append("    </properties>\n\n");
        content.append("    <dependencies>\n");
        content.append("        <dependency>\n");
        content.append("            <groupId>org.springframework.boot</groupId>\n");
        content.append("            <artifactId>spring-boot-starter-web</artifactId>\n");
        content.append("        </dependency>\n\n");
        content.append("        <dependency>\n");
        content.append("            <groupId>org.springframework.boot</groupId>\n");
        content.append("            <artifactId>spring-boot-starter-data-jpa</artifactId>\n");
        content.append("        </dependency>\n\n");
        content.append("        <dependency>\n");
        content.append("            <groupId>org.springframework.boot</groupId>\n");
        content.append("            <artifactId>spring-boot-starter-security</artifactId>\n");
        content.append("        </dependency>\n\n");
        content.append("        <dependency>\n");
        content.append("            <groupId>com.mysql</groupId>\n");
        content.append("            <artifactId>mysql-connector-j</artifactId>\n");
        content.append("            <scope>runtime</scope>\n");
        content.append("        </dependency>\n\n");
        content.append("        <dependency>\n");
        content.append("            <groupId>org.projectlombok</groupId>\n");
        content.append("            <artifactId>lombok</artifactId>\n");
        content.append("            <optional>true</optional>\n");
        content.append("        </dependency>\n\n");
        content.append("        <dependency>\n");
        content.append("            <groupId>io.jsonwebtoken</groupId>\n");
        content.append("            <artifactId>jjwt-api</artifactId>\n");
        content.append("            <version>0.11.5</version>\n");
        content.append("        </dependency>\n\n");
        content.append("        <dependency>\n");
        content.append("            <groupId>io.jsonwebtoken</groupId>\n");
        content.append("            <artifactId>jjwt-impl</artifactId>\n");
        content.append("            <version>0.11.5</version>\n");
        content.append("            <scope>runtime</scope>\n");
        content.append("        </dependency>\n\n");
        content.append("        <dependency>\n");
        content.append("            <groupId>io.jsonwebtoken</groupId>\n");
        content.append("            <artifactId>jjwt-jackson</artifactId>\n");
        content.append("            <version>0.11.5</version>\n");
        content.append("            <scope>runtime</scope>\n");
        content.append("        </dependency>\n\n");
        content.append("        <dependency>\n");
        content.append("            <groupId>org.springframework.boot</groupId>\n");
        content.append("            <artifactId>spring-boot-starter-test</artifactId>\n");
        content.append("            <scope>test</scope>\n");
        content.append("        </dependency>\n");
        content.append("    </dependencies>\n\n");
        content.append("    <build>\n");
        content.append("        <plugins>\n");
        content.append("            <plugin>\n");
        content.append("                <groupId>org.springframework.boot</groupId>\n");
        content.append("                <artifactId>spring-boot-maven-plugin</artifactId>\n");
        content.append("            </plugin>\n");
        content.append("        </plugins>\n");
        content.append("    </build>\n\n");
        content.append("</project>\n");

        Generator.util.FileUtils.writeToFile(pomPath, content.toString());
    }
    
    private void generateApiResponse() throws IOException {
        String packagePath = generatorConfig.getCommonPackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/ApiResponse.java";
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getCommonPackage()).append(";\n\n");
        content.append("public class ApiResponse<T> {\n");
        content.append("    private int code;\n");
        content.append("    private String message;\n");
        content.append("    private T data;\n\n");
        content.append("    public ApiResponse() {\n");
        content.append("    }\n\n");
        content.append("    public ApiResponse(int code, String message, T data) {\n");
        content.append("        this.code = code;\n");
        content.append("        this.message = message;\n");
        content.append("        this.data = data;\n");
        content.append("    }\n\n");
        content.append("    public static <T> ApiResponse<T> success(T data) {\n");
        content.append("        return new ApiResponse<>(200, \"成功\", data);\n");
        content.append("    }\n\n");
        content.append("    public static <T> ApiResponse<T> success(String message, T data) {\n");
        content.append("        return new ApiResponse<>(200, message, data);\n");
        content.append("    }\n\n");
        content.append("    public static ApiResponse<Void> success(String message) {\n");
        content.append("        return new ApiResponse<>(200, message, null);\n");
        content.append("    }\n\n");
        content.append("    public static <T> ApiResponse<T> error(int code, String message) {\n");
        content.append("        return new ApiResponse<>(code, message, null);\n");
        content.append("    }\n\n");
        content.append("    public int getCode() {\n");
        content.append("        return code;\n");
        content.append("    }\n\n");
        content.append("    public void setCode(int code) {\n");
        content.append("        this.code = code;\n");
        content.append("    }\n\n");
        content.append("    public String getMessage() {\n");
        content.append("        return message;\n");
        content.append("    }\n\n");
        content.append("    public void setMessage(String message) {\n");
        content.append("        this.message = message;\n");
        content.append("    }\n\n");
        content.append("    public T getData() {\n");
        content.append("        return data;\n");
        content.append("    }\n\n");
        content.append("    public void setData(T data) {\n");
        content.append("        this.data = data;\n");
        content.append("    }\n");
        content.append("}\n");
        
        Generator.util.FileUtils.writeToFile(filePath, content.toString());
    }
    
    public void generateAll(TableInfo table) throws IOException {
        generateEntity(table);
        generateRepository(table);
        generateService(table);
        generateServiceImpl(table);
        generateController(table);
        generateDTO(table);
    }
    
    private void generateEntity(TableInfo table) throws IOException {
        String className = Generator.util.StringUtils.toCamelCase(table.getTableName(), true);
        String packagePath = generatorConfig.getEntityPackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/" + className + ".java";
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getEntityPackage()).append(";\n\n");
        
        Set<String> imports = new TreeSet<>();
        imports.add("jakarta.persistence.*");
        imports.add("java.io.Serializable");
        
        boolean hasDate = false;
        boolean hasDateTime = false;
        boolean hasCreationTimestamp = false;
        boolean hasUpdateTimestamp = false;
        for (ColumnInfo column : table.getColumns()) {
            if ("DATE".equals(column.getColumnType())) hasDate = true;
            if ("DATETIME".equals(column.getColumnType()) || "TIMESTAMP".equals(column.getColumnType())) hasDateTime = true;
            String colName = column.getColumnName().toLowerCase();
            if (isTimeType(column.getColumnType()) && (colName.startsWith("create_") || colName.startsWith("created_"))) {
                hasCreationTimestamp = true;
            }
            if (isTimeType(column.getColumnType()) && (colName.startsWith("update_") || colName.startsWith("updated_"))) {
                hasUpdateTimestamp = true;
            }
        }
        if (hasDate || hasDateTime) {
            imports.add("java.time.LocalDateTime");
        }
        if (hasCreationTimestamp) {
            imports.add("org.hibernate.annotations.CreationTimestamp");
        }
        if (hasUpdateTimestamp) {
            imports.add("org.hibernate.annotations.UpdateTimestamp");
        }
        
        for (String imp : imports) {
            content.append("import ").append(imp).append(";\n");
        }
        content.append("import lombok.*;\n\n");
        
        content.append("@Entity\n");
        content.append("@Table(name = \"").append(table.getTableName()).append("\")\n");
        content.append("@Data\n");
        content.append("@NoArgsConstructor\n");
        content.append("@AllArgsConstructor\n");
        content.append("public class ").append(className).append(" implements Serializable {\n\n");
        
        content.append("    private static final long serialVersionUID = 1L;\n\n");
        
        for (ColumnInfo column : table.getColumns()) {
            String fieldName = Generator.util.StringUtils.toCamelCase(column.getColumnName(), false);
            String colName = column.getColumnName().toLowerCase();
            
            if (column.isPrimaryKey()) {
                content.append("    @Id\n");
                content.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
            }
            if (isTimeType(column.getColumnType())) {
                if (colName.startsWith("create_") || colName.startsWith("created_")) {
                    content.append("    @CreationTimestamp\n");
                }
                if (colName.startsWith("update_") || colName.startsWith("updated_")) {
                    content.append("    @UpdateTimestamp\n");
                }
            }
            content.append("    @Column(name = \"").append(column.getColumnName()).append("\"");
            if (!column.isNullable() && !column.isPrimaryKey()) {
                content.append(", nullable = false");
            }
            content.append(")\n");
            
            String fieldType = TypeUtils.mapJavaType(column.getColumnType());
            content.append("    private ").append(fieldType).append(" ").append(fieldName).append(";\n\n");
        }
        
        content.append("}\n");
        
        Generator.util.FileUtils.writeToFile(filePath, content.toString());
    }
    
    private void generateRepository(TableInfo table) throws IOException {
        String className = Generator.util.StringUtils.toCamelCase(table.getTableName(), true);
        String repositoryName = className + "Repository";
        String packagePath = generatorConfig.getRepositoryPackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/" + repositoryName + ".java";
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getRepositoryPackage()).append(";\n\n");
        
        content.append("import ").append(generatorConfig.getEntityPackage()).append(".").append(className).append(";\n");
        content.append("import org.springframework.data.jpa.repository.JpaRepository;\n");
        content.append("import org.springframework.data.jpa.repository.JpaSpecificationExecutor;\n\n");
        
        content.append("import java.util.Optional;\n\n");
        
        content.append("public interface ").append(repositoryName).append(" extends JpaRepository<").append(className).append(", Long>, JpaSpecificationExecutor<").append(className).append("> {\n");
        
        if (getUserEntityClassName().equals(className)) {
            content.append("    Optional<").append(getUserEntityClassName()).append("> ")
                    .append(getFindByUsernameMethodName()).append("(String username);\n");
        }
        
        content.append("}\n");
        
        Generator.util.FileUtils.writeToFile(filePath, content.toString());
    }
    
    private void generateService(TableInfo table) throws IOException {
        String className = Generator.util.StringUtils.toCamelCase(table.getTableName(), true);
        String serviceName = className + "Service";
        String packagePath = generatorConfig.getServicePackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/" + serviceName + ".java";
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getServicePackage()).append(";\n\n");
        
        content.append("import ").append(generatorConfig.getEntityPackage()).append(".").append(className).append(";\n");
        content.append("import org.springframework.data.domain.Page;\n");
        content.append("import org.springframework.data.domain.Pageable;\n\n");
        
        content.append("import java.util.List;\n");
        content.append("import java.util.Optional;\n\n");
        
        content.append("public interface ").append(serviceName).append(" {\n");
        content.append("    List<").append(className).append("> findAll();\n");
        content.append("    Page<").append(className).append("> findAll(Pageable pageable);\n");
        content.append("    Optional<").append(className).append("> findById(Long id);\n");
        content.append("    ").append(className).append(" save(").append(className).append(" entity);\n");
        content.append("    void deleteById(Long id);\n");
        content.append("    Page<").append(className).append("> search(").append(className).append(" condition, Pageable pageable);\n");
        content.append("}\n");
        
        Generator.util.FileUtils.writeToFile(filePath, content.toString());
    }
    
    private void generateServiceImpl(TableInfo table) throws IOException {
        String className = Generator.util.StringUtils.toCamelCase(table.getTableName(), true);
        String serviceName = className + "Service";
        String serviceImplName = className + "ServiceImpl";
        String repositoryName = className + "Repository";
        String packagePath = generatorConfig.getServiceImplPackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/" + serviceImplName + ".java";
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getServiceImplPackage()).append(";\n\n");
        
        content.append("import ").append(generatorConfig.getEntityPackage()).append(".").append(className).append(";\n");
        content.append("import ").append(generatorConfig.getRepositoryPackage()).append(".").append(repositoryName).append(";\n");
        content.append("import ").append(generatorConfig.getServicePackage()).append(".").append(serviceName).append(";\n");
        content.append("import org.springframework.data.domain.Page;\n");
        content.append("import org.springframework.data.domain.Pageable;\n");
        content.append("import org.springframework.data.jpa.domain.Specification;\n");
        content.append("import org.springframework.stereotype.Service;\n\n");
        
        content.append("import java.util.List;\n");
        content.append("import java.util.Optional;\n\n");
        
        content.append("@Service\n");
        content.append("public class ").append(serviceImplName).append(" implements ").append(serviceName).append(" {\n\n");
        
        content.append("    private final ").append(repositoryName).append(" repository;\n\n");
        content.append("    public ").append(serviceImplName).append("(").append(repositoryName).append(" repository) {\n");
        content.append("        this.repository = repository;\n");
        content.append("    }\n\n");
        
        content.append("    @Override\n");
        content.append("    public List<").append(className).append("> findAll() {\n");
        content.append("        return repository.findAll();\n");
        content.append("    }\n\n");
        
        content.append("    @Override\n");
        content.append("    public Page<").append(className).append("> findAll(Pageable pageable) {\n");
        content.append("        return repository.findAll(pageable);\n");
        content.append("    }\n\n");
        
        content.append("    @Override\n");
        content.append("    public Optional<").append(className).append("> findById(Long id) {\n");
        content.append("        return repository.findById(id);\n");
        content.append("    }\n\n");
        
        content.append("    @Override\n");
        content.append("    public ").append(className).append(" save(").append(className).append(" entity) {\n");
        content.append("        return repository.save(entity);\n");
        content.append("    }\n\n");
        
        content.append("    @Override\n");
        content.append("    public void deleteById(Long id) {\n");
        content.append("        repository.deleteById(id);\n");
        content.append("    }\n\n");
        
        content.append("    @Override\n");
        content.append("    public Page<").append(className).append("> search(").append(className).append(" condition, Pageable pageable) {\n");
        content.append("        Specification<").append(className).append("> spec = (root, query, cb) -> {\n");
        content.append("            var predicates = cb.conjunction();\n");
        content.append("            return predicates;\n");
        content.append("        };\n");
        content.append("        return repository.findAll(spec, pageable);\n");
        content.append("    }\n");
        
        content.append("}\n");
        
        Generator.util.FileUtils.writeToFile(filePath, content.toString());
    }
    
    private void generateController(TableInfo table) throws IOException {
        String className = Generator.util.StringUtils.toCamelCase(table.getTableName(), true);
        String serviceName = className + "Service";
        String controllerName = className + "Controller";
        String entityName = Generator.util.StringUtils.toCamelCase(table.getTableName(), false);
        String packagePath = generatorConfig.getControllerPackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/" + controllerName + ".java";
        
        String dtoName = className + "DTO";
        String requestType = generatorConfig.isUseDTO() ? dtoName : className;
        String responseType = generatorConfig.isUseDTO() ? dtoName : className;
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getControllerPackage()).append(";\n\n");
        
        content.append("import ").append(generatorConfig.getEntityPackage()).append(".").append(className).append(";\n");
        content.append("import ").append(generatorConfig.getServicePackage()).append(".").append(serviceName).append(";\n");
        content.append("import ").append(generatorConfig.getCommonPackage()).append(".ApiResponse;\n");
        
        if (generatorConfig.isUseDTO()) {
            content.append("import ").append(generatorConfig.getDtoPackage()).append(".").append(dtoName).append(";\n");
        }
        
        content.append("import org.springframework.data.domain.Page;\n");
        content.append("import org.springframework.data.domain.PageRequest;\n");
        content.append("import org.springframework.data.domain.Pageable;\n");
        content.append("import org.springframework.web.bind.annotation.*;\n");
        content.append("import java.util.List;\n");
        
        boolean enableAuth = hasAuthTable && hasRoleField && authConfig.isEnableAllTableAuth();
        String adminRole = authConfig.getRoleAdmin();
        
        if (enableAuth) {
            content.append("import org.springframework.security.access.prepost.PreAuthorize;\n");
            content.append("import org.springframework.security.core.Authentication;\n");
            content.append("import org.springframework.security.core.context.SecurityContextHolder;\n");
        }

        if (enableAuth && authConfig.isUserOwnedTable(table.getTableName())) {
            content.append("import ").append(generatorConfig.getRepositoryPackage()).append(".").append(getUserEntityClassName()).append("Repository;\n");
        }
        
        content.append("\n");
        
        content.append("@RestController\n");
        content.append("@RequestMapping(\"").append(generatorConfig.getApiPath()).append("/").append(entityName).append("\")\n");
        content.append("public class ").append(controllerName).append(" {\n\n");
        
        boolean isUserOwned = enableAuth && authConfig.isUserOwnedTable(table.getTableName());

        content.append("    private final ").append(serviceName).append(" service;\n\n");

        if (isUserOwned) {
            content.append("    private final ").append(getUserEntityClassName()).append("Repository userRepository;\n\n");
        }

        content.append("    public ").append(controllerName).append("(").append(serviceName).append(" service");
        if (isUserOwned) {
            content.append(", ").append(getUserEntityClassName()).append("Repository userRepository");
        }
        content.append(") {\n");
        content.append("        this.service = service;\n");
        if (isUserOwned) {
            content.append("        this.userRepository = userRepository;\n");
        }
        content.append("    }\n\n");
        
        if (enableAuth) {
            content.append("    private String getCurrentUsername() {\n");
            content.append("        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();\n");
            content.append("        return authentication != null ? authentication.getName() : null;\n");
            content.append("    }\n\n");

            content.append("    private boolean isAdmin() {\n");
            content.append("        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();\n");
            content.append("        return authentication != null && authentication.getAuthorities().stream()\n");
            content.append("                .anyMatch(auth -> auth.getAuthority().equals(\"ROLE_").append(adminRole).append("\"));\n");
            content.append("    }\n\n");

            if (isUserOwned) {
                content.append("    private Long getCurrentUserId() {\n");
                content.append("        String username = getCurrentUsername();\n");
                content.append("        if (username == null) {\n");
                content.append("            throw new RuntimeException(\"未登录\");\n");
                content.append("        }\n");
                content.append("        return userRepository.").append(getFindByUsernameMethodName()).append("(username)\n");
                content.append("                .map(").append(getUserEntityClassName()).append("::getId)\n");
                content.append("                .orElseThrow(() -> new RuntimeException(\"当前用户不存在\"));\n");
                content.append("    }\n\n");
            }
        }
        
        content.append("    @GetMapping\n");
        content.append("    public ApiResponse<List<").append(responseType).append(">> getAll() {\n");
        content.append("        List<").append(className).append("> data = service.findAll();\n");
        if (generatorConfig.isUseDTO()) {
            content.append("        List<").append(dtoName).append("> dtoList = data.stream().map(this::toDTO).toList();\n");
            content.append("        return ApiResponse.success(dtoList);\n");
        } else {
            content.append("        return ApiResponse.success(data);\n");
        }
        content.append("    }\n\n");
        
        content.append("    @GetMapping(\"/page\")\n");
        content.append("    public ApiResponse<Page<").append(responseType).append(">> getByPage(\n");
        content.append("            @RequestParam(defaultValue = \"0\") int page,\n");
        content.append("            @RequestParam(defaultValue = \"10\") int size) {\n");
        content.append("        Pageable pageable = PageRequest.of(page, size);\n");
        content.append("        Page<").append(className).append("> data = service.findAll(pageable);\n");
        if (generatorConfig.isUseDTO()) {
            content.append("        Page<").append(dtoName).append("> dtoPage = data.map(this::toDTO);\n");
            content.append("        return ApiResponse.success(dtoPage);\n");
        } else {
            content.append("        return ApiResponse.success(data);\n");
        }
        content.append("    }\n\n");
        
        content.append("    @GetMapping(\"/{id}\")\n");
        content.append("    public ApiResponse<").append(responseType).append("> getById(@PathVariable Long id) {\n");
        content.append("        return service.findById(id)\n");
        if (generatorConfig.isUseDTO()) {
            content.append("                .map(entity -> ApiResponse.success(toDTO(entity)))\n");
        } else {
            content.append("                .map(ApiResponse::success)\n");
        }
        content.append("                .orElse(ApiResponse.error(404, \"数据不存在\"));\n");
        content.append("    }\n\n");
        
        if (enableAuth) {
            content.append("    @PreAuthorize(\"hasRole('").append(adminRole).append("')\")\n");
        }
        content.append("    @PostMapping\n");
        content.append("    public ApiResponse<").append(responseType).append("> create(@RequestBody ").append(requestType).append(" request) {\n");
        if (generatorConfig.isUseDTO()) {
            content.append("        ").append(className).append(" entity = toEntity(request);\n");
            content.append("        ").append(className).append(" data = service.save(entity);\n");
            content.append("        return ApiResponse.success(\"创建成功\", toDTO(data));\n");
        } else {
            content.append("        ").append(className).append(" data = service.save(request);\n");
            content.append("        return ApiResponse.success(\"创建成功\", data);\n");
        }
        content.append("    }\n\n");
        
        if (enableAuth) {
            content.append("    @PreAuthorize(\"isAuthenticated()\")\n");
        }
        content.append("    @PutMapping(\"/{id}\")\n");
        content.append("    public ApiResponse<").append(responseType).append("> update(@PathVariable Long id, @RequestBody ").append(requestType).append(" request) {\n");
        
        if (enableAuth) {
            if (getUserEntityClassName().equals(className)) {
                content.append("        String currentUsername = getCurrentUsername();\n");
                content.append("        return service.findById(id)\n");
                content.append("                .map(existing -> {\n");
                content.append("                    if (!isAdmin() && !existing.")
                        .append(getUserFieldGetter(authConfig.getUsernameField()))
                        .append("().equals(currentUsername)) {\n");
                content.append("                        throw new RuntimeException(\"只能修改自己的信息\");\n");
                content.append("                    }\n");
                
                for (ColumnInfo column : table.getColumns()) {
                    String fieldName = Generator.util.StringUtils.toCamelCase(column.getColumnName(), false);
                    if (!column.isPrimaryKey() && !isAutoFillField(column) && !isExcludedField(column)) {
                        content.append("                    existing.set").append(Generator.util.StringUtils.toCamelCase(column.getColumnName(), true))
                                .append("(request.get").append(Generator.util.StringUtils.toCamelCase(column.getColumnName(), true)).append("());\n");
                    }
                }
                
                content.append("                    ").append(className).append(" data = service.save(existing);\n");
                if (generatorConfig.isUseDTO()) {
                    content.append("                    return ApiResponse.success(\"更新成功\", toDTO(data));\n");
                } else {
                    content.append("                    return ApiResponse.success(\"更新成功\", data);\n");
                }
                content.append("                })\n");
                content.append("                .orElse(ApiResponse.error(404, \"数据不存在\"));\n");
            } else if (authConfig.isUserOwnedTable(table.getTableName())) {
                String userField = authConfig.getUserOwnedField(table.getTableName());
                String userFieldGetter = Generator.util.StringUtils.toCamelCase(userField, true);

                content.append("        return service.findById(id)\n");
                content.append("                .map(existing -> {\n");
                content.append("                    Long currentUserId = getCurrentUserId();\n");
                content.append("                    if (!isAdmin() && !existing.get").append(userFieldGetter).append("().equals(currentUserId)) {\n");
                content.append("                        throw new RuntimeException(\"只能修改自己的数据\");\n");
                content.append("                    }\n");
                
                for (ColumnInfo column : table.getColumns()) {
                    String fieldName = Generator.util.StringUtils.toCamelCase(column.getColumnName(), false);
                    if (!column.isPrimaryKey() && !isAutoFillField(column) && !isExcludedField(column)) {
                        content.append("                    existing.set").append(Generator.util.StringUtils.toCamelCase(column.getColumnName(), true))
                                .append("(request.get").append(Generator.util.StringUtils.toCamelCase(column.getColumnName(), true)).append("());\n");
                    }
                }
                
                content.append("                    ").append(className).append(" data = service.save(existing);\n");
                if (generatorConfig.isUseDTO()) {
                    content.append("                    return ApiResponse.success(\"更新成功\", toDTO(data));\n");
                } else {
                    content.append("                    return ApiResponse.success(\"更新成功\", data);\n");
                }
                content.append("                })\n");
                content.append("                .orElse(ApiResponse.error(404, \"数据不存在\"));\n");
            } else {
                content.append("        if (!isAdmin()) {\n");
                content.append("            return ApiResponse.error(403, \"权限不足\");\n");
                content.append("        }\n");
                content.append("        return service.findById(id)\n");
                content.append("                .map(existing -> {\n");
                
                for (ColumnInfo column : table.getColumns()) {
                    String fieldName = Generator.util.StringUtils.toCamelCase(column.getColumnName(), false);
                    if (!column.isPrimaryKey() && !isAutoFillField(column) && !isExcludedField(column)) {
                        content.append("                    existing.set").append(Generator.util.StringUtils.toCamelCase(column.getColumnName(), true))
                                .append("(request.get").append(Generator.util.StringUtils.toCamelCase(column.getColumnName(), true)).append("());\n");
                    }
                }
                
                content.append("                    ").append(className).append(" data = service.save(existing);\n");
                if (generatorConfig.isUseDTO()) {
                    content.append("                    return ApiResponse.success(\"更新成功\", toDTO(data));\n");
                } else {
                    content.append("                    return ApiResponse.success(\"更新成功\", data);\n");
                }
                content.append("                })\n");
                content.append("                .orElse(ApiResponse.error(404, \"数据不存在\"));\n");
            }
        } else {
            content.append("        return service.findById(id)\n");
            content.append("                .map(existing -> {\n");
            
            for (ColumnInfo column : table.getColumns()) {
                String fieldName = Generator.util.StringUtils.toCamelCase(column.getColumnName(), false);
                if (!column.isPrimaryKey() && !isAutoFillField(column) && !isExcludedField(column)) {
                    content.append("                    existing.set").append(Generator.util.StringUtils.toCamelCase(column.getColumnName(), true))
                            .append("(request.get").append(Generator.util.StringUtils.toCamelCase(column.getColumnName(), true)).append("());\n");
                }
            }
            
            content.append("                    ").append(className).append(" data = service.save(existing);\n");
            if (generatorConfig.isUseDTO()) {
                content.append("                    return ApiResponse.success(\"更新成功\", toDTO(data));\n");
            } else {
                content.append("                    return ApiResponse.success(\"更新成功\", data);\n");
            }
            content.append("                })\n");
            content.append("                .orElse(ApiResponse.error(404, \"数据不存在\"));\n");
        }
        content.append("    }\n\n");
        
        if (enableAuth) {
            if (getUserEntityClassName().equals(className)) {
                content.append("    @PreAuthorize(\"hasRole('").append(adminRole).append("')\")\n");
            } else if (authConfig.isUserOwnedTable(table.getTableName())) {
                content.append("    @PreAuthorize(\"isAuthenticated()\")\n");
            } else {
                content.append("    @PreAuthorize(\"hasRole('").append(adminRole).append("')\")\n");
            }
        }
        content.append("    @DeleteMapping(\"/{id}\")\n");
        content.append("    public ApiResponse<Void> delete(@PathVariable Long id) {\n");
        
        if (enableAuth && authConfig.isUserOwnedTable(table.getTableName())) {
            String userField = authConfig.getUserOwnedField(table.getTableName());
            String userFieldGetter = Generator.util.StringUtils.toCamelCase(userField, true);

            content.append("        return service.findById(id)\n");
            content.append("                .map(existing -> {\n");
            content.append("                    Long currentUserId = getCurrentUserId();\n");
            content.append("                    if (!isAdmin() && !existing.get").append(userFieldGetter).append("().equals(currentUserId)) {\n");
            content.append("                        throw new RuntimeException(\"只能删除自己的数据\");\n");
            content.append("                    }\n");
            content.append("                    service.deleteById(id);\n");
            content.append("                    return ApiResponse.success(\"删除成功\");\n");
            content.append("                })\n");
            content.append("                .orElse(ApiResponse.error(404, \"数据不存在\"));\n");
        } else {
            content.append("        service.deleteById(id);\n");
            content.append("        return ApiResponse.success(\"删除成功\");\n");
        }
        content.append("    }\n\n");
        
        content.append("    @PostMapping(\"/search\")\n");
        content.append("    public ApiResponse<Page<").append(responseType).append(">> search(\n");
        content.append("            @RequestBody ").append(requestType).append(" condition,\n");
        content.append("            @RequestParam(defaultValue = \"0\") int page,\n");
        content.append("            @RequestParam(defaultValue = \"10\") int size) {\n");
        content.append("        Pageable pageable = PageRequest.of(page, size);\n");
        if (generatorConfig.isUseDTO()) {
            content.append("        ").append(className).append(" entity = toEntity(condition);\n");
            content.append("        Page<").append(className).append("> data = service.search(entity, pageable);\n");
            content.append("        Page<").append(dtoName).append("> dtoPage = data.map(this::toDTO);\n");
            content.append("        return ApiResponse.success(dtoPage);\n");
        } else {
            content.append("        Page<").append(className).append("> data = service.search(condition, pageable);\n");
            content.append("        return ApiResponse.success(data);\n");
        }
        content.append("    }\n");
        
        if (generatorConfig.isUseDTO()) {
            content.append("\n");
            content.append("    private ").append(dtoName).append(" toDTO(").append(className).append(" entity) {\n");
            content.append("        ").append(dtoName).append(" dto = new ").append(dtoName).append("();\n");
            for (ColumnInfo column : table.getColumns()) {
                String fieldName = Generator.util.StringUtils.toCamelCase(column.getColumnName(), false);
                if (!isExcludedField(column)) {
                    content.append("        dto.set").append(Generator.util.StringUtils.toCamelCase(column.getColumnName(), true))
                            .append("(entity.get").append(Generator.util.StringUtils.toCamelCase(column.getColumnName(), true)).append("());\n");
                }
            }
            content.append("        return dto;\n");
            content.append("    }\n\n");
            
            content.append("    private ").append(className).append(" toEntity(").append(dtoName).append(" dto) {\n");
            content.append("        ").append(className).append(" entity = new ").append(className).append("();\n");
            for (ColumnInfo column : table.getColumns()) {
                String fieldName = Generator.util.StringUtils.toCamelCase(column.getColumnName(), false);
                if (!isAutoFillField(column) && !isExcludedField(column)) {
                    content.append("        entity.set").append(Generator.util.StringUtils.toCamelCase(column.getColumnName(), true))
                            .append("(dto.get").append(Generator.util.StringUtils.toCamelCase(column.getColumnName(), true)).append("());\n");
                }
            }
            content.append("        return entity;\n");
            content.append("    }\n");
        }
        
        content.append("}\n");
        
        Generator.util.FileUtils.writeToFile(filePath, content.toString());
    }
    
    private void generateDTO(TableInfo table) throws IOException {
        String className = Generator.util.StringUtils.toCamelCase(table.getTableName(), true);
        String dtoName = className + "DTO";
        String packagePath = generatorConfig.getDtoPackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/" + dtoName + ".java";
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getDtoPackage()).append(";\n\n");
        
        Set<String> imports = new TreeSet<>();
        boolean hasDate = false;
        boolean hasDateTime = false;
        for (ColumnInfo column : table.getColumns()) {
            String fieldName = Generator.util.StringUtils.toCamelCase(column.getColumnName(), false);
            if (isExcludedField(column)) {
                continue;
            }
            if ("DATE".equals(column.getColumnType())) hasDate = true;
            if ("DATETIME".equals(column.getColumnType()) || "TIMESTAMP".equals(column.getColumnType())) hasDateTime = true;
        }
        if (hasDate || hasDateTime) {
            imports.add("java.time.LocalDateTime");
        }
        
        for (String imp : imports) {
            content.append("import ").append(imp).append(";\n");
        }
        content.append("import lombok.*;\n\n");
        
        content.append("@Data\n");
        content.append("@NoArgsConstructor\n");
        content.append("@AllArgsConstructor\n");
        content.append("public class ").append(dtoName).append(" {\n\n");
        
        for (ColumnInfo column : table.getColumns()) {
            String fieldName = Generator.util.StringUtils.toCamelCase(column.getColumnName(), false);
            if (isExcludedField(column)) {
                continue;
            }
            
            String fieldType = TypeUtils.mapJavaType(column.getColumnType());
            content.append("    private ").append(fieldType).append(" ").append(fieldName).append(";\n\n");
        }
        
        content.append("}\n");
        
        Generator.util.FileUtils.writeToFile(filePath, content.toString());
    }
    
    private boolean isTimeType(String columnType) {
        return "DATETIME".equalsIgnoreCase(columnType) || "TIMESTAMP".equalsIgnoreCase(columnType) || "DATE".equalsIgnoreCase(columnType);
    }
    
    private boolean isAutoFillField(ColumnInfo column) {
        if (!isTimeType(column.getColumnType())) return false;
        String name = column.getColumnName().toLowerCase();
        return name.startsWith("create_") || name.startsWith("created_")
            || name.startsWith("update_") || name.startsWith("updated_");
    }

    private String getUserEntityClassName() {
        return Generator.util.StringUtils.toCamelCase(authConfig.getTableName(), true);
    }

    private String getFindByUsernameMethodName() {
        return "findBy" + Generator.util.StringUtils.toCamelCase(authConfig.getUsernameField(), true);
    }

    private String getUserFieldGetter(String fieldName) {
        return "get" + Generator.util.StringUtils.toCamelCase(fieldName, true);
    }

    private boolean isExcludedField(ColumnInfo column) {
        String fieldName = Generator.util.StringUtils.toCamelCase(column.getColumnName(), false);
        return generatorConfig.getExcludedFields().contains(fieldName)
                || generatorConfig.getExcludedFields().contains(column.getColumnName())
                || authConfig.getPasswordField().equalsIgnoreCase(column.getColumnName());
    }
}
