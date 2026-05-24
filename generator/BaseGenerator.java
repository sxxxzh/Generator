package Generator.generator;

import Generator.config.GeneratorConfig;
import Generator.model.TableInfo;
import Generator.util.FileUtils;
import Generator.util.StringUtils;

import java.io.IOException;

public abstract class BaseGenerator {
    protected final GeneratorConfig config;
    
    public BaseGenerator(GeneratorConfig config) {
        this.config = config;
    }
    
    public abstract void generate(TableInfo table) throws IOException;
    
    protected String toCamelCase(String str, boolean capitalizeFirst) {
        return StringUtils.toCamelCase(str, capitalizeFirst);
    }
    
    protected void writeToFile(String filePath, String content) throws IOException {
        FileUtils.writeToFile(filePath, content);
    }
    
    protected String getPackagePath(String packageName) {
        return packageName.replace(".", "/");
    }
}
