import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

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
            calculateNodePositions();
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

        // Initialize random positions in a smaller central area
        initializeInCenter();

        // Force-directed layout parameters
        double k = Math.sqrt((graphPanel.getWidth() * graphPanel.getHeight()) /
                (2.0 * getAllNodes().size())); // Increased spacing
        double temperature = graphPanel.getWidth() / 4.0; // Increased initial temperature
        int iterations = 150; // More iterations for better convergence

        // Run force-directed layout
        for (int i = 0; i < iterations && temperature > 0.1; i++) {
            Map<String, Point.Double> forces = new HashMap<>();
            calculateForces(forces, k);
            applyForces(forces, temperature);
            centerGraph(); // Center the graph after each iteration
            temperature *= 0.98; // Slower cooling
        }

        normalizePositions();
    }

    private void initializeInCenter() {
        Random rand = new Random();

        // Calculate center of the panel
        double centerX = graphPanel.getWidth() / 2.0;
        double centerY = graphPanel.getHeight() / 2.0;

        // Initialize positions in a smaller central area (30% of panel size)
        double radiusX = graphPanel.getWidth() * 0.15;
        double radiusY = graphPanel.getHeight() * 0.15;

        for (String word : getAllNodes()) {
            // Position nodes in a circular area around the center
            double angle = rand.nextDouble() * 2 * Math.PI;
            double r = rand.nextDouble(); // Random radius between 0 and 1
            r = Math.sqrt(r); // Square root for more uniform distribution

            double x = centerX + r * radiusX * Math.cos(angle);
            double y = centerY + r * radiusY * Math.sin(angle);

            nodePositions.put(word, new Point((int)x, (int)y));
        }
    }

    private void calculateForces(Map<String, Point.Double> forces, double k) {
        Set<String> allNodes = getAllNodes();

        // Initialize force vectors
        for (String node : allNodes) {
            forces.put(node, new Point.Double(0, 0));
        }

        // Calculate repulsive forces between all nodes
        for (String v1 : allNodes) {
            Point p1 = nodePositions.get(v1);
            for (String v2 : allNodes) {
                if (v1.equals(v2)) continue;

                Point p2 = nodePositions.get(v2);
                double dx = p1.x - p2.x;
                double dy = p1.y - p2.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < 1) distance = 1;

                // Stronger repulsive force
                double force = (k * k) / (distance * 0.75); // Increased repulsion
                Point.Double f1 = forces.get(v1);
                f1.x += (dx / distance) * force;
                f1.y += (dy / distance) * force;
            }
        }

        // Calculate attractive forces between connected nodes
        for (String v1 : currentPath) {
            Set<String> synonyms = pathSynonyms.get(v1);
            if (synonyms != null) {
                for (String v2 : synonyms) {
                    Point p1 = nodePositions.get(v1);
                    Point p2 = nodePositions.get(v2);
                    double dx = p1.x - p2.x;
                    double dy = p1.y - p2.y;
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance < 1) distance = 1;

                    // Weaker attractive force
                    double force = (distance * distance) / (k * 2.0); // Reduced attraction
                    Point.Double f1 = forces.get(v1);
                    Point.Double f2 = forces.get(v2);
                    f1.x -= (dx / distance) * force;
                    f1.y -= (dy / distance) * force;
                    f2.x += (dx / distance) * force;
                    f2.y += (dy / distance) * force;
                }
            }
        }
    }

    private void centerGraph() {
        // Calculate current center of mass
        double totalX = 0, totalY = 0;
        Set<String> allNodes = getAllNodes();
        for (String node : allNodes) {
            Point pos = nodePositions.get(node);
            totalX += pos.x;
            totalY += pos.y;
        }

        double centerX = totalX / allNodes.size();
        double centerY = totalY / allNodes.size();

        // Calculate desired center
        double targetX = graphPanel.getWidth() / 2.0;
        double targetY = graphPanel.getHeight() / 2.0;

        // Move all nodes
        double dx = targetX - centerX;
        double dy = targetY - centerY;
        for (String node : allNodes) {
            Point pos = nodePositions.get(node);
            pos.x += dx;
            pos.y += dy;
        }
    }

    private void normalizePositions() {
        // Ensure minimum distance between nodes
        int minDistance = 100; // Minimum distance between nodes

        for (String v1 : getAllNodes()) {
            Point p1 = nodePositions.get(v1);
            for (String v2 : getAllNodes()) {
                if (v1.equals(v2)) continue;

                Point p2 = nodePositions.get(v2);
                double dx = p2.x - p1.x;
                double dy = p2.y - p1.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < minDistance) {
                    // Push nodes apart
                    double angle = Math.atan2(dy, dx);
                    double pushDist = (minDistance - distance) / 2;

                    p1.x -= Math.cos(angle) * pushDist;
                    p1.y -= Math.sin(angle) * pushDist;
                    p2.x += Math.cos(angle) * pushDist;
                    p2.y += Math.sin(angle) * pushDist;
                }
            }
        }

        // Keep within bounds with padding
        int padding = 50;
        for (Point p : nodePositions.values()) {
            p.x = Math.min(Math.max(p.x, padding), graphPanel.getWidth() - padding);
            p.y = Math.min(Math.max(p.y, padding), graphPanel.getHeight() - padding);
        }
    }

    private Set<String> getAllNodes() {
        Set<String> allNodes = new HashSet<>();
        allNodes.addAll(currentPath);
        for (Set<String> synonyms : pathSynonyms.values()) {
            allNodes.addAll(synonyms);
        }
        return allNodes;
    }

    private void applyForces(Map<String, Point.Double> forces, double temperature) {
        for (Map.Entry<String, Point.Double> entry : forces.entrySet()) {
            String node = entry.getKey();
            Point.Double force = entry.getValue();
            Point pos = nodePositions.get(node);

            // Calculate displacement
            double dx = Math.min(Math.max(force.x, -temperature), temperature);
            double dy = Math.min(Math.max(force.y, -temperature), temperature);

            // Update position
            pos.x += dx;
            pos.y += dy;

            // Keep within bounds
            pos.x = Math.min(Math.max(pos.x, 50), graphPanel.getWidth() - 50);
            pos.y = Math.min(Math.max(pos.y, 50), graphPanel.getHeight() - 50);
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