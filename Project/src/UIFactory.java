import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Factory class for creating UI components with consistent styling.
 */
public class UIFactory {
    private static final Font TITLE_FONT = new Font("Roboto", Font.BOLD, 24);
    private static final Font HEADER_FONT = new Font("Roboto", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Roboto", Font.PLAIN, 14);
    private static final Font VALUE_FONT = new Font("Roboto", Font.BOLD, 16);
    
    private static final Color PRIMARY_COLOR = new Color(30, 136, 229); // Blue
    private static final Color SECONDARY_COLOR = new Color(117, 117, 117); // Gray
    
    /**
     * Creates a styled header panel.
     * 
     * @param title The title for the header
     * @param backgroundColor The background color
     * @return A styled header panel
     */
    public static JPanel createHeaderPanel(String title, Color backgroundColor) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        return panel;
    }
    
    /**
     * Creates a styled info card.
     * 
     * @param title The title for the card
     * @param value The value to display
     * @return A styled info card
     */
    public static JPanel createInfoCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            new EtchedBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(LABEL_FONT);
        titleLabel.setForeground(SECONDARY_COLOR);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(VALUE_FONT);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Creates a styled button.
     * 
     * @param text The text for the button
     * @param isPrimary Whether this is a primary button
     * @return A styled button
     */
    public static JButton createButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        
        if (isPrimary) {
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(null);
            button.setForeground(null);
        }
        
        button.setFocusPainted(false);
        return button;
    }
    
    /**
     * Creates a styled image icon from a URL.
     * 
     * @param url The URL for the image
     * @return An ImageIcon, or null if the image couldn't be loaded
     */
    public static ImageIcon createImageIcon(String url) {
        try {
            return new ImageIcon(ImageIO.read(new URL(url)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Returns a color based on the weather condition.
     * 
     * @param condition The weather condition
     * @return A color representing the condition
     */
    public static Color getWeatherColor(String condition) {
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
    
    /**
     * Creates a styled forecast card.
     * 
     * @param day The day of the forecast
     * @param iconUrl The URL for the weather icon
     * @param high The high temperature
     * @param low The low temperature
     * @param description The weather description
     * @return A styled forecast card
     */
    public static JPanel createForecastCard(String day, String iconUrl, 
                                           String high, String low, String description) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new EtchedBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Day
        JLabel dayLabel = new JLabel(day);
        dayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dayLabel.setFont(HEADER_FONT);
        
        // Icon
        JLabel iconLabel = new JLabel();
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon icon = createImageIcon(iconUrl);
        if (icon != null) {
            iconLabel.setIcon(icon);
        }
        
        // Temperature
        JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tempPanel.setOpaque(false);
        
        JLabel highLabel = new JLabel(high);
        highLabel.setFont(VALUE_FONT);
        
        JLabel lowLabel = new JLabel(low);
        lowLabel.setFont(LABEL_FONT);
        lowLabel.setForeground(SECONDARY_COLOR);
        
        tempPanel.add(highLabel);
        tempPanel.add(Box.createHorizontalStrut(10));
        tempPanel.add(lowLabel);
        
        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setFont(LABEL_FONT);
        
        // Add components
        panel.add(dayLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(tempPanel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(descLabel);
        
        return panel;
    }
}