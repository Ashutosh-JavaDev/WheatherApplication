import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class WeatherAppGUI extends JFrame {
    private static String API_KEY;
    private static String BASE_URL;
    static {
    try {
        InputStream input = WeatherAppGUI.class.getResourceAsStream("/config.properties");
        if (input == null) {
            throw new FileNotFoundException("config.properties not found in the classpath");
        }
        Properties props = new Properties();
        props.load(input);
        API_KEY = props.getProperty("api.key");
        BASE_URL = props.getProperty("api.url");
        input.close();
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Could not load configuration file.", "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}

        
    
    private JTextField cityField;
    private JLabel temperatureLabel;
    private JLabel weatherLabel;
    private JLabel humidityLabel;
    private JLabel windSpeedLabel;
    private JButton searchButton;
    private JPanel weatherPanel;
    
    public WeatherAppGUI() {
        setTitle("Weather Information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Search panel (North)
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        cityField = new JTextField();
        searchButton = new JButton("Search");
        searchPanel.add(new JLabel("City: "), BorderLayout.WEST);
        searchPanel.add(cityField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Weather info panel (Center)
        weatherPanel = new JPanel();
        weatherPanel.setLayout(new BoxLayout(weatherPanel, BoxLayout.Y_AXIS));
        weatherPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        temperatureLabel = new JLabel("Temperature: --");
        weatherLabel = new JLabel("Weather: --");
        humidityLabel = new JLabel("Humidity: --");
        windSpeedLabel = new JLabel("Wind Speed: --");
        
        // Add components with spacing
        weatherPanel.add(temperatureLabel);
        weatherPanel.add(Box.createVerticalStrut(10));
        weatherPanel.add(weatherLabel);
        weatherPanel.add(Box.createVerticalStrut(10));
        weatherPanel.add(humidityLabel);
        weatherPanel.add(Box.createVerticalStrut(10));
        weatherPanel.add(windSpeedLabel);
        
        // Add panels to main panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(weatherPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add action listener to search button
        searchButton.addActionListener(e -> fetchWeatherData());
        
        // Add action listener to text field (for Enter key)
        cityField.addActionListener(e -> fetchWeatherData());
    }
    
    private void fetchWeatherData() {
        
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a city name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show loading message
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        updateLabels("Loading...", "Loading...", "Loading...", "Loading...");
        
        // Create worker thread for API call
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                String urlString = String.format("%s?q=%s&appid=%s&units=metric", BASE_URL, city, API_KEY);
                URL url = new URL(urlString);
                
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                return response.toString();
            }
            
            @Override
            protected void done() {
                try {
                    String jsonData = get();
                    displayWeatherData(jsonData);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(WeatherAppGUI.this,
                            "Error fetching weather data: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    updateLabels("--", "--", "--", "--");
                }
                setCursor(Cursor.getDefaultCursor());
            }
        };
        
        worker.execute();
    }
    
    private void displayWeatherData(String jsonData) {
        try {
            // Extract temperature
            int tempStart = jsonData.indexOf("\"temp\":") + 7;
            int tempEnd = jsonData.indexOf(",", tempStart);
            String temperature = jsonData.substring(tempStart, tempEnd);
            
            // Extract weather description
            int descStart = jsonData.indexOf("\"description\":\"") + 14;
            int descEnd = jsonData.indexOf("\"", descStart);
            String description = jsonData.substring(descStart, descEnd);
            
            // Extract humidity
            int humStart = jsonData.indexOf("\"humidity\":") + 11;
            int humEnd = jsonData.indexOf(",", humStart);
            String humidity = jsonData.substring(humStart, humEnd);
            
            // Extract wind speed
            int windStart = jsonData.indexOf("\"speed\":") + 8;
            int windEnd = jsonData.indexOf(",", windStart);
            String windSpeed = jsonData.substring(windStart, windEnd);
            
            updateLabels(
                    "Temperature: " + temperature + "°C",
                    "Weather: " + description,
                    "Humidity: " + humidity + "%",
                    "Wind Speed: " + windSpeed + " m/s"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error parsing weather data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            updateLabels("--", "--", "--", "--");
        }
    }
    
    private void updateLabels(String temp, String weather, String humidity, String wind) {
        temperatureLabel.setText(temp);
        weatherLabel.setText(weather);
        humidityLabel.setText(humidity);
        windSpeedLabel.setText(wind);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeatherAppGUI app = new WeatherAppGUI();
            app.setVisible(true);
        });
    }
}