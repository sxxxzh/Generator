package Generator.metadata;

import Generator.config.DatabaseConfig;
import Generator.model.ColumnInfo;
import Generator.model.TableInfo;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class DatabaseMetadataReader {
    private final DatabaseConfig config;
    private static boolean driversLoaded = false;
    private static Driver mysqlDriver = null;

    public DatabaseMetadataReader(DatabaseConfig config) {
        this.config = config;
    }

    private static void ensureJarDriversLoaded() {
        if (driversLoaded) return;
        synchronized (DatabaseMetadataReader.class) {
            if (driversLoaded) return;
            try {
                File jarDir = findJarResourceDir();
                File[] jarFiles = jarDir.listFiles((d, n) -> n.endsWith(".jar"));
                if (jarFiles == null || jarFiles.length == 0) {
                    throw new RuntimeException("No JDBC driver JARs found in: " + jarDir.getAbsolutePath());
                }

                URL[] urls = new URL[jarFiles.length];
                for (int i = 0; i < jarFiles.length; i++) {
                    urls[i] = jarFiles[i].toURI().toURL();
                    System.out.println("  加载 JAR: " + jarFiles[i].getName());
                }

                URLClassLoader cl = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
                Class<?> driverClass = cl.loadClass("com.mysql.cj.jdbc.Driver");
                mysqlDriver = (Driver) driverClass.getDeclaredConstructor().newInstance();

                driversLoaded = true;
                System.out.println("  MySQL JDBC 驱动加载完成");
            } catch (Exception e) {
                throw new RuntimeException("Failed to load JDBC drivers from JarResource", e);
            }
        }
    }

    private static File findJarResourceDir() {
        File jarDir = new File("JarResource");
        if (jarDir.exists() && jarDir.isDirectory()) return jarDir;

        jarDir = new File("src/main/java/Generator/JarResource");
        if (jarDir.exists() && jarDir.isDirectory()) return jarDir;

        throw new RuntimeException("JarResource directory not found");
    }

    public Connection getConnection() throws SQLException {
        ensureJarDriversLoaded();
        Properties props = new Properties();
        props.setProperty("user", config.getUser());
        props.setProperty("password", config.getPassword());
        return mysqlDriver.connect(config.getUrl(), props);
    }
    
    public List<TableInfo> getAllTables(Connection connection) throws SQLException {
        String databaseName = config.getName();
        List<TableInfo> tables = new ArrayList<>();
        
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tableResultSet = metaData.getTables(databaseName, null, "%", new String[]{"TABLE"});
        
        while (tableResultSet.next()) {
            String tableName = tableResultSet.getString("TABLE_NAME");
            String tableComment = tableResultSet.getString("REMARKS");
            
            TableInfo table = new TableInfo(tableName, tableComment);
            
            Set<String> primaryKeys = new HashSet<>();
            ResultSet pkResultSet = metaData.getPrimaryKeys(databaseName, null, tableName);
            while (pkResultSet.next()) {
                primaryKeys.add(pkResultSet.getString("COLUMN_NAME"));
            }
            
            ResultSet columnResultSet = metaData.getColumns(databaseName, null, tableName, "%");
            while (columnResultSet.next()) {
                String columnName = columnResultSet.getString("COLUMN_NAME");
                String columnType = columnResultSet.getString("TYPE_NAME");
                boolean nullable = "YES".equals(columnResultSet.getString("IS_NULLABLE"));
                boolean isPrimaryKey = primaryKeys.contains(columnName);
                String comment = columnResultSet.getString("REMARKS");
                
                ColumnInfo column = new ColumnInfo(columnName, columnType, nullable, isPrimaryKey, comment);
                table.addColumn(column);
            }
            
            tables.add(table);
        }
        
        return tables;
    }
}
