import javax.json.*;
import java.io.StringReader;

/**
 * Utility class for parsing JSON responses from the weather API.
 */
public class JsonParser {
    
    /**
     * Parses a JSON string and returns a JsonObject.
     * 
     * @param jsonString The JSON string to parse
     * @return A JsonObject representing the parsed JSON
     * @throws Exception If there is an error parsing the JSON
     */
    public static JsonObject parseJson(String jsonString) throws Exception {
        try (JsonReader reader = Json.createReader(new StringReader(jsonString))) {
            return reader.readObject();
        } catch (Exception e) {
            throw new Exception("Error parsing JSON: " + e.getMessage());
        }
    }
    
    /**
     * Safely extracts a string value from a JsonObject.
     * 
     * @param json The JsonObject to extract from
     * @param key The key to extract
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The string value or the default value
     */
    public static String getString(JsonObject json, String key, String defaultValue) {
        return json.containsKey(key) ? json.getString(key) : defaultValue;
    }
    
    /**
     * Safely extracts a double value from a JsonObject.
     * 
     * @param json The JsonObject to extract from
     * @param key The key to extract
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The double value or the default value
     */
    public static double getDouble(JsonObject json, String key, double defaultValue) {
        return json.containsKey(key) ? json.getJsonNumber(key).doubleValue() : defaultValue;
    }
    
    /**
     * Safely extracts an integer value from a JsonObject.
     * 
     * @param json The JsonObject to extract from
     * @param key The key to extract
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The integer value or the default value
     */
    public static int getInt(JsonObject json, String key, int defaultValue) {
        return json.containsKey(key) ? json.getInt(key) : defaultValue;
    }
    
    /**
     * Safely extracts a long value from a JsonObject.
     * 
     * @param json The JsonObject to extract from
     * @param key The key to extract
     * @param defaultValue The default value to return if the key doesn't exist
     * @return The long value or the default value
     */
    public static long getLong(JsonObject json, String key, long defaultValue) {
        return json.containsKey(key) ? json.getJsonNumber(key).longValue() : defaultValue;
    }
    
    /**
     * Safely extracts a nested JsonObject from a JsonObject.
     * 
     * @param json The JsonObject to extract from
     * @param key The key to extract
     * @return The JsonObject or null if it doesn't exist
     */
    public static JsonObject getJsonObject(JsonObject json, String key) {
        return json.containsKey(key) ? json.getJsonObject(key) : null;
    }
    
    /**
     * Safely extracts a JsonArray from a JsonObject.
     * 
     * @param json The JsonObject to extract from
     * @param key The key to extract
     * @return The JsonArray or null if it doesn't exist
     */
    public static JsonArray getJsonArray(JsonObject json, String key) {
        return json.containsKey(key) ? json.getJsonArray(key) : null;
    }
}