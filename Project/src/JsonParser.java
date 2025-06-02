import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Utility class for parsing JSON responses from the weather API.
 */
public class JsonParser {
    
    /**
     * Parses a JSON string and returns a JSONObject.
     * 
     * @param jsonString The JSON string to parse
     * @return A JSONObject representing the parsed JSON
     * @throws Exception If there is an error parsing the JSON
     */
    public static JSONObject parseJson(String jsonString) throws Exception {
        try {
            return new JSONObject(jsonString);
        } catch (Exception e) {
            throw new Exception("Error parsing JSON: " + e.getMessage());
        }
    }
    
    /**
     * Safely extracts a string value from a JSONObject.
     * 
     * @param json The JSONObject to extract from
     * @param key The key to extract
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The string value or the default value
     */
    public static String getString(JSONObject json, String key, String defaultValue) {
        return json.has(key) ? json.getString(key) : defaultValue;
    }
    
    /**
     * Safely extracts a double value from a JSONObject.
     * 
     * @param json The JSONObject to extract from
     * @param key The key to extract
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The double value or the default value
     */
    public static double getDouble(JSONObject json, String key, double defaultValue) {
        return json.has(key) ? json.getDouble(key) : defaultValue;
    }
    
    /**
     * Safely extracts an integer value from a JSONObject.
     * 
     * @param json The JSONObject to extract from
     * @param key The key to extract
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The integer value or the default value
     */
    public static int getInt(JSONObject json, String key, int defaultValue) {
        return json.has(key) ? json.getInt(key) : defaultValue;
    }
    
    /**
     * Safely extracts a long value from a JSONObject.
     * 
     * @param json The JSONObject to extract from
     * @param key The key to extract
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The long value or the default value
     */
    public static long getLong(JSONObject json, String key, long defaultValue) {
        return json.has(key) ? json.getLong(key) : defaultValue;
    }
    
    /**
     * Safely extracts a nested JSONObject from a JSONObject.
     * 
     * @param json The JSONObject to extract from
     * @param key The key to extract
     * @return The JSONObject or null if it doesn't exist
     */
    public static JSONObject getJsonObject(JSONObject json, String key) {
        return json.has(key) ? json.getJSONObject(key) : null;
    }
    
    /**
     * Safely extracts a JSONArray from a JSONObject.
     * 
     * @param json The JSONObject to extract from
     * @param key The key to extract
     * @return The JSONArray or null if it doesn't exist
     */
    public static JSONArray getJsonArray(JSONObject json, String key) {
        return json.has(key) ? json.getJSONArray(key) : null;
    }
}