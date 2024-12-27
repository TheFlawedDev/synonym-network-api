import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;

/**
 * Constructs a `SynonymGuiContainer` window that serves as a container for
 * multiple synonym-related graphical user interfaces.
 *
 * This class uses a `CardLayout` to switch between different GUI panels, such
 * as the `PathFindingGUI` for exploring synonym connections and the
 * `GenerateRandomWordGUI` for generating random word interactions.
 *
 * The constructor initializes and sets up the container, ensuring the layout
 * and panels are properly configured for user interaction.
 *
 * @author Jorge Velazquez, Nick Budd
 */
public class SynonymGuiContainer extends JFrame {
    private static final long serialVersionUID = 1L;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private PathFindingGUI pathFindingGUI;
    private GenerateRandomWordGUI randomWordGUI;

    /**
     * Constructs a new SynonymGuiContainer.
     */
    public SynonymGuiContainer() {
        setupContainer();
    }

    /*
     * Configures the main container layout and components. Initializes the
     * `PathFindingGUI` and `GenerateRandomWordGUI` as switchable views managed by a
     * `CardLayout`. Includes a toggle button to switch between the two GUIs and
     * ensures they are displayed within the main application window.
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
        SynonymGraph synonymGraph = new SynonymGraph();
        pathFindingGUI = new PathFindingGUI(synonymGraph) {
            private static final long serialVersionUID = 1L;

            @Override
            public void setVisible(boolean visible) {
                // Override to prevent independent window
            }
        };

        randomWordGUI = new GenerateRandomWordGUI(synonymGraph) {
            private static final long serialVersionUID = 1L;

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
        });

        // Set initial size and position
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Initializes the `SynonymGuiContainer`, ensuring all GUI components are
     * created and managed on the Event Dispatch Thread (EDT).
     *
     * @param args command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SynonymGuiContainer container = new SynonymGuiContainer();
            container.setVisible(true);
        });
    }
}