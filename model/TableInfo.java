package Generator.model;

import java.util.ArrayList;
import java.util.List;

public class TableInfo {
    private final String tableName;
    private final String tableComment;
    private final List<ColumnInfo> columns = new ArrayList<>();
    
    public TableInfo(String tableName, String tableComment) {
        this.tableName = tableName;
        this.tableComment = tableComment;
    }
    
    public void addColumn(ColumnInfo column) {
        columns.add(column);
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public String getTableComment() {
        return tableComment;
    }
    
    public List<ColumnInfo> getColumns() {
        return columns;
    }
}
