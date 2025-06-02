import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.json.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for handling weather API requests and responses.
 */
public class WeatherService {
    private final String apiKey;
    private final String baseUrl;
    private final String forecastUrl = "https://api.openweathermap.org/data/2.5/forecast";
    private final String iconBaseUrl = "https://openweathermap.org/img/wn/";
    
    /**
     * Constructor for the WeatherService.
     * 
     * @param apiKey The API key for the weather service
     * @param baseUrl The base URL for the weather API
     */
    public WeatherService(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }
    
    /**
     * Fetches current weather data for a city.
     * 
     * @param city The city to fetch weather data for
     * @param units The units to use (metric or imperial)
     * @return A WeatherData object containing the current weather
     * @throws Exception If there is an error fetching or parsing the data
     */
    public WeatherData getCurrentWeather(String city, String units) throws Exception {
        String urlString = String.format("%s?q=%s&appid=%s&units=%s", baseUrl, city, apiKey, units);
        String jsonData = fetchData(urlString);
        return parseCurrentWeather(jsonData);
    }
    
    /**
     * Fetches 5-day forecast data for a city.
     * 
     * @param city The city to fetch forecast data for
     * @param units The units to use (metric or imperial)
     * @return A list of ForecastData objects containing the forecast
     * @throws Exception If there is an error fetching or parsing the data
     */
    public List<ForecastData> getForecast(String city, String units) throws Exception {
        String urlString = String.format("%s?q=%s&appid=%s&units=%s", forecastUrl, city, apiKey, units);
        String jsonData = fetchData(urlString);
        return parseForecast(jsonData);
    }
    
    /**
     * Fetches data from a URL.
     * 
     * @param urlString The URL to fetch data from
     * @return The response as a string
     * @throws Exception If there is an error fetching the data
     */
    private String fetchData(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Error fetching data: HTTP " + responseCode);
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return response.toString();
    }
    
    /**
     * Parses the current weather JSON data.
     * 
     * @param jsonData The JSON data to parse
     * @return A WeatherData object containing the current weather
     * @throws Exception If there is an error parsing the data
     */
    private WeatherData parseCurrentWeather(String jsonData) throws Exception {
        JsonObject json = JsonParser.parseJson(jsonData);
        
        WeatherData data = new WeatherData();
        data.setCity(json.getString("name"));
        
        JsonObject sys = JsonParser.getJsonObject(json, "sys");
        if (sys != null) {
            data.setCountry(JsonParser.getString(sys, "country", ""));
            data.setSunrise(JsonParser.getLong(sys, "sunrise", 0));
            data.setSunset(JsonParser.getLong(sys, "sunset", 0));
        }
        
        JsonObject main = JsonParser.getJsonObject(json, "main");
        if (main != null) {
            data.setTemperature(JsonParser.getDouble(main, "temp", 0));
            data.setFeelsLike(JsonParser.getDouble(main, "feels_like", 0));
            data.setHumidity(JsonParser.getInt(main, "humidity", 0));
            data.setPressure(JsonParser.getInt(main, "pressure", 0));
        }
        
        JsonObject wind = JsonParser.getJsonObject(json, "wind");
        if (wind != null) {
            data.setWindSpeed(JsonParser.getDouble(wind, "speed", 0));
            data.setWindDirection(JsonParser.getInt(wind, "deg", 0));
        }
        
        JsonArray weatherArray = JsonParser.getJsonArray(json, "weather");
        if (weatherArray != null && !weatherArray.isEmpty()) {
            JsonObject weather = weatherArray.getJsonObject(0);
            data.setDescription(JsonParser.getString(weather, "description", ""));
            data.setIcon(JsonParser.getString(weather, "icon", ""));
            data.setCondition(JsonParser.getString(weather, "main", ""));
        }
        
        return data;
    }
    
    /**
     * Parses the forecast JSON data.
     * 
     * @param jsonData The JSON data to parse
     * @return A list of ForecastData objects containing the forecast
     * @throws Exception If there is an error parsing the data
     */
    private List<ForecastData> parseForecast(String jsonData) throws Exception {
        JsonObject json = JsonParser.parseJson(jsonData);
        JsonArray list = JsonParser.getJsonArray(json, "list");
        
        if (list == null) {
            throw new Exception("Invalid forecast data format");
        }
        
        Map<String, ForecastData> dailyForecasts = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (int i = 0; i < list.size(); i++) {
            JsonObject item = list.getJsonObject(i);
            String dateTimeStr = item.getString("dt_txt");
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTimeStr);
            String dateStr = dateFormat.format(date);
            
            // Skip if we already have 5 days
            if (dailyForecasts.size() >= 5 && !dailyForecasts.containsKey(dateStr)) {
                continue;
            }
            
            // Create or update forecast for this day
            ForecastData forecast = dailyForecasts.getOrDefault(dateStr, new ForecastData());
            forecast.setDate(date);
            
            JsonObject main = JsonParser.getJsonObject(item, "main");
            JsonArray weatherArray = JsonParser.getJsonArray(item, "weather");
            
            if (main != null) {
                double temp = JsonParser.getDouble(main, "temp", 0);
                if (forecast.getMaxTemp() < temp) forecast.setMaxTemp(temp);
                if (forecast.getMinTemp() == 0 || forecast.getMinTemp() > temp) forecast.setMinTemp(temp);
            }
            
            // For noon forecast, use as the main display data
            if (dateTimeStr.contains("12:00") && weatherArray != null && !weatherArray.isEmpty()) {
                JsonObject weather = weatherArray.getJsonObject(0);
                forecast.setDescription(JsonParser.getString(weather, "description", ""));
                forecast.setIcon(JsonParser.getString(weather, "icon", ""));
                forecast.setCondition(JsonParser.getString(weather, "main", ""));
            }
            
            dailyForecasts.put(dateStr, forecast);
        }
        
        // Convert map to list
        List<ForecastData> result = new ArrayList<>(dailyForecasts.values());
        result.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        
        return result.subList(0, Math.min(5, result.size()));
    }
    
    /**
     * Gets the URL for a weather icon.
     * 
     * @param iconCode The icon code
     * @param size The size (@1x or @2x)
     * @return The URL for the icon
     */
    public String getIconUrl(String iconCode, String size) {
        return iconBaseUrl + iconCode + size + ".png";
    }
}