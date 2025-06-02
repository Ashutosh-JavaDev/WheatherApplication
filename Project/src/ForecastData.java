import java.util.Date;

/**
 * Class representing forecast weather data.
 */
public class ForecastData {
    private Date date;
    private double minTemp;
    private double maxTemp;
    private String description;
    private String icon;
    private String condition;
    
    /**
     * Default constructor.
     */
    public ForecastData() {
        // Initialize with default values
        this.date = new Date();
        this.minTemp = 0;
        this.maxTemp = 0;
        this.description = "";
        this.icon = "";
        this.condition = "";
    }
    
    // Getters and setters
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public double getMinTemp() { return minTemp; }
    public void setMinTemp(double minTemp) { this.minTemp = minTemp; }
    
    public double getMaxTemp() { return maxTemp; }
    public void setMaxTemp(double maxTemp) { this.maxTemp = maxTemp; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    @Override
    public String toString() {
        return "ForecastData{" +
                "date=" + date +
                ", minTemp=" + minTemp +
                ", maxTemp=" + maxTemp +
                ", condition='" + condition + '\'' +
                '}';
    }
}