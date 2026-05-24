package Generator.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    public static void writeToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
        
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }

    public static void writeToFileIfAbsent(String filePath, String content) throws IOException {
        if (!fileExists(filePath)) {
            writeToFile(filePath, content);
        }
    }
}
