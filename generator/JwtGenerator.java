package Generator.generator;

import Generator.config.AuthConfig;
import Generator.config.GeneratorConfig;
import Generator.config.JwtConfig;
import Generator.model.ColumnInfo;
import Generator.model.TableInfo;
import Generator.util.FileUtils;
import Generator.util.TypeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtGenerator {
    private final GeneratorConfig generatorConfig;
    private final JwtConfig jwtConfig;
    private final AuthConfig authConfig;
    private final String roleFieldJavaType;
    private final List<ColumnInfo> nonNullDefaultFields;

    public JwtGenerator(GeneratorConfig generatorConfig, JwtConfig jwtConfig, AuthConfig authConfig, List<TableInfo> tables) {
        this.generatorConfig = generatorConfig;
        this.jwtConfig = jwtConfig;
        this.authConfig = authConfig;
        this.roleFieldJavaType = resolveFieldJavaType(tables, authConfig.getTableName(), authConfig.getRoleField(), "String");
        validateConfiguredRoleValues();
        this.nonNullDefaultFields = computeNonNullDefaultFields(tables);
    }

    private String getUserEntityName() {
        return Generator.util.StringUtils.toCamelCase(authConfig.getTableName(), true);
    }

    private String getUserFieldGetter(String fieldName) {
        return "get" + Generator.util.StringUtils.toCamelCase(fieldName, true);
    }

    private String getUserFieldSetter(String fieldName) {
        return "set" + Generator.util.StringUtils.toCamelCase(fieldName, true);
    }

    private String getFindByUsernameMethodName() {
        return "findBy" + Generator.util.StringUtils.toCamelCase(authConfig.getUsernameField(), true);
    }

    private String resolveFieldJavaType(List<TableInfo> tables, String tableName, String fieldName, String defaultType) {
        for (TableInfo table : tables) {
            if (!table.getTableName().equalsIgnoreCase(tableName)) {
                continue;
            }
            for (ColumnInfo column : table.getColumns()) {
                if (column.getColumnName().equalsIgnoreCase(fieldName)) {
                    return TypeUtils.mapJavaType(column.getColumnType());
                }
            }
        }
        return defaultType;
    }

    private String convertStringExpressionToRoleType(String expression) {
        return switch (roleFieldJavaType) {
            case "Byte" -> "Byte.valueOf(" + expression + ")";
            case "Short" -> "Short.valueOf(" + expression + ")";
            case "Integer" -> "Integer.valueOf(" + expression + ")";
            case "Long" -> "Long.valueOf(" + expression + ")";
            case "Float" -> "Float.valueOf(" + expression + ")";
            case "Double" -> "Double.valueOf(" + expression + ")";
            case "java.math.BigDecimal" -> "new java.math.BigDecimal(" + expression + ")";
            case "Boolean" -> "Boolean.valueOf(" + expression + ")";
            default -> expression;
        };
    }

    private String getRoleDefaultLiteral() {
        String value = authConfig.getRoleUser();
        return switch (roleFieldJavaType) {
            case "Byte" -> "(byte) " + Byte.valueOf(value);
            case "Short" -> "(short) " + Short.valueOf(value);
            case "Integer" -> Integer.valueOf(value).toString();
            case "Long" -> Long.valueOf(value) + "L";
            case "Float" -> Float.valueOf(value) + "f";
            case "Double" -> Double.valueOf(value).toString();
            case "java.math.BigDecimal" -> "new java.math.BigDecimal(\"" + value + "\")";
            case "Boolean" -> Boolean.valueOf(value).toString();
            default -> "\"" + escapeJava(value) + "\"";
        };
    }

    private boolean isAutoFillField(ColumnInfo column) {
        String type = column.getColumnType().toUpperCase();
        if (!"DATETIME".equals(type) && !"TIMESTAMP".equals(type) && !"DATE".equals(type)) {
            return false;
        }
        String name = column.getColumnName().toLowerCase();
        return name.startsWith("create_") || name.startsWith("created_")
            || name.startsWith("update_") || name.startsWith("updated_");
    }

    private String getDefaultLiteralForType(String javaType) {
        return switch (javaType) {
            case "Byte" -> "(byte) 1";
            case "Short" -> "(short) 1";
            case "Integer" -> "1";
            case "Long" -> "1L";
            case "Float" -> "1.0f";
            case "Double" -> "1.0";
            case "java.math.BigDecimal" -> "java.math.BigDecimal.ZERO";
            case "Boolean" -> "true";
            case "String" -> "\"\"";
            case "java.time.LocalDate" -> "java.time.LocalDate.now()";
            case "java.time.LocalTime" -> "java.time.LocalTime.now()";
            case "java.time.LocalDateTime" -> "java.time.LocalDateTime.now()";
            default -> "null";
        };
    }

    private List<ColumnInfo> computeNonNullDefaultFields(List<TableInfo> tables) {
        List<ColumnInfo> result = new ArrayList<>();
        for (TableInfo table : tables) {
            if (!table.getTableName().equalsIgnoreCase(authConfig.getTableName())) {
                continue;
            }
            for (ColumnInfo column : table.getColumns()) {
                if (column.isPrimaryKey()) continue;
                if (column.isNullable()) continue;
                if (isAutoFillField(column)) continue;
                if (column.getColumnName().equalsIgnoreCase(authConfig.getUsernameField())) continue;
                if (column.getColumnName().equalsIgnoreCase(authConfig.getPasswordField())) continue;
                if (column.getColumnName().equalsIgnoreCase(authConfig.getRoleField())) continue;
                result.add(column);
            }
        }
        return result;
    }

    private String escapeJava(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void validateConfiguredRoleValues() {
        if ("String".equals(roleFieldJavaType)) {
            return;
        }
        if (!isCompatibleRoleValue(authConfig.getRoleAdmin())
                || !isCompatibleRoleValue(authConfig.getRoleUser())
                || !isCompatibleRoleValue(authConfig.getRoleGuest())) {
            throw new IllegalArgumentException(
                    "\n========================================\n"
                    + " 配置错误: " + authConfig.getRoleField() + " 字段在数据库中类型为 " + roleFieldJavaType + "\n"
                    + " 但 roleAdmin/roleUser/roleGuest 配置了非数值字符串，\n"
                    + " 会导致运行时 NumberFormatException。\n"
                    + " 请在 Main.main() 中将它们设置为实际数据库值：\n"
                    + "   例如: authConfig.setRoleUser(\"1\");\n"
                    + "         authConfig.setRoleAdmin(\"2\");\n"
                    + "         authConfig.setRoleGuest(\"0\");\n"
                    + "========================================"
            );
        }
    }

    private boolean isCompatibleRoleValue(String value) {
        try {
            switch (roleFieldJavaType) {
                case "Byte" -> Byte.valueOf(value);
                case "Short" -> Short.valueOf(value);
                case "Integer" -> Integer.valueOf(value);
                case "Long" -> Long.valueOf(value);
                case "Float" -> Float.valueOf(value);
                case "Double" -> Double.valueOf(value);
                case "java.math.BigDecimal" -> new java.math.BigDecimal(value);
                case "Boolean" -> {
                    if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                        return false;
                    }
                }
                default -> {
                    return true;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void generateJwtUtils() throws IOException {
        String packagePath = generatorConfig.getCommonPackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/JwtUtils.java";
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getCommonPackage()).append(";\n\n");
        
        content.append("import io.jsonwebtoken.*;\n");
        content.append("import org.springframework.beans.factory.annotation.Value;\n");
        content.append("import org.springframework.stereotype.Component;\n\n");
        
        content.append("import java.security.KeyFactory;\n");
        content.append("import java.security.PrivateKey;\n");
        content.append("import java.security.PublicKey;\n");
        content.append("import java.security.spec.PKCS8EncodedKeySpec;\n");
        content.append("import java.security.spec.X509EncodedKeySpec;\n");
        content.append("import java.util.Base64;\n");
        content.append("import java.util.Date;\n");
        content.append("import java.util.HashMap;\n");
        content.append("import java.util.Map;\n\n");
        
        content.append("@Component\n");
        content.append("public class JwtUtils {\n\n");
        
        content.append("    @Value(\"${jwt.private-key}\")\n");
        content.append("    private String privateKeyStr;\n\n");
        
        content.append("    @Value(\"${jwt.public-key}\")\n");
        content.append("    private String publicKeyStr;\n\n");
        
        content.append("    @Value(\"${jwt.expiration}\")\n");
        content.append("    private Long expiration;\n\n");
        
        content.append("    private PrivateKey getPrivateKey() {\n");
        content.append("        try {\n");
        content.append("            byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);\n");
        content.append("            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);\n");
        content.append("            KeyFactory keyFactory = KeyFactory.getInstance(\"EC\");\n");
        content.append("            return keyFactory.generatePrivate(keySpec);\n");
        content.append("        } catch (Exception e) {\n");
        content.append("            throw new RuntimeException(\"Failed to load private key\", e);\n");
        content.append("        }\n");
        content.append("    }\n\n");
        
        content.append("    private PublicKey getPublicKey() {\n");
        content.append("        try {\n");
        content.append("            byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);\n");
        content.append("            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);\n");
        content.append("            KeyFactory keyFactory = KeyFactory.getInstance(\"EC\");\n");
        content.append("            return keyFactory.generatePublic(keySpec);\n");
        content.append("        } catch (Exception e) {\n");
        content.append("            throw new RuntimeException(\"Failed to load public key\", e);\n");
        content.append("        }\n");
        content.append("    }\n\n");
        
        content.append("    public String generateToken(String username) {\n");
        content.append("        Map<String, Object> claims = new HashMap<>();\n");
        content.append("        return createToken(claims, username);\n");
        content.append("    }\n\n");
        
        content.append("    public String generateToken(String username, Map<String, Object> claims) {\n");
        content.append("        return createToken(claims, username);\n");
        content.append("    }\n\n");
        
        content.append("    private String createToken(Map<String, Object> claims, String subject) {\n");
        content.append("        return Jwts.builder()\n");
        content.append("                .setClaims(claims)\n");
        content.append("                .setSubject(subject)\n");
        content.append("                .setIssuedAt(new Date(System.currentTimeMillis()))\n");
        content.append("                .setExpiration(new Date(System.currentTimeMillis() + expiration))\n");
        content.append("                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)\n");
        content.append("                .compact();\n");
        content.append("    }\n\n");
        
        content.append("    public String extractUsername(String token) {\n");
        content.append("        return extractClaim(token, Claims::getSubject);\n");
        content.append("    }\n\n");
        
        content.append("    public Date extractExpiration(String token) {\n");
        content.append("        return extractClaim(token, Claims::getExpiration);\n");
        content.append("    }\n\n");
        
        content.append("    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {\n");
        content.append("        final Claims claims = extractAllClaims(token);\n");
        content.append("        return claimsResolver.apply(claims);\n");
        content.append("    }\n\n");
        
        content.append("    private Claims extractAllClaims(String token) {\n");
        content.append("        return Jwts.parserBuilder()\n");
        content.append("                .setSigningKey(getPublicKey())\n");
        content.append("                .build()\n");
        content.append("                .parseClaimsJws(token)\n");
        content.append("                .getBody();\n");
        content.append("    }\n\n");
        
        content.append("    private Boolean isTokenExpired(String token) {\n");
        content.append("        return extractExpiration(token).before(new Date());\n");
        content.append("    }\n\n");
        
        content.append("    public Boolean validateToken(String token, String username) {\n");
        content.append("        final String extractedUsername = extractUsername(token);\n");
        content.append("        return (extractedUsername.equals(username) && !isTokenExpired(token));\n");
        content.append("    }\n");
        content.append("}\n");
        
        FileUtils.writeToFile(filePath, content.toString());
    }
    
    public void generateJwtAuthenticationFilter() throws IOException {
        String packagePath = generatorConfig.getCommonPackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/JwtAuthenticationFilter.java";
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getCommonPackage()).append(";\n\n");
        
        content.append("import jakarta.servlet.FilterChain;\n");
        content.append("import jakarta.servlet.ServletException;\n");
        content.append("import jakarta.servlet.http.HttpServletRequest;\n");
        content.append("import jakarta.servlet.http.HttpServletResponse;\n");
        content.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        content.append("import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;\n");
        content.append("import org.springframework.security.core.context.SecurityContextHolder;\n");
        content.append("import org.springframework.security.core.userdetails.UserDetails;\n");
        content.append("import org.springframework.security.core.userdetails.UserDetailsService;\n");
        content.append("import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;\n");
        content.append("import org.springframework.stereotype.Component;\n");
        content.append("import org.springframework.web.filter.OncePerRequestFilter;\n\n");
        
        content.append("import java.io.IOException;\n\n");
        
        content.append("@Component\n");
        content.append("public class JwtAuthenticationFilter extends OncePerRequestFilter {\n\n");
        
        content.append("    @Autowired\n");
        content.append("    private JwtUtils jwtUtils;\n\n");
        
        content.append("    @Autowired\n");
        content.append("    private UserDetailsService userDetailsService;\n\n");
        
        content.append("    @Override\n");
        content.append("    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)\n");
        content.append("            throws ServletException, IOException {\n\n");
        
        content.append("        final String authorizationHeader = request.getHeader(\"Authorization\");\n\n");
        
        content.append("        String username = null;\n");
        content.append("        String jwt = null;\n\n");
        
        content.append("        if (authorizationHeader != null && authorizationHeader.startsWith(\"Bearer \")) {\n");
        content.append("            jwt = authorizationHeader.substring(7);\n");
        content.append("            try {\n");
        content.append("                username = jwtUtils.extractUsername(jwt);\n");
        content.append("            } catch (Exception e) {\n");
        content.append("                logger.error(\"Error extracting username from token\", e);\n");
        content.append("            }\n");
        content.append("        }\n\n");
        
        content.append("        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {\n");
        content.append("            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);\n\n");
        
        content.append("            if (jwtUtils.validateToken(jwt, userDetails.getUsername())) {\n");
        content.append("                UsernamePasswordAuthenticationToken authenticationToken =\n");
        content.append("                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());\n");
        content.append("                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));\n");
        content.append("                SecurityContextHolder.getContext().setAuthentication(authenticationToken);\n");
        content.append("            }\n");
        content.append("        }\n\n");
        
        content.append("        filterChain.doFilter(request, response);\n");
        content.append("    }\n");
        content.append("}\n");
        
        FileUtils.writeToFile(filePath, content.toString());
    }
    
    public void generateSecurityConfig() throws IOException {
        String packagePath = generatorConfig.getCommonPackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/SecurityConfig.java";
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getCommonPackage()).append(";\n\n");
        
        content.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        content.append("import org.springframework.context.annotation.Bean;\n");
        content.append("import org.springframework.context.annotation.Configuration;\n");
        content.append("import org.springframework.security.authentication.AuthenticationManager;\n");
        content.append("import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;\n");
        content.append("import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;\n");
        content.append("import org.springframework.security.config.annotation.web.builders.HttpSecurity;\n");
        content.append("import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;\n");
        content.append("import org.springframework.security.config.http.SessionCreationPolicy;\n");
        content.append("import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;\n");
        content.append("import org.springframework.security.crypto.password.PasswordEncoder;\n");
        content.append("import org.springframework.security.web.SecurityFilterChain;\n");
        content.append("import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;\n");
        content.append("import org.springframework.web.cors.CorsConfiguration;\n");
        content.append("import org.springframework.web.cors.CorsConfigurationSource;\n");
        content.append("import org.springframework.web.cors.UrlBasedCorsConfigurationSource;\n\n");
        content.append("import java.util.Arrays;\n\n");
        
        content.append("@Configuration\n");
        content.append("@EnableWebSecurity\n");
        content.append("@EnableMethodSecurity\n");
        content.append("public class SecurityConfig {\n\n");
        
        content.append("    @Autowired\n");
        content.append("    private JwtAuthenticationFilter jwtAuthenticationFilter;\n\n");
        
        content.append("    @Bean\n");
        content.append("    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {\n");
        content.append("        http\n");
        content.append("                .cors(cors -> cors.configurationSource(corsConfigurationSource()))\n");
        content.append("                .csrf(csrf -> csrf.disable())\n");
        content.append("                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))\n");
        content.append("                .authorizeHttpRequests(auth -> auth\n");
        content.append("                        .requestMatchers(\"").append(generatorConfig.getApiPath()).append("/auth/**\").permitAll()\n");
        content.append("                        .anyRequest().authenticated()\n");
        content.append("                )\n");
        content.append("                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);\n\n");
        
        content.append("        return http.build();\n");
        content.append("    }\n\n");
        content.append("    @Bean\n");
        content.append("    public CorsConfigurationSource corsConfigurationSource() {\n");
        content.append("        CorsConfiguration configuration = new CorsConfiguration();\n");
        content.append("        configuration.setAllowedOriginPatterns(Arrays.asList(\"*\"));\n");
        content.append("        configuration.setAllowedMethods(Arrays.asList(\"GET\", \"POST\", \"PUT\", \"DELETE\", \"OPTIONS\"));\n");
        content.append("        configuration.setAllowedHeaders(Arrays.asList(\"*\"));\n");
        content.append("        configuration.setAllowCredentials(true);\n");
        content.append("        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();\n");
        content.append("        source.registerCorsConfiguration(\"/**\", configuration);\n");
        content.append("        return source;\n");
        content.append("    }\n\n");
        
        content.append("    @Bean\n");
        content.append("    public PasswordEncoder passwordEncoder() {\n");
        content.append("        return new BCryptPasswordEncoder();\n");
        content.append("    }\n\n");
        
        content.append("    @Bean\n");
        content.append("    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {\n");
        content.append("        return authConfig.getAuthenticationManager();\n");
        content.append("    }\n");
        content.append("}\n");
        
        FileUtils.writeToFile(filePath, content.toString());
    }
    
    public void generateAuthController() throws IOException {
        String packagePath = generatorConfig.getControllerPackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/AuthController.java";
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getControllerPackage()).append(";\n\n");
        
        content.append("import ").append(generatorConfig.getCommonPackage()).append(".ApiResponse;\n");
        content.append("import ").append(generatorConfig.getCommonPackage()).append(".JwtUtils;\n");
        content.append("import ").append(generatorConfig.getEntityPackage()).append(".").append(getUserEntityName()).append(";\n");
        content.append("import ").append(generatorConfig.getRepositoryPackage()).append(".").append(getUserEntityName()).append("Repository;\n");
        content.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        content.append("import org.springframework.security.authentication.AuthenticationManager;\n");
        content.append("import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;\n");
        content.append("import org.springframework.security.core.Authentication;\n");
        content.append("import org.springframework.security.core.AuthenticationException;\n");
        content.append("import org.springframework.security.crypto.password.PasswordEncoder;\n");
        content.append("import org.springframework.web.bind.annotation.*;\n\n");

        content.append("import java.util.HashMap;\n");
        content.append("import java.util.Map;\n\n");

        content.append("@RestController\n");
        content.append("@RequestMapping(\"").append(generatorConfig.getApiPath()).append("/auth\")\n");
        content.append("public class AuthController {\n\n");

        content.append("    @Autowired\n");
        content.append("    private AuthenticationManager authenticationManager;\n\n");

        content.append("    @Autowired\n");
        content.append("    private JwtUtils jwtUtils;\n\n");

        content.append("    @Autowired\n");
        content.append("    private ").append(getUserEntityName()).append("Repository userRepository;\n\n");
        
        content.append("    @Autowired\n");
        content.append("    private PasswordEncoder passwordEncoder;\n\n");
        
        content.append("    @PostMapping(\"/login\")\n");
        content.append("    public ApiResponse<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {\n");
        content.append("        try {\n");
        content.append("            Authentication authentication = authenticationManager.authenticate(\n");
        content.append("                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())\n");
        content.append("            );\n\n");
        
        content.append("            String token = jwtUtils.generateToken(loginRequest.getUsername());\n\n");
        
        content.append("            Map<String, String> response = new HashMap<>();\n");
        content.append("            response.put(\"token\", token);\n");
        content.append("            response.put(\"username\", loginRequest.getUsername());\n\n");
        
        content.append("            return ApiResponse.success(\"登录成功\", response);\n");
        content.append("        } catch (AuthenticationException e) {\n");
        content.append("            return ApiResponse.error(401, \"用户名或密码错误\");\n");
        content.append("        }\n");
        content.append("    }\n\n");
        
        content.append("    @PostMapping(\"/register\")\n");
        content.append("    public ApiResponse<").append(getUserEntityName()).append("> register(@RequestBody RegisterRequest registerRequest) {\n");
        content.append("        if (userRepository.").append(getFindByUsernameMethodName()).append("(registerRequest.getUsername()).isPresent()) {\n");
        content.append("            return ApiResponse.error(400, \"用户名已存在\");\n");
        content.append("        }\n\n");

        content.append("        ").append(getUserEntityName()).append(" user = new ").append(getUserEntityName()).append("();\n");
        content.append("        user.").append(getUserFieldSetter(authConfig.getUsernameField())).append("(registerRequest.getUsername());\n");
        content.append("        user.").append(getUserFieldSetter(authConfig.getPasswordField())).append("(passwordEncoder.encode(registerRequest.getPassword()));\n");
        if (!"String".equals(roleFieldJavaType)) {
            content.append("        user.").append(getUserFieldSetter(authConfig.getRoleField()))
                    .append("(registerRequest.getRole() != null ? registerRequest.getRole() : ")
                    .append(getRoleDefaultLiteral()).append(");\n\n");
        } else {
            String roleValueExpression = "registerRequest.getRole() != null ? registerRequest.getRole() : \"" + escapeJava(authConfig.getRoleUser()) + "\"";
            content.append("        user.").append(getUserFieldSetter(authConfig.getRoleField())).append("(")
                    .append(convertStringExpressionToRoleType(roleValueExpression)).append(");\n\n");
        }
        for (ColumnInfo col : nonNullDefaultFields) {
            String javaType = TypeUtils.mapJavaType(col.getColumnType());
            content.append("        user.").append(getUserFieldSetter(col.getColumnName()))
                    .append("(").append(getDefaultLiteralForType(javaType)).append(");\n");
        }
        content.append("\n        ").append(getUserEntityName()).append(" savedUser = userRepository.save(user);\n");
        content.append("        return ApiResponse.success(\"注册成功\", savedUser);\n");
        content.append("    }\n\n");
        
        content.append("    public static class LoginRequest {\n");
        content.append("        private String username;\n");
        content.append("        private String password;\n\n");
        
        content.append("        public String getUsername() { return username; }\n");
        content.append("        public void setUsername(String username) { this.username = username; }\n");
        content.append("        public String getPassword() { return password; }\n");
        content.append("        public void setPassword(String password) { this.password = password; }\n");
        content.append("    }\n\n");
        
        content.append("    public static class RegisterRequest {\n");
        content.append("        private String username;\n");
        content.append("        private String password;\n");
        if ("String".equals(roleFieldJavaType)) {
            content.append("        private String role;\n\n");
        } else {
            content.append("        private ").append(roleFieldJavaType).append(" role;\n\n");
        }
        
        content.append("        public String getUsername() { return username; }\n");
        content.append("        public void setUsername(String username) { this.username = username; }\n");
        content.append("        public String getPassword() { return password; }\n");
        content.append("        public void setPassword(String password) { this.password = password; }\n");
        if ("String".equals(roleFieldJavaType)) {
            content.append("        public String getRole() { return role; }\n");
            content.append("        public void setRole(String role) { this.role = role; }\n");
        } else {
            content.append("        public ").append(roleFieldJavaType).append(" getRole() { return role; }\n");
            content.append("        public void setRole(").append(roleFieldJavaType).append(" role) { this.role = role; }\n");
        }
        content.append("    }\n");
        content.append("}\n");
        
        FileUtils.writeToFile(filePath, content.toString());
    }
    
    public void generateUserDetailsService() throws IOException {
        String packagePath = generatorConfig.getServicePackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/CustomUserDetailsService.java";
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getServicePackage()).append(";\n\n");
        
        content.append("import ").append(generatorConfig.getEntityPackage()).append(".").append(getUserEntityName()).append(";\n");
        content.append("import ").append(generatorConfig.getRepositoryPackage()).append(".").append(getUserEntityName()).append("Repository;\n");
        content.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        content.append("import org.springframework.security.core.authority.SimpleGrantedAuthority;\n");
        content.append("import org.springframework.security.core.userdetails.UserDetails;\n");
        content.append("import org.springframework.security.core.userdetails.UserDetailsService;\n");
        content.append("import org.springframework.security.core.userdetails.UsernameNotFoundException;\n");
        content.append("import org.springframework.stereotype.Service;\n\n");

        content.append("import java.util.Collections;\n\n");

        content.append("@Service\n");
        content.append("public class CustomUserDetailsService implements UserDetailsService {\n\n");

        content.append("    @Autowired\n");
        content.append("    private ").append(getUserEntityName()).append("Repository userRepository;\n\n");

        content.append("    @Override\n");
        content.append("    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {\n");
        content.append("        ").append(getUserEntityName()).append(" user = userRepository.").append(getFindByUsernameMethodName()).append("(username)\n");
        content.append("                .orElseThrow(() -> new UsernameNotFoundException(\"用户不存在: \" + username));\n\n");
        
        content.append("        return new org.springframework.security.core.userdetails.User(\n");
        content.append("                user.").append(getUserFieldGetter(authConfig.getUsernameField())).append("(),\n");
        content.append("                user.").append(getUserFieldGetter(authConfig.getPasswordField())).append("(),\n");
        content.append("                Collections.singletonList(new SimpleGrantedAuthority(\"ROLE_\" + user.").append(getUserFieldGetter(authConfig.getRoleField())).append("()))\n");
        content.append("        );\n");
        content.append("    }\n");
        content.append("}\n");
        
        FileUtils.writeToFile(filePath, content.toString());
    }
    
    public void generateGlobalExceptionHandler() throws IOException {
        String packagePath = generatorConfig.getCommonPackage().replace(".", "/");
        String filePath = generatorConfig.getOutputPath() + "/" + packagePath + "/GlobalExceptionHandler.java";
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(generatorConfig.getCommonPackage()).append(";\n\n");
        
        content.append("import org.springframework.http.HttpStatus;\n");
        content.append("import org.springframework.web.bind.annotation.ExceptionHandler;\n");
        content.append("import org.springframework.web.bind.annotation.ResponseStatus;\n");
        content.append("import org.springframework.web.bind.annotation.RestControllerAdvice;\n\n");
        
        content.append("@RestControllerAdvice\n");
        content.append("public class GlobalExceptionHandler {\n\n");
        
        content.append("    @ExceptionHandler(RuntimeException.class)\n");
        content.append("    @ResponseStatus(HttpStatus.FORBIDDEN)\n");
        content.append("    public ApiResponse<Void> handleRuntimeException(RuntimeException e) {\n");
        content.append("        return ApiResponse.error(403, e.getMessage());\n");
        content.append("    }\n\n");
        
        content.append("    @ExceptionHandler(Exception.class)\n");
        content.append("    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)\n");
        content.append("    public ApiResponse<Void> handleException(Exception e) {\n");
        content.append("        return ApiResponse.error(500, \"服务器内部错误：\" + e.getMessage());\n");
        content.append("    }\n");
        content.append("}\n");
        
        FileUtils.writeToFile(filePath, content.toString());
    }
}
