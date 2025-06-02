import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.json.*;

public class WeatherAppGUI extends JFrame {
    // Configuration constants
    private static String API_KEY;
    private static String BASE_URL;
    private static String ICON_BASE_URL = "https://openweathermap.org/img/wn/";
    
    // UI Components
    private JTextField cityField;
    private JButton searchButton;
    private JPanel weatherPanel;
    private JPanel forecastPanel;
    private JPanel favoritesPanel;
    private JComboBox<String> unitComboBox;
    private JComboBox<String> historyComboBox;
    private JTabbedPane tabbedPane;
    private JLabel statusLabel;
    
    // Data components
    private List<String> searchHistory = new ArrayList<>();
    private Set<String> favorites = new HashSet<>();
    private String currentUnit = "metric";
    
    // Initialize configuration
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

    public WeatherAppGUI() {
        setTitle("Weather Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Set custom font
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, 
                WeatherAppGUI.class.getResourceAsStream("/fonts/Roboto-Regular.ttf")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        createUI();
        loadSavedData();
    }
    
    private void createUI() {
        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Create tabbed pane for different views
        tabbedPane = new JTabbedPane();
        
        // Current weather panel
        weatherPanel = new JPanel();
        weatherPanel.setLayout(new BoxLayout(weatherPanel, BoxLayout.Y_AXIS));
        weatherPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Forecast panel
        forecastPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        forecastPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Favorites panel
        favoritesPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        favoritesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add panels to tabbed pane
        JScrollPane weatherScrollPane = new JScrollPane(weatherPanel);
        JScrollPane forecastScrollPane = new JScrollPane(forecastPanel);
        JScrollPane favoritesScrollPane = new JScrollPane(favoritesPanel);
        
        tabbedPane.addTab("Current Weather", new ImageIcon(), weatherScrollPane);
        tabbedPane.addTab("5-Day Forecast", new ImageIcon(), forecastScrollPane);
        tabbedPane.addTab("Favorites", new ImageIcon(), favoritesScrollPane);
        
        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel = new JLabel("Ready");
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        // Add components to main panel
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 10, 0),
            BorderFactory.createCompoundBorder(
                new EtchedBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
        ));
        
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        cityField = new JTextField();
        cityField.setFont(new Font("Roboto", Font.PLAIN, 14));
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Roboto", Font.BOLD, 14));
        searchButton.setBackground(new Color(30, 136, 229));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        inputPanel.add(new JLabel("City: "), BorderLayout.WEST);
        inputPanel.add(cityField, BorderLayout.CENTER);
        inputPanel.add(searchButton, BorderLayout.EAST);
        
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Unit selection
        String[] units = {"Celsius (°C)", "Fahrenheit (°F)"};
        unitComboBox = new JComboBox<>(units);
        unitComboBox.addActionListener(e -> {
            currentUnit = unitComboBox.getSelectedIndex() == 0 ? "metric" : "imperial";
            if (!cityField.getText().trim().isEmpty()) {
                fetchWeatherData();
            }
        });
        
        // Search history
        historyComboBox = new JComboBox<>(new DefaultComboBoxModel<>());
        historyComboBox.setPreferredSize(new Dimension(200, historyComboBox.getPreferredSize().height));
        historyComboBox.addActionListener(e -> {
            if (historyComboBox.getSelectedIndex() > 0) {
                cityField.setText((String) historyComboBox.getSelectedItem());
                fetchWeatherData();
            }
        });
        updateHistoryComboBox();
        
        optionsPanel.add(new JLabel("Units: "));
        optionsPanel.add(unitComboBox);
        optionsPanel.add(Box.createHorizontalStrut(20));
        optionsPanel.add(new JLabel("History: "));
        optionsPanel.add(historyComboBox);
        
        searchPanel.add(inputPanel, BorderLayout.NORTH);
        searchPanel.add(optionsPanel, BorderLayout.CENTER);
        
        // Add action listeners
        searchButton.addActionListener(e -> fetchWeatherData());
        cityField.addActionListener(e -> fetchWeatherData());
        
        return searchPanel;
    }
    
    private void fetchWeatherData() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a city name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Add to search history
        if (!searchHistory.contains(city)) {
            searchHistory.add(0, city);
            if (searchHistory.size() > 10) {
                searchHistory.remove(searchHistory.size() - 1);
            }
            updateHistoryComboBox();
            saveSearchHistory();
        }
        
        // Show loading message
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Loading weather data for " + city + "...");
        
        // Fetch current weather
        SwingWorker<WeatherData, Void> currentWeatherWorker = new SwingWorker<>() {
            @Override
            protected WeatherData doInBackground() throws Exception {
                return fetchCurrentWeather(city);
            }
            
            @Override
            protected void done() {
                try {
                    WeatherData data = get();
                    displayCurrentWeather(data);
                    statusLabel.setText("Current weather data loaded successfully");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(WeatherAppGUI.this,
                            "Error fetching current weather data: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    statusLabel.setText("Failed to load current weather data");
                }
                setCursor(Cursor.getDefaultCursor());
            }
        };
        
        // Fetch forecast
        SwingWorker<List<ForecastData>, Void> forecastWorker = new SwingWorker<>() {
            @Override
            protected List<ForecastData> doInBackground() throws Exception {
                return fetchForecast(city);
            }
            
            @Override
            protected void done() {
                try {
                    List<ForecastData> forecast = get();
                    displayForecast(forecast);
                    statusLabel.setText("Forecast data loaded successfully");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(WeatherAppGUI.this,
                            "Error fetching forecast data: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    statusLabel.setText("Failed to load forecast data");
                }
                setCursor(Cursor.getDefaultCursor());
            }
        };
        
        currentWeatherWorker.execute();
        forecastWorker.execute();
    }
    
    private WeatherData fetchCurrentWeather(String city) throws Exception {
        String urlString = String.format("%s?q=%s&appid=%s&units=%s", BASE_URL, city, API_KEY, currentUnit);
        URL url = new URL(urlString);
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        try (InputStream is = connection.getInputStream();
             JsonReader reader = Json.createReader(is)) {
            
            JsonObject json = reader.readObject();
            
            WeatherData data = new WeatherData();
            data.setCity(json.getString("name"));
            data.setCountry(json.getJsonObject("sys").getString("country"));
            
            JsonObject main = json.getJsonObject("main");
            data.setTemperature(main.getJsonNumber("temp").doubleValue());
            data.setFeelsLike(main.getJsonNumber("feels_like").doubleValue());
            data.setHumidity(main.getInt("humidity"));
            data.setPressure(main.getInt("pressure"));
            
            JsonObject wind = json.getJsonObject("wind");
            data.setWindSpeed(wind.getJsonNumber("speed").doubleValue());
            data.setWindDirection(wind.containsKey("deg") ? wind.getInt("deg") : 0);
            
            JsonArray weatherArray = json.getJsonArray("weather");
            JsonObject weather = weatherArray.getJsonObject(0);
            data.setDescription(weather.getString("description"));
            data.setIcon(weather.getString("icon"));
            data.setCondition(weather.getString("main"));
            
            data.setSunrise(json.getJsonObject("sys").getJsonNumber("sunrise").longValue());
            data.setSunset(json.getJsonObject("sys").getJsonNumber("sunset").longValue());
            
            return data;
        }
    }
    
    private List<ForecastData> fetchForecast(String city) throws Exception {
        String urlString = String.format("https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s&units=%s", 
                                        city, API_KEY, currentUnit);
        URL url = new URL(urlString);
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        try (InputStream is = connection.getInputStream();
             JsonReader reader = Json.createReader(is)) {
            
            JsonObject json = reader.readObject();
            JsonArray list = json.getJsonArray("list");
            
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
                
                JsonObject main = item.getJsonObject("main");
                JsonArray weatherArray = item.getJsonArray("weather");
                JsonObject weather = weatherArray.getJsonObject(0);
                
                // Update min/max temps
                double temp = main.getJsonNumber("temp").doubleValue();
                if (forecast.getMaxTemp() < temp) forecast.setMaxTemp(temp);
                if (forecast.getMinTemp() == 0 || forecast.getMinTemp() > temp) forecast.setMinTemp(temp);
                
                // For noon forecast, use as the main display data
                if (dateTimeStr.contains("12:00")) {
                    forecast.setDescription(weather.getString("description"));
                    forecast.setIcon(weather.getString("icon"));
                    forecast.setCondition(weather.getString("main"));
                }
                
                dailyForecasts.put(dateStr, forecast);
            }
            
            // Convert map to list and sort by date
            List<ForecastData> result = new ArrayList<>(dailyForecasts.values());
            Collections.sort(result, Comparator.comparing(ForecastData::getDate));
            
            return result.subList(0, Math.min(5, result.size()));
        }
    }
    
    private void displayCurrentWeather(WeatherData data) {
        weatherPanel.removeAll();
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(getWeatherColor(data.getCondition()));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // City name and add to favorites
        JPanel cityPanel = new JPanel(new BorderLayout());
        cityPanel.setOpaque(false);
        
        JLabel cityLabel = new JLabel(data.getCity() + ", " + data.getCountry());
        cityLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        cityLabel.setForeground(Color.WHITE);
        
        JButton favoriteButton = new JButton(favorites.contains(data.getCity()) ? "★" : "☆");
        favoriteButton.setFocusPainted(false);
        favoriteButton.setContentAreaFilled(false);
        favoriteButton.setBorderPainted(false);
        favoriteButton.setFont(new Font("Dialog", Font.BOLD, 24));
        favoriteButton.setForeground(Color.WHITE);
        favoriteButton.addActionListener(e -> toggleFavorite(data.getCity()));
        
        cityPanel.add(cityLabel, BorderLayout.CENTER);
        cityPanel.add(favoriteButton, BorderLayout.EAST);
        
        // Current conditions
        JPanel conditionsPanel = new JPanel(new BorderLayout(20, 0));
        conditionsPanel.setOpaque(false);
        
        // Temperature and icon
        JPanel tempIconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tempIconPanel.setOpaque(false);
        
        String unit = currentUnit.equals("metric") ? "°C" : "°F";
        JLabel tempLabel = new JLabel(String.format("%.1f%s", data.getTemperature(), unit));
        tempLabel.setFont(new Font("Roboto", Font.BOLD, 48));
        tempLabel.setForeground(Color.WHITE);
        
        JLabel iconLabel = new JLabel();
        try {
            URL iconUrl = new URL(ICON_BASE_URL + data.getIcon() + "@2x.png");
            ImageIcon icon = new ImageIcon(ImageIO.read(iconUrl));
            iconLabel.setIcon(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        tempIconPanel.add(tempLabel);
        tempIconPanel.add(iconLabel);
        
        // Description
        JLabel descLabel = new JLabel(capitalizeWords(data.getDescription()));
        descLabel.setFont(new Font("Roboto", Font.PLAIN, 18));
        descLabel.setForeground(Color.WHITE);
        
        conditionsPanel.add(tempIconPanel, BorderLayout.CENTER);
        conditionsPanel.add(descLabel, BorderLayout.SOUTH);
        
        // Add to header panel
        headerPanel.add(cityPanel, BorderLayout.NORTH);
        headerPanel.add(conditionsPanel, BorderLayout.CENTER);
        
        // Create details panel
        JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Add weather detail cards
        detailsPanel.add(createDetailCard("Feels Like", 
            String.format("%.1f%s", data.getFeelsLike(), unit), "thermometer"));
        detailsPanel.add(createDetailCard("Humidity", 
            data.getHumidity() + "%", "droplet"));
        detailsPanel.add(createDetailCard("Wind", 
            String.format("%.1f %s", data.getWindSpeed(), 
            currentUnit.equals("metric") ? "m/s" : "mph"), "wind"));
        detailsPanel.add(createDetailCard("Pressure", 
            data.getPressure() + " hPa", "bar-chart"));
        
        // Create sun times panel
        JPanel sunPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        sunPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), 
            "Sunrise & Sunset"));
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        
        JPanel sunrisePanel = new JPanel(new BorderLayout());
        sunrisePanel.add(new JLabel("Sunrise"), BorderLayout.NORTH);
        JLabel sunriseLabel = new JLabel(timeFormat.format(new Date(data.getSunrise() * 1000)));
        sunriseLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        sunrisePanel.add(sunriseLabel, BorderLayout.CENTER);
        
        JPanel sunsetPanel = new JPanel(new BorderLayout());
        sunsetPanel.add(new JLabel("Sunset"), BorderLayout.NORTH);
        JLabel sunsetLabel = new JLabel(timeFormat.format(new Date(data.getSunset() * 1000)));
        sunsetLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        sunsetPanel.add(sunsetLabel, BorderLayout.CENTER);
        
        sunPanel.add(sunrisePanel);
        sunPanel.add(sunsetPanel);
        
        // Add all panels to weather panel
        weatherPanel.add(headerPanel);
        weatherPanel.add(Box.createVerticalStrut(20));
        weatherPanel.add(detailsPanel);
        weatherPanel.add(Box.createVerticalStrut(20));
        weatherPanel.add(sunPanel);
        weatherPanel.add(Box.createVerticalGlue());
        
        weatherPanel.revalidate();
        weatherPanel.repaint();
    }
    
    private void displayForecast(List<ForecastData> forecast) {
        forecastPanel.removeAll();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
        String unit = currentUnit.equals("metric") ? "°C" : "°F";
        
        for (ForecastData day : forecast) {
            JPanel dayPanel = new JPanel();
            dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));
            dayPanel.setBorder(BorderFactory.createCompoundBorder(
                new EtchedBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            
            // Date
            JLabel dateLabel = new JLabel(dateFormat.format(day.getDate()));
            dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            dateLabel.setFont(new Font("Roboto", Font.BOLD, 14));
            
            // Icon
            JLabel iconLabel = new JLabel();
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            try {
                URL iconUrl = new URL(ICON_BASE_URL + day.getIcon() + ".png");
                ImageIcon icon = new ImageIcon(ImageIO.read(iconUrl));
                iconLabel.setIcon(icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Temperature
            JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            tempPanel.setOpaque(false);
            
            JLabel maxTempLabel = new JLabel(String.format("%.0f°", day.getMaxTemp()));
            maxTempLabel.setFont(new Font("Roboto", Font.BOLD, 16));
            
            JLabel minTempLabel = new JLabel(String.format("%.0f°", day.getMinTemp()));
            minTempLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
            minTempLabel.setForeground(Color.GRAY);
            
            tempPanel.add(maxTempLabel);
            tempPanel.add(Box.createHorizontalStrut(10));
            tempPanel.add(minTempLabel);
            
            // Description
            JLabel descLabel = new JLabel(capitalizeWords(day.getDescription()));
            descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            descLabel.setFont(new Font("Roboto", Font.PLAIN, 12));
            
            // Add components
            dayPanel.add(dateLabel);
            dayPanel.add(Box.createVerticalStrut(10));
            dayPanel.add(iconLabel);
            dayPanel.add(Box.createVerticalStrut(10));
            dayPanel.add(tempPanel);
            dayPanel.add(Box.createVerticalStrut(5));
            dayPanel.add(descLabel);
            
            forecastPanel.add(dayPanel);
        }
        
        forecastPanel.revalidate();
        forecastPanel.repaint();
    }
    
    private JPanel createDetailCard(String title, String value, String iconName) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            new EtchedBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void toggleFavorite(String city) {
        if (favorites.contains(city)) {
            favorites.remove(city);
        } else {
            favorites.add(city);
        }
        
        updateFavoritesPanel();
        saveFavorites();
        
        // Update favorite button in current weather display
        Component[] components = weatherPanel.getComponents();
        if (components.length > 0 && components[0] instanceof JPanel) {
            JPanel headerPanel = (JPanel) components[0];
            Component[] headerComponents = headerPanel.getComponents();
            if (headerComponents.length > 0 && headerComponents[0] instanceof JPanel) {
                JPanel cityPanel = (JPanel) headerComponents[0];
                Component[] cityComponents = cityPanel.getComponents();
                if (cityComponents.length > 1 && cityComponents[1] instanceof JButton) {
                    JButton favoriteButton = (JButton) cityComponents[1];
                    favoriteButton.setText(favorites.contains(city) ? "★" : "☆");
                }
            }
        }
    }
    
    private void updateFavoritesPanel() {
        favoritesPanel.removeAll();
        
        if (favorites.isEmpty()) {
            JLabel emptyLabel = new JLabel("No favorite cities added yet");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            favoritesPanel.add(emptyLabel);
        } else {
            for (String city : favorites) {
                JPanel cityPanel = new JPanel(new BorderLayout());
                cityPanel.setBorder(BorderFactory.createCompoundBorder(
                    new EtchedBorder(),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                
                JLabel cityLabel = new JLabel(city);
                cityLabel.setFont(new Font("Roboto", Font.BOLD, 16));
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                
                JButton viewButton = new JButton("View");
                viewButton.addActionListener(e -> {
                    cityField.setText(city);
                    fetchWeatherData();
                    tabbedPane.setSelectedIndex(0);
                });
                
                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(e -> toggleFavorite(city));
                
                buttonPanel.add(viewButton);
                buttonPanel.add(removeButton);
                
                cityPanel.add(cityLabel, BorderLayout.WEST);
                cityPanel.add(buttonPanel, BorderLayout.EAST);
                
                favoritesPanel.add(cityPanel);
            }
        }
        
        favoritesPanel.revalidate();
        favoritesPanel.repaint();
    }
    
    private void updateHistoryComboBox() {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) historyComboBox.getModel();
        model.removeAllElements();
        model.addElement("Recent Searches");
        
        for (String city : searchHistory) {
            model.addElement(city);
        }
        
        historyComboBox.setSelectedIndex(0);
    }
    
    private void loadSavedData() {
        // Load search history
        try {
            File historyFile = new File("search_history.txt");
            if (historyFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(historyFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        searchHistory.add(line);
                    }
                }
                updateHistoryComboBox();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Load favorites
        try {
            File favoritesFile = new File("favorites.txt");
            if (favoritesFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(favoritesFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        favorites.add(line);
                    }
                }
                updateFavoritesPanel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void saveSearchHistory() {
        try {
            File historyFile = new File("search_history.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(historyFile))) {
                for (String city : searchHistory) {
                    writer.write(city);
                    writer.newLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void saveFavorites() {
        try {
            File favoritesFile = new File("favorites.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(favoritesFile))) {
                for (String city : favorites) {
                    writer.write(city);
                    writer.newLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Color getWeatherColor(String condition) {
        switch (condition.toLowerCase()) {
            case "clear":
                return new Color(30, 136, 229); // Blue
            case "clouds":
                return new Color(117, 117, 117); // Gray
            case "rain":
            case "drizzle":
                return new Color(3, 169, 244); // Light Blue
            case "thunderstorm":
                return new Color(94, 53, 177); // Deep Purple
            case "snow":
                return new Color(129, 212, 250); // Light Blue
            case "mist":
            case "fog":
                return new Color(189, 189, 189); // Light Gray
            default:
                return new Color(30, 136, 229); // Default Blue
        }
    }
    
    private String capitalizeWords(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : text.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                c = Character.toUpperCase(c);
                capitalizeNext = false;
            }
            result.append(c);
        }
        
        return result.toString();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                WeatherAppGUI app = new WeatherAppGUI();
                app.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error starting application: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    // Data models
    private static class WeatherData {
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
    }
    
    private static class ForecastData {
        private Date date;
        private double minTemp;
        private double maxTemp;
        private String description;
        private String icon;
        private String condition;
        
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
    }
}