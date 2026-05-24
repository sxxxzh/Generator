package Generator.util;

public class StringUtils {
    public static String toCamelCase(String str, boolean capitalizeFirst) {
        StringBuilder result = new StringBuilder();
        boolean nextUpper = capitalizeFirst;
        
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            
            if (c == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        
        return result.toString();
    }
}
