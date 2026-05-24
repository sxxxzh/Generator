package Generator;

import Generator.config.AuthConfig;
import Generator.config.DatabaseConfig;
import Generator.config.GeneratorConfig;
import Generator.config.JwtConfig;
import Generator.generator.CodeGenerator;
import Generator.generator.JwtGenerator;
import Generator.generator.WebTemplateGenerator;
import Generator.metadata.DatabaseMetadataReader;
import Generator.model.TableInfo;
import Generator.util.FileUtils;
import Generator.util.KeyPairGeneratorUtil;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DatabaseConfig dbConfig = new DatabaseConfig();
        GeneratorConfig generatorConfig = new GeneratorConfig();
        AuthConfig authConfig = new AuthConfig();
        JwtConfig jwtConfig = new JwtConfig();
        
        System.out.println("========================================");
        System.out.println("   Spring Boot CRUD 代码生成器启动");
        System.out.println("========================================");
        System.out.println("数据库配置:");
        System.out.println("  - 主机: " + dbConfig.getHost());
        System.out.println("  - 端口: " + dbConfig.getPort());
        System.out.println("  - 数据库: " + dbConfig.getName());
        System.out.println("代码生成配置:");
        System.out.println("  - 基硌包: " + generatorConfig.getBasePackage());
        System.out.println("  - 使用 DTO: " + generatorConfig.isUseDTO() + (generatorConfig.isUseDTO() ? " (Controller 使用 DTO)" : " (Controller 使用 Entity)"));
        System.out.println("  - 排除字段: " + generatorConfig.getExcludedFields());
        System.out.println("  - 选择表: " + (generatorConfig.getSelectedTables().isEmpty() ? "全部" : generatorConfig.getSelectedTables()));
        System.out.println("  - API 地址: " + generatorConfig.getApiBase());
        System.out.println("  - API 路径: " + generatorConfig.getApiPath());
        System.out.println("权限验证配置:");
        System.out.println("  - 表名: " + authConfig.getTableName());
        System.out.println("  - 角色字段: " + authConfig.getRoleField());
        System.out.println("  - 角色等级: " + authConfig.getRoleAdmin() + " > " + authConfig.getRoleUser() + " > " + authConfig.getRoleGuest());
        System.out.println("  - 全表权限验证: " + authConfig.isEnableAllTableAuth());
        System.out.println("  - 用户所有权表: " + (authConfig.getUserOwnedTables().isEmpty() ? "无" : authConfig.getUserOwnedTables()));
        System.out.println("JWT 配置:");
        System.out.println("  - 算法: " + jwtConfig.getAlgorithm());
        System.out.println("  - 过期时间: " + jwtConfig.getExpiration() + "ms");
        System.out.println("  - 自动生成密钥对: " + jwtConfig.isAutoGenerateKeyPair());
        System.out.println("========================================");
        
        try {
            if (jwtConfig.isAutoGenerateKeyPair()) {
                System.out.println("\n正在生成 ECC 密钥对...");
                KeyPairGeneratorUtil.KeyPairResult keyPair = KeyPairGeneratorUtil.generateECKeyPair();
                jwtConfig.setPrivateKey(keyPair.getPrivateKey());
                jwtConfig.setPublicKey(keyPair.getPublicKey());
                System.out.println("✓ ECC 密钥对生成完成");
            }

            System.out.println("正在生成 application.yml (多环境)...");
            generateApplicationYml(dbConfig, jwtConfig);
            System.out.println("✓ application*.yml 生成完成");
            
            DatabaseMetadataReader metadataReader = new DatabaseMetadataReader(dbConfig);
            Connection connection = metadataReader.getConnection();
            
            System.out.println("✓ 数据库连接成功");
            
            List<TableInfo> tables = metadataReader.getAllTables(connection);
            
            System.out.println("✓ 发现 " + tables.size() + " 个表");
            
            if (!generatorConfig.getSelectedTables().isEmpty()) {
                tables = tables.stream()
                    .filter(t -> generatorConfig.getSelectedTables().contains(t.getTableName()))
                    .toList();
                System.out.println("✓ 选择生成 " + tables.size() + " 个表");
            }
            
            CodeGenerator codeGenerator = new CodeGenerator(generatorConfig, authConfig);
            codeGenerator.checkAuthTable(tables);
            
            System.out.println("\n正在生成公共类...");
            codeGenerator.generateCommonClasses();
            System.out.println("✓ 公共类生成完成");

            System.out.println("\n正在生成 pom.xml...");
            codeGenerator.generatePomXml();
            System.out.println("✓ pom.xml 检查完成");
            
            System.out.println("\n正在生成 JWT 相关代码...");
            JwtGenerator jwtGenerator = new JwtGenerator(generatorConfig, jwtConfig);
            jwtGenerator.generateJwtUtils();
            jwtGenerator.generateJwtAuthenticationFilter();
            jwtGenerator.generateSecurityConfig();
            jwtGenerator.generateAuthController();
            jwtGenerator.generateUserDetailsService();
            jwtGenerator.generateGlobalExceptionHandler();
            System.out.println("✓ JWT 相关代码生成完成");
            
            for (TableInfo table : tables) {
                System.out.println("\n正在生成表 [" + table.getTableName() + "] 的代码...");
                codeGenerator.generateAll(table);
                System.out.println("✓ 表 [" + table.getTableName() + "] 代码生成完成");
            }
            
            System.out.println("\n正在生成 Web 模板...");
            WebTemplateGenerator webTemplateGenerator = new WebTemplateGenerator(generatorConfig, authConfig, jwtConfig);
            webTemplateGenerator.generateAll(tables);
            
            connection.close();
            
            System.out.println("\n========================================");
            System.out.println("   所有代码生成完成！");
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void generateApplicationYml(DatabaseConfig dbConfig, JwtConfig jwtConfig) throws IOException {
        String ymlDir = "src/main/resources/";

        writeYmlFile(ymlDir + "application.yml", buildMainYml());

        writeYmlFile(ymlDir + "application-dev.yml", buildEnvYml(dbConfig, jwtConfig, "dev", "8080", "update", "true"));

        writeYmlFile(ymlDir + "application-prod.yml", buildEnvYml(dbConfig, jwtConfig, "prod", "80", "none", "false"));

        writeYmlFile(ymlDir + "application-test.yml", buildEnvYml(dbConfig, jwtConfig, "test", "8081", "create-drop", "true"));
    }

    private static String buildMainYml() {
        StringBuilder content = new StringBuilder();
        content.append("spring:\n");
        content.append("  application:\n");
        content.append("    name: SpringBoot\n");
        content.append("    version: 1.0.0\n");
        content.append("  profiles:\n");
        content.append("    active: dev\n");
        content.append("  web:\n");
        content.append("    resources:\n");
        content.append("      static-locations: classpath:/static/\n");
        content.append("      add-mappings: true\n");
        return content.toString();
    }

    private static String buildEnvYml(DatabaseConfig dbConfig, JwtConfig jwtConfig, String env, String port, String ddlAuto, String showSql) {
        String dbName = env.equals("dev") ? dbConfig.getName() : dbConfig.getName() + "_" + env;

        StringBuilder content = new StringBuilder();
        content.append("spring:\n");
        content.append("  datasource:\n");
        content.append("    url: jdbc:mysql://").append(dbConfig.getHost()).append(":").append(dbConfig.getPort())
                .append("/").append(dbName).append("?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8\n");
        content.append("    username: ").append(dbConfig.getUser()).append("\n");
        content.append("    password: ").append(dbConfig.getPassword()).append("\n");
        content.append("    driver-class-name: com.mysql.cj.jdbc.Driver\n");
        content.append("  jpa:\n");
        content.append("    hibernate:\n");
        content.append("      ddl-auto: ").append(ddlAuto).append("\n");
        content.append("    show-sql: ").append(showSql).append("\n");
        content.append("    properties:\n");
        content.append("      hibernate:\n");
        content.append("        dialect: org.hibernate.dialect.MySQLDialect\n");
        content.append("        format_sql: true\n");
        content.append("\n");
        content.append("server:\n");
        content.append("  port: ").append(port).append("\n");
        content.append("\n");
        content.append("jwt:\n");
        content.append("  private-key: ").append(jwtConfig.getPrivateKey()).append("\n");
        content.append("  public-key: ").append(jwtConfig.getPublicKey()).append("\n");
        content.append("  expiration: ").append(jwtConfig.getExpiration()).append("\n");
        content.append("  token-header: Authorization\n");
        content.append("  token-prefix: Bearer \n");
        content.append("  algorithm: EC\n");
        return content.toString();
    }

    private static void writeYmlFile(String path, String content) throws IOException {
        FileUtils.writeToFile(path, content);
        System.out.println("  " + path.replace("src/main/resources/", ""));
    }
}
