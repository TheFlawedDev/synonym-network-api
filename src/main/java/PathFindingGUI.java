import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A graphical user interface for exploring synonym relationships between words.
 * This class provides a visual representation of synonym paths between two words
 * using GraphStream library for graph visualization and Swing for the GUI components.
 *
 * The interface allows users to:
 * - Input source and target words
 * - Visualize the synonym path between words
 * - View related synonyms along the path
 * - Reset the visualization
 */
public class PathFindingGUI extends JFrame {
    private SynonymGraph synonymGraph;
    private JTextField sourceWordField;
    private JTextField targetWordField;
    private JButton exploreButton;
    private JButton resetButton;
    private JTextArea analysisArea;
    private Graph displayGraph;
    private Viewer viewer;
    private JPanel mainPanel;

    /**
     * Constructs a new PathFindingGUI window.
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
     * Sets up the GUI components and layouts.
     * Initializes and arranges:
     * - Input fields for source and target words
     * - Control buttons
     * - Graph visualization panel
     * - Analysis text area
     */
    private void setupGUI() {
        setTitle("Synonym Network Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Enter a source word:"), gbc);

        gbc.gridx = 1;
        sourceWordField = new JTextField(15);
        inputPanel.add(sourceWordField, gbc);

        gbc.gridx = 2;
        inputPanel.add(new JLabel("Enter a target word:"), gbc);

        gbc.gridx = 3;
        targetWordField = new JTextField(15);
        inputPanel.add(targetWordField, gbc);

        gbc.gridx = 4;
        exploreButton = new JButton("Explore");
        exploreButton.addActionListener(e -> exploreConnection());
        inputPanel.add(exploreButton, gbc);

        gbc.gridx = 5;
        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetAll());
        inputPanel.add(resetButton, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Graph Panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(1000, 600));
        viewer = displayGraph.display();
        displayGraph.setAttribute("layout.stabilization-limit", 0);
        displayGraph.setAttribute("layout.quality", 4);
        viewer.enableAutoLayout();
        SwingUtilities.invokeLater(() -> {
            mainPanel.add((Component) viewer.addDefaultView(false), BorderLayout.CENTER);
        });
        add(mainPanel, BorderLayout.CENTER);

        // Analysis Panel
        analysisArea = new JTextArea(5, 40);
        analysisArea.setEditable(false);
        add(new JScrollPane(analysisArea), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Explores and visualizes the synonym connection between the source and target words.
     * This method:
     * - Validates input words
     * - Finds the shortest path between words
     * - Creates a visual graph representation
     * - Displays related synonyms
     * - Updates the analysis text area with path information
     *
     * The visualization includes:
     * - Source node (blue)
     * - Target node (red)
     * - Path nodes (black)
     * - Synonym nodes (gray)
     * - Path edges (blue)
     * - Synonym edges (gray)
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
                    "node { size: 20px; fill-color: #777; text-offset: 0px, -15px; text-alignment: above; text-size: 20; text-color: #000000; } " +
                            "node.source { fill-color: #4169E1; } " +
                            "node.path { fill-color: #000000; } " +
                            "node.target { fill-color: #DC143C; } " +
                            "node.synonym { fill-color: #80808; } " +
                            "edge { fill-color: #000000; } " +
                            "edge.path { fill-color: #4169E1; size: 2px; } " +
                            "edge.synonym { fill-color: #808080; }");

            List<String> path = synonymGraph.findPath(sourceWord, targetWord);
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
                sourceNode.setAttribute("ui.class", "source");
            }

            Node targetNode = displayGraph.getNode(targetWord);
            if (targetNode != null) {
                targetNode.setAttribute("ui.class", "target");
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

    /**
     * Resets the GUI to its initial state.
     * Clears:
     * - Input fields
     * - Graph visualization
     * - Analysis text area
     */
    private void resetAll() {
        SwingUtilities.invokeLater(() -> {
            sourceWordField.setText("");
            targetWordField.setText("");
            displayGraph.clear();
            analysisArea.setText("");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PathFindingGUI().setVisible(true));
    }
}
