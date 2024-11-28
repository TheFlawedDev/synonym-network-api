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

public class GenerateRandomWordGUI extends JFrame {
	private SynonymGraph synonymGraph;
	private JTextField sourceWordField;
	private JTextField depthLevelField;
	private JButton exploreButton;
	private JButton resetButton;
	private JTextArea analysisArea;
	private Graph displayGraph;
	private Viewer viewer;
	private JPanel mainPanel;

	public GenerateRandomWordGUI() {
		synonymGraph = new SynonymGraph();
		System.setProperty("org.graphstream.ui", "swing");
		displayGraph = new SingleGraph("Random Synonym Path");
		setupGUI();
	}

	private void setupGUI() {
		setTitle("Random Synonym Path Generator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));

		// Input Panel
		JPanel inputPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);

		gbc.gridx = 0;
		gbc.gridy = 0;
		inputPanel.add(new JLabel("Start Word:"), gbc);

		gbc.gridx = 1;
		sourceWordField = new JTextField(15);
		inputPanel.add(sourceWordField, gbc);

		gbc.gridx = 2;
		inputPanel.add(new JLabel("Target Depth:"), gbc);

		gbc.gridx = 3;
		depthLevelField = new JTextField(15);
		inputPanel.add(depthLevelField, gbc);

		gbc.gridx = 4;
		exploreButton = new JButton("Generate Path");
		exploreButton.addActionListener(e -> generateRandomPath());
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

	private void generateRandomPath() {
		String startWord = sourceWordField.getText().trim().toLowerCase();
		String depthStr = depthLevelField.getText().trim();

		if (startWord.isEmpty() || depthStr.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter both start word and depth", "Input Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			int targetDepth = Integer.parseInt(depthStr);
			if (targetDepth < 1) {
				JOptionPane.showMessageDialog(this, "Depth must be at least 1", "Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			SwingUtilities.invokeLater(() -> {
				displayGraph.clear();
				displayGraph.setAttribute("ui.stylesheet",
						"node { size: 20px; fill-color: #777; text-offset: 0px, -15px; text-alignment: above; text-size: 20; text-color: #000000; } " +
								"node.start { fill-color: #4169E1; } " +  // Royal Blue for start
								"node.path { fill-color: #2E8B57; } " +   // Sea Green for intermediate
								"node.end { fill-color: #DC143C; } " +    // Crimson for end
								"node.synonym { fill-color: #808080; } " + // Gray for synonyms
								"edge { fill-color: #000000; } " +
								"edge.path { fill-color: #4169E1; size: 2px; } " +
								"edge.synonym { fill-color: #808080; }");

				List<String> randomPath = synonymGraph.generateWordAtDepth(startWord, targetDepth);

				if (randomPath == null || randomPath.isEmpty()) {
					analysisArea.setText("Could not generate a path of the requested depth from the start word.");
					return;
				}

				Map<String, Set<String>> pathSynonyms = synonymGraph.getPathSynonyms(randomPath);

				// Add path nodes
				for (String word : randomPath) {
					Node node = displayGraph.addNode(word);
					node.setAttribute("ui.label", word);
					node.setAttribute("ui.class", "path");
				}

				// Mark start and end nodes
				Node startNode = displayGraph.getNode(startWord);
				if (startNode != null) {
					startNode.setAttribute("ui.class", "start");
				}

				Node endNode = displayGraph.getNode(randomPath.get(randomPath.size() - 1));
				if (endNode != null) {
					endNode.setAttribute("ui.class", "end");
				}

				// Add path edges
				for (int i = 1; i < randomPath.size(); i++) {
					String edgeId = randomPath.get(i - 1) + "-" + randomPath.get(i);
					Edge edge = displayGraph.addEdge(edgeId, randomPath.get(i - 1), randomPath.get(i));
					edge.setAttribute("ui.class", "path");
				}

				// Add synonyms for each word in the path
				if (pathSynonyms != null) {
					for (Map.Entry<String, Set<String>> entry : pathSynonyms.entrySet()) {
						String word = entry.getKey();
						for (String synonym : entry.getValue()) {
							if (!randomPath.contains(synonym)) { // Only add if not in main path
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
				}

				// Update analysis text
				StringBuilder analysis = new StringBuilder();
				analysis.append("Random Synonym Path:\n");
				analysis.append(String.join(" → ", randomPath)).append("\n\n");
				analysis.append("Path Length: ").append(randomPath.size() - 1).append(" connections\n");
				analysis.append("Target Depth: ").append(targetDepth);
				analysisArea.setText(analysis.toString());
			});
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Please enter a valid number for depth", "Input Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void resetAll() {
		SwingUtilities.invokeLater(() -> {
			sourceWordField.setText("");
			depthLevelField.setText("");
			displayGraph.clear();
			analysisArea.setText("");
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new GenerateRandomWordGUI().setVisible(true));
	}
}