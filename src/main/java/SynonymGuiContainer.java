import javax.swing.*;
import java.awt.*;

/**
 * A container GUI class that manages and switches between different synonym-related interfaces.
 * This class serves as the main container for multiple synonym exploration tools, providing
 * a toggle mechanism to switch between different functionalities.
 *
 * <p>The container includes:
 * <ul>
 *     <li>A PathFindingGUI for exploring synonym paths between words</li>
 *     <li>A GenerateRandomWordGUI for random word exploration</li>
 *     <li>A toggle button to switch between interfaces</li>
 * </ul>
 *
 * <p>The class uses CardLayout to manage the switching between different interfaces,
 * ensuring only one interface is visible at a time while maintaining the state of both.
 */
public class SynonymGuiContainer extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private PathFindingGUI pathFindingGUI;
    private GenerateRandomWordGUI randomWordGUI;

    public SynonymGuiContainer() {
        setupContainer();
    }

    /**
     * Sets up the main container and initializes all GUI components.
     * This method:
     * <ul>
     *     <li>Sets up the frame properties</li>
     *     <li>Creates and configures the toggle button</li>
     *     <li>Initializes the card layout and its components</li>
     *     <li>Sets up the action listeners for interface switching</li>
     *     <li>Configures the initial layout and positioning</li>
     * </ul>
     */
    private void setupContainer() {
        setTitle("Synonym Network Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create toggle button
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JToggleButton toggleButton = new JToggleButton("Path Finding");
        toggleButton.setPreferredSize(new Dimension(150, 30));
        togglePanel.add(toggleButton);
        add(togglePanel, BorderLayout.NORTH);

        // Create card panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Initialize GUIs without making them visible
        pathFindingGUI = new PathFindingGUI() {
            @Override
            public void setVisible(boolean visible) {
                // Override to prevent independent window
            }
        };

        randomWordGUI = new GenerateRandomWordGUI() {
            @Override
            public void setVisible(boolean visible) {
                // Override to prevent independent window
            }
        };

        // Add GUIs to card panel
        cardPanel.add(pathFindingGUI.getContentPane(), "PathFinding");
        cardPanel.add(randomWordGUI.getContentPane(), "RandomPath");

        // Add card panel to frame
        add(cardPanel, BorderLayout.CENTER);

        // Setup toggle button action
        toggleButton.addActionListener(e -> {
            boolean isSelected = toggleButton.isSelected();
            toggleButton.setText(isSelected ? "Random Path" : "Path Finding");
            cardLayout.show(cardPanel, isSelected ? "RandomPath" : "PathFinding");
            pack();
        });

        // Set initial size and position
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SynonymGuiContainer container = new SynonymGuiContainer();
            container.setVisible(true);
        });
    }
}