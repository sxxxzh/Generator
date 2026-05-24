package Generator.model;

public class ColumnInfo {
    private final String columnName;
    private final String columnType;
    private final boolean nullable;
    private final boolean primaryKey;
    private final String comment;
    
    public ColumnInfo(String columnName, String columnType, boolean nullable, boolean primaryKey, String comment) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.nullable = nullable;
        this.primaryKey = primaryKey;
        this.comment = comment;
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    public String getColumnType() {
        return columnType;
    }
    
    public boolean isNullable() {
        return nullable;
    }
    
    public boolean isPrimaryKey() {
        return primaryKey;
    }
    
    public String getComment() {
        return comment;
    }
}
