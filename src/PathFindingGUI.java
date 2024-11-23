import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PathFindingGUI extends JFrame {
    private SynonymGraph synonymGraph;
    private JTextField sourceWordField;
    private JTextField targetWordField;
    private JButton exploreButton;
    private JButton resetButton;
    private JPanel graphPanel;
    private JTextArea analysisArea;
    private List<String> currentPath;
    private Map<String, Set<String>> pathSynonyms;
    private Map<String, Point> nodePositions;
    private int connectionLevel;

    public PathFindingGUI() {
        synonymGraph = new SynonymGraph();
        nodePositions = new HashMap<>();
        setupGUI();
    }

    private void setupGUI() {
        setTitle("Synonym Network Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Source word input
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Enter a source word:"), gbc);

        gbc.gridx = 1;
        sourceWordField = new JTextField(15);
        inputPanel.add(sourceWordField, gbc);

        // Target word input
        gbc.gridx = 2;
        inputPanel.add(new JLabel("Enter a target word:"), gbc);

        gbc.gridx = 3;
        targetWordField = new JTextField(15);
        inputPanel.add(targetWordField, gbc);

        // Buttons
        gbc.gridx = 4;
        exploreButton = new JButton("Explore");
        exploreButton.addActionListener(e -> exploreConnection());
        inputPanel.add(exploreButton, gbc);

        gbc.gridx = 5;
        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetAll());
        inputPanel.add(resetButton, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Graph Visualization Panel
        graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawEnhancedGraph(g);
            }
        };
        graphPanel.setPreferredSize(new Dimension(1000, 600));
        graphPanel.setBackground(Color.WHITE);
        add(new JScrollPane(graphPanel), BorderLayout.CENTER);

        // Analysis Panel
        analysisArea = new JTextArea(5, 40);
        analysisArea.setEditable(false);
        add(new JScrollPane(analysisArea), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void exploreConnection() {
        String sourceWord = sourceWordField.getText().trim().toLowerCase();
        String targetWord = targetWordField.getText().trim().toLowerCase();

        if (sourceWord.isEmpty() || targetWord.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both words", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentPath = synonymGraph.findPath(sourceWord, targetWord);
        pathSynonyms = synonymGraph.getPathSynonyms(sourceWord, targetWord);
        connectionLevel = synonymGraph.getConnectionsLevel(sourceWord, targetWord);

        if (currentPath == null) {
            analysisArea.setText("No path found between these words.");
            currentPath = null;
            pathSynonyms = null;
        } else {
            calculateNodePositions(); // Add this line
            StringBuilder analysis = new StringBuilder();
            analysis.append("Synonyms Path:\n");
            analysis.append(String.join(" → ", currentPath)).append("\n\n");
            analysis.append("Connection Level: ").append(connectionLevel);
            analysisArea.setText(analysis.toString());
        }

        graphPanel.repaint();
    }

    private void calculateNodePositions() {
        nodePositions.clear();
        if (currentPath == null || pathSynonyms == null) return;

        int centerY = graphPanel.getHeight() / 2;
        int horizontalSpacing = Math.min((graphPanel.getWidth() - 100) / (currentPath.size() - 1), 200);
        // Position main path nodes with maximum spacing of 200 pixels

        // Start with more left padding for better visibility
        int startX = Math.max(100, (graphPanel.getWidth() - (horizontalSpacing * (currentPath.size() - 1))) / 2);

        // Position main path nodes
        for (int i = 0; i < currentPath.size(); i++) {
            String word = currentPath.get(i);
            nodePositions.put(word, new Point(startX + (i * horizontalSpacing), centerY));
        }

        // Position synonym nodes around main path nodes with smaller radius
        for (String pathWord : currentPath) {
            Point centerPoint = nodePositions.get(pathWord);
            Set<String> synonyms = pathSynonyms.get(pathWord);

            if (synonyms == null || synonyms.isEmpty()) continue;

            int synonymCount = synonyms.size();
            // Reduce the radius for less spread
            double radiusY = 80; // Vertical radius for elliptical arrangement
            double radiusX = horizontalSpacing / 4; // Horizontal radius

            int synonymIndex = 0;
            for (String synonym : synonyms) {
                if (!currentPath.contains(synonym)) {
                    // Adjust angle calculation to spread synonyms in the upper and lower half
                    double angle = Math.PI + (Math.PI * synonymIndex) / (synonymCount - 1);
                    int x = (int) (centerPoint.x + radiusX * Math.cos(angle));
                    int y = (int) (centerPoint.y + radiusY * Math.sin(angle));
                    nodePositions.put(synonym, new Point(x, y));
                    synonymIndex++;
                }
            }
        }
    }

    private void drawEnhancedGraph(Graphics g) {
        if (currentPath == null || pathSynonyms == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw connections first
        g2d.setStroke(new BasicStroke(1.0f));
        for (String pathWord : currentPath) {
            Point wordPos = nodePositions.get(pathWord);
            Set<String> synonyms = pathSynonyms.get(pathWord);

            // Draw main path connections
            if (currentPath.indexOf(pathWord) < currentPath.size() - 1) {
                String nextWord = currentPath.get(currentPath.indexOf(pathWord) + 1);
                Point nextPos = nodePositions.get(nextWord);
                g2d.setColor(new Color(100, 100, 100));
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawLine(wordPos.x, wordPos.y, nextPos.x, nextPos.y);
            }

            // Draw synonym connections
            g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                    0, new float[]{3}, 0));
            g2d.setColor(new Color(180, 180, 180));
            for (String synonym : synonyms) {
                if (!currentPath.contains(synonym)) {
                    Point synPos = nodePositions.get(synonym);
                    g2d.drawLine(wordPos.x, wordPos.y, synPos.x, synPos.y);
                }
            }
        }

        // Draw nodes
        int nodeRadius = 30;
        for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
            String word = entry.getKey();
            Point pos = entry.getValue();

            // Set node color
            if (currentPath.contains(word)) {
                if (word.equals(currentPath.get(0))) {
                    g2d.setColor(new Color(65, 105, 225)); // Start word
                } else if (word.equals(currentPath.get(currentPath.size() - 1))) {
                    g2d.setColor(new Color(205, 92, 92)); // End word
                } else {
                    g2d.setColor(new Color(43, 10, 10)); // Path word
                }
            } else {
                g2d.setColor(new Color(150, 150, 150)); // Synonym word
            }

            // Draw node
            g2d.fillOval(pos.x - nodeRadius, pos.y - nodeRadius,
                    nodeRadius * 2, nodeRadius * 2);

            // Draw label
            g2d.setColor(Color.WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(word);
            int textHeight = fm.getHeight();
            g2d.drawString(word, pos.x - (textWidth / 2),
                    pos.y + (textHeight / 4));
        }
    }

    private void resetAll() {
        sourceWordField.setText("");
        targetWordField.setText("");
        currentPath = null;
        pathSynonyms = null;
        nodePositions.clear();
        analysisArea.setText("");
        graphPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PathFindingGUI().setVisible(true);
        });
    }
}