package Generator.util;

public class TypeUtils {
    public static String mapJavaType(String sqlType) {
        return switch (sqlType.toUpperCase()) {
            case "TINYINT" -> "Byte";
            case "SMALLINT" -> "Short";
            case "INT", "INTEGER" -> "Integer";
            case "BIGINT" -> "Long";
            case "FLOAT" -> "Float";
            case "DOUBLE" -> "Double";
            case "DECIMAL", "NUMERIC" -> "java.math.BigDecimal";
            case "BIT", "BOOLEAN" -> "Boolean";
            case "CHAR", "VARCHAR", "TEXT", "LONGTEXT" -> "String";
            case "DATE" -> "java.time.LocalDate";
            case "TIME" -> "java.time.LocalTime";
            case "DATETIME", "TIMESTAMP" -> "java.time.LocalDateTime";
            case "BLOB", "LONGBLOB" -> "byte[]";
            default -> "Object";
        };
    }
}
