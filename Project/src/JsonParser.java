/**
 * Utility class for parsing JSON responses from the weather API.
 */
public class JsonParser {
    
    /**
     * Extracts a value from JSON string based on the key.
     * 
     * @param json The JSON string to parse
     * @param key The key to find
     * @return The value associated with the key
     */
    public static String extractValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":";
            int start = json.indexOf(searchKey);
            if (start == -1) return "";
            
            start += searchKey.length();
            // Skip whitespace
            while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
                start++;
            }
            
            // Check if value is a string
            if (json.charAt(start) == '"') {
                start++; // Skip opening quote
                int end = json.indexOf("\"", start);
                return json.substring(start, end);
            } 
            // Value is a number or boolean
            else {
                int end = json.indexOf(",", start);
                if (end == -1) {
                    end = json.indexOf("}", start);
                }
                if (end == -1) return "";
                return json.substring(start, end).trim();
            }
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Extracts a nested object from JSON string.
     * 
     * @param json The JSON string to parse
     * @param key The key of the nested object
     * @return The nested object as a string
     */
    public static String extractObject(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":";
            int start = json.indexOf(searchKey);
            if (start == -1) return "{}";
            
            start += searchKey.length();
            // Skip whitespace
            while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
                start++;
            }
            
            if (json.charAt(start) != '{') return "{}";
            
            int count = 1;
            int end = start + 1;
            while (count > 0 && end < json.length()) {
                if (json.charAt(end) == '{') count++;
                if (json.charAt(end) == '}') count--;
                end++;
            }
            
            return json.substring(start, end);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    /**
     * Extracts the first item from a weather array.
     * 
     * @param json The JSON string to parse
     * @return The first weather item as a string
     */
    public static String extractFirstWeather(String json) {
        try {
            String weatherKey = "\"weather\":";
            int start = json.indexOf(weatherKey);
            if (start == -1) return "{}";
            
            start += weatherKey.length();
            // Skip to first object
            while (start < json.length() && json.charAt(start) != '{') {
                start++;
            }
            
            if (json.charAt(start) != '{') return "{}";
            
            int count = 1;
            int end = start + 1;
            while (count > 0 && end < json.length()) {
                if (json.charAt(end) == '{') count++;
                if (json.charAt(end) == '}') count--;
                end++;
            }
            
            return json.substring(start, end);
        } catch (Exception e) {
            return "{}";
        }
    }
}
