package Generator.generator;

import Generator.config.GeneratorConfig;
import Generator.model.ColumnInfo;
import Generator.model.TableInfo;
import Generator.util.TypeUtils;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class EntityGenerator extends BaseGenerator {
    
    public EntityGenerator(GeneratorConfig config) {
        super(config);
    }
    
    @Override
    public void generate(TableInfo table) throws IOException {
        String className = toCamelCase(table.getTableName(), true);
        String packagePath = getPackagePath(config.getEntityPackage());
        String filePath = config.getOutputPath() + "/" + packagePath + "/" + className + ".java";
        
        StringBuilder content = new StringBuilder();
        content.append("package ").append(config.getEntityPackage()).append(";\n\n");
        
        Set<String> imports = new TreeSet<>();
        imports.add("jakarta.persistence.*");
        imports.add("java.io.Serializable");
        
        boolean hasDate = false;
        boolean hasDateTime = false;
        for (ColumnInfo column : table.getColumns()) {
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
        
        content.append("@Entity\n");
        content.append("@Table(name = \"").append(table.getTableName()).append("\")\n");
        content.append("@Data\n");
        content.append("@NoArgsConstructor\n");
        content.append("@AllArgsConstructor\n");
        content.append("public class ").append(className).append(" implements Serializable {\n\n");
        
        content.append("    private static final long serialVersionUID = 1L;\n\n");
        
        for (ColumnInfo column : table.getColumns()) {
            String fieldName = toCamelCase(column.getColumnName(), false);
            if (config.getExcludedFields().contains(fieldName) || config.getExcludedFields().contains(column.getColumnName())) {
                continue;
            }
            
            if (column.isPrimaryKey()) {
                content.append("    @Id\n");
                content.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
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
        
        writeToFile(filePath, content.toString());
    }
}
