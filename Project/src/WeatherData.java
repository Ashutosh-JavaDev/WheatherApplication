/**
 * Class representing current weather data.
 */
public class WeatherData {
    private String city;
    private String country;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private int pressure;
    private double windSpeed;
    private int windDirection;
    private String description;
    private String icon;
    private String condition;
    private long sunrise;
    private long sunset;
    
    /**
     * Default constructor.
     */
    public WeatherData() {
        // Initialize with default values
        this.city = "";
        this.country = "";
        this.temperature = 0;
        this.feelsLike = 0;
        this.humidity = 0;
        this.pressure = 0;
        this.windSpeed = 0;
        this.windDirection = 0;
        this.description = "";
        this.icon = "";
        this.condition = "";
        this.sunrise = 0;
        this.sunset = 0;
    }
    
    // Getters and setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    
    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }
    
    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }
    
    public int getPressure() { return pressure; }
    public void setPressure(int pressure) { this.pressure = pressure; }
    
    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    
    public int getWindDirection() { return windDirection; }
    public void setWindDirection(int windDirection) { this.windDirection = windDirection; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public long getSunrise() { return sunrise; }
    public void setSunrise(long sunrise) { this.sunrise = sunrise; }
    
    public long getSunset() { return sunset; }
    public void setSunset(long sunset) { this.sunset = sunset; }
    
    @Override
    public String toString() {
        return "WeatherData{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", temperature=" + temperature +
                ", condition='" + condition + '\'' +
                '}';
    }
}