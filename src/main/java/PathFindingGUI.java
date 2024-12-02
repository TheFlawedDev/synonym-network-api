import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

/**
 * A graphical user interface for exploring synonym relationships between words.
 * This class provides a visual representation of synonym paths between two
 * words using GraphStream library for graph visualization and Swing for the GUI
 * components.
 *
 * The interface allows users to: - Input source and target words - Visualize
 * the synonym path between words - View related synonyms along the path - Reset
 * the visualization
 *
 * @author Jorge Velazquez, Nick Budd
 */
public class PathFindingGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private SynonymGraph synonymGraph;
    private JTextField sourceWordField;
    private JTextField targetWordField;
    private JButton exploreButton;
    private JButton resetButton;
    private JTextArea analysisArea;
    private Graph displayGraph;
    private Viewer viewer;
    private JPanel mainPanel;
    private List<String> path;
    private JButton definitionsButton;

    /**
     * Constructs a new PathFindingGUI window when running PathFindingGUI alone.
     * Initializes the synonym graph, sets up the GraphStream display properties,
     * and configures the GUI components.
     */
    public PathFindingGUI() {
        synonymGraph = new SynonymGraph();
        System.setProperty("org.graphstream.ui", "swing");
        displayGraph = new SingleGraph("Synonym Network");
        setupGUI();
    }

    /**
     * Overloaded Constructor. Constructs a new PathFindingGUI when given a
     * SynonymGraph from a container. Initializes the synonym graph, sets up the
     * GraphStream display properties, and configures the GUI components.
     *
     * @param graph
     */
    public PathFindingGUI(SynonymGraph graph) {
        synonymGraph = graph;
        System.setProperty("org.graphstream.ui", "swing");
        displayGraph = new SingleGraph("Synonym Network");
        setupGUI();
    }

    /*
     * Sets up the GUI components and layouts. Initializes and arranges: - Input
     * fields for source and target words - Control buttons - Graph visualization
     * panel - Analysis text area
     */
    private void setupGUI() {
        setTitle("Synonym Network Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        add(createTopPanel(), BorderLayout.NORTH);
        add(createGraphPanel(), BorderLayout.CENTER);
        add(createAnalysisPanel(), BorderLayout.SOUTH);
        try{
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setLocationRelativeTo(null);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(createTitlePanel(), BorderLayout.NORTH);
        topPanel.add(createInputPanel(), BorderLayout.CENTER);
        return topPanel;
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = createStyledLabel("Synonym Network Explorer", "Monospace", Font.BOLD, 24);
        JLabel subtitleLabel = createStyledLabel("Find connectivity between two words", "SansSerif", Font.PLAIN, 14);
        subtitleLabel.setForeground(Color.GRAY);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        return titlePanel;
    }

    private JLabel createStyledLabel(String text, String fontName, int fontStyle, int fontSize) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font(fontName, fontStyle, fontSize));
        return label;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        inputPanel.add(createWordInputPanel("Enter a source word", sourceWordField = new JTextField(15)));
        inputPanel.add(createWordInputPanel("Enter a target word", targetWordField = new JTextField(15)));
        inputPanel.add(createExploreButtonPanel("Explore"));
        inputPanel.add(createExploreButtonPanel("Definitions"));

        return inputPanel;
    }

    private JPanel createWordInputPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));

        styleTextField(textField);

        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createExploreButtonPanel(String buttonLabel) {
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonWrapper.setBackground(Color.WHITE);
        if (buttonLabel.equals("Definitions")) {
            definitionsButton = new JButton(buttonLabel);
            styleExploreButton(definitionsButton);
            definitionsButton.addActionListener(e -> showDefinitions());
            buttonWrapper.add(definitionsButton);
            return buttonWrapper;
        } else {
            exploreButton = new JButton(buttonLabel);
            styleExploreButton(exploreButton);
            exploreButton.addActionListener(e -> exploreConnection());
            buttonWrapper.add(exploreButton);
            return buttonWrapper;
        }
    }

    private JPanel createGraphPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setPreferredSize(new Dimension(800, 400));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20),
                BorderFactory.createLineBorder(new Color(230, 230, 230))));

        viewer = displayGraph.display();
        SwingUtilities.invokeLater(() -> {
            mainPanel.add((Component) viewer.addDefaultView(false), BorderLayout.CENTER);
        });

        return mainPanel;
    }

    private JPanel createAnalysisPanel() {
        JPanel analysisPanel = new JPanel(new BorderLayout());
        analysisPanel.setBackground(Color.WHITE);
        analysisPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        analysisArea = new JTextArea(5, 40);
        analysisArea.setEditable(false);
        analysisArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        analysisArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        resetButton = new JButton("Reset");
        styleResetButton(resetButton);
        resetButton.addActionListener(e -> resetAll());

        JPanel resetPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        resetPanel.setBackground(Color.WHITE);
        resetPanel.add(resetButton);

        analysisPanel.add(new JScrollPane(analysisArea), BorderLayout.CENTER);
        analysisPanel.add(resetPanel, BorderLayout.SOUTH);

        return analysisPanel;
    }

    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(200, 35));
        textField.setBorder(
                BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    private void styleExploreButton(JButton button) {
        button.setPreferredSize(new Dimension(110, 40));
        button.setBackground(new Color(37, 99, 235));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(true);
        button.setBorderPainted(true);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleResetButton(JButton button) {
        button.setPreferredSize(new Dimension(80, 30));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /*
     * Explores and visualizes the synonym connection between the source and target
     * words. This method: - Validates input words - Finds the shortest path between
     * words - Creates a visual graph representation - Displays related synonyms -
     * Updates the analysis text area with path information
     *
     * The visualization includes: - Source node (blue) - Target node (red) - Path
     * nodes (black) - Synonym nodes (gray) - Path edges (blue) - Synonym edges
     * (gray)
     */
    private void exploreConnection() {
        String sourceWord = sourceWordField.getText().trim().toLowerCase();
        String targetWord = targetWordField.getText().trim().toLowerCase();

        if (sourceWord.isEmpty() || targetWord.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both words", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            displayGraph.clear();
            displayGraph.setAttribute("ui.stylesheet",
                    "node { size: 20px; fill-color: #777; text-offset: 0px, -15px; text-alignment: above; text-size: 20; text-color: #000000;} "
                            + "node.start {fill-color: blue;} " + // Royal Blue for start
                            "node.path { fill-color: #2E8B57; } " + // Sea Green for intermediate
                            "node.end { fill-color: #DC143C; } " + // Crimson for end
                            "node.synonym { fill-color: #808080; } " + // Gray for synonyms
                            "edge { fill-color: #000000;} " + "edge.path { fill-color: #4169E1; size: 2px; } "
                            + "edge.synonym { fill-color: #808080; }");

            path = synonymGraph.findPath(sourceWord, targetWord);
            Map<String, Set<String>> pathSynonyms = synonymGraph.getPathSynonyms(path);

            if (path == null) {
                analysisArea.setText("No path found between these words.");
                return;
            }

            // Add path nodes and edges
            for (String word : path) {
                Node node = displayGraph.addNode(word);
                node.setAttribute("ui.label", word);
                node.setAttribute("ui.class", "path");
            }

            // Mark the source and target nodes explicitly
            Node sourceNode = displayGraph.getNode(sourceWord);
            if (sourceNode != null) {
                sourceNode.setAttribute("ui.class", "start");
            }

            Node targetNode = displayGraph.getNode(targetWord);
            if (targetNode != null) {
                targetNode.setAttribute("ui.class", "end");
            }

            // Add path edges
            for (int i = 1; i < path.size(); i++) {
                String edgeId = path.get(i - 1) + "-" + path.get(i);
                Edge edge = displayGraph.addEdge(edgeId, path.get(i - 1), path.get(i));
                edge.setAttribute("ui.class", "path");
            }

            // Add synonyms
            if (pathSynonyms != null) {
                for (Map.Entry<String, Set<String>> entry : pathSynonyms.entrySet()) {
                    String word = entry.getKey();
                    for (String synonym : entry.getValue()) {
                        try {
                            Node node = displayGraph.addNode(synonym);
                            node.setAttribute("ui.label", synonym);
                            node.setAttribute("ui.class", "synonym");

                            Edge edge = displayGraph.addEdge(word + "-" + synonym, word, synonym);
                            edge.setAttribute("ui.class", "synonym");
                        } catch (IdAlreadyInUseException e) {
                            // Node already exists
                        }
                    }
                }
            }

            StringBuilder analysis = new StringBuilder();
            analysis.append("Synonyms Path:\n");
            analysis.append(String.join(" → ", path)).append("\n\n");
            analysis.append("Connection Level: ").append(path.size() - 1);
            analysisArea.setText(analysis.toString());
        });
    }

    /*
     * Displays all of the definitions from the words from the path created by the
     * exploreConnection, given that a path was created.
     */
    private void showDefinitions() {
        if (path == null) {
            JOptionPane.showMessageDialog(this, "Please explore the synonym connection before viewing the definitions.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder definitions = new StringBuilder();

        // Word Definitions Header
        definitions.append("<html><body>");
        definitions.append("<h2 style='text-align:center; font-weight:bold;'>Word Definitions:</h2>");

        // Path words and definitions
        for (String word : path) {
            String definition = synonymGraph.findWordDefinition(word);
            definitions.append("<b>").append(word).append(":</b> ").append(definition).append("<br><hr>");
        }
        definitions.append("</body></html>");

        // Use JTextPane for HTML formatting
        JTextPane definitionsPane = new JTextPane();
        definitionsPane.setContentType("text/html");
        definitionsPane.setText(definitions.toString());
        definitionsPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(definitionsPane);

        // Create a separate window for the definitions
        JFrame definitionsFrame = new JFrame("Word Definitions");
        definitionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        definitionsFrame.setSize(600, 400);
        definitionsFrame.setLocationRelativeTo(this);
        definitionsFrame.setVisible(true);
        definitionsFrame.toFront();
        definitionsFrame.add(scrollPane, BorderLayout.CENTER);
    }

    /*
     * Resets the GUI to its initial state. Clears: - Input fields - Graph
     * visualization - Analysis text area - Path
     */
    private void resetAll() {
        SwingUtilities.invokeLater(() -> {
            sourceWordField.setText("");
            targetWordField.setText("");
            displayGraph.clear();
            analysisArea.setText("");
        });
        path = null;
    }

    /**
     * Displays the PathFindingGUI application using the Swing framework.
     *
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PathFindingGUI().setVisible(true));
    }
}
