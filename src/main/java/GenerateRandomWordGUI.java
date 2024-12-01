import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenerateRandomWordGUI extends JFrame {
	private SynonymGraph synonymGraph;
	List<String> randomPath;
	private JTextField sourceWordField;
	private JTextField depthLevelField;
	private JButton exploreButton;
	private JButton resetButton;
	private JTextArea analysisArea;
	private Graph displayGraph;
	private Viewer viewer;
	private JPanel mainPanel;
	private JButton definitionsButton;

	public GenerateRandomWordGUI() {
		synonymGraph = new SynonymGraph();
		System.setProperty("org.graphstream.ui", "swing");
		displayGraph = new SingleGraph("Random Synonym Path");
		setupGUI();
	}

	private void setupGUI() {
		setTitle("Synonym Network Explorer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));
		getContentPane().setBackground(Color.WHITE);

		add(createTopPanel(), BorderLayout.NORTH);
		add(createGraphPanel(), BorderLayout.CENTER);
		add(createAnalysisPanel(), BorderLayout.SOUTH);

		pack();
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
		JLabel subtitleLabel = createStyledLabel("Generate random word based on depth level", "SansSerif", Font.PLAIN, 14);
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

		inputPanel.add(createWordInputPanel("Start Word", sourceWordField = new JTextField(15)));
		inputPanel.add(createWordInputPanel("Target Depth:", depthLevelField = new JTextField(15)));
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
		if(buttonLabel.equals("Definitions")){
			definitionsButton = new JButton(buttonLabel);
			styleExploreButton(definitionsButton);
			definitionsButton.addActionListener(e ->showDefinitions());
			buttonWrapper.add(definitionsButton);
			return buttonWrapper;
		}
		else {
			exploreButton = new JButton(buttonLabel);
			styleExploreButton(exploreButton);
			exploreButton.addActionListener(e -> generateRandomPath());

			buttonWrapper.add(exploreButton);
			return buttonWrapper;
		}
	}

	private JPanel createGraphPanel() {
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setPreferredSize(new Dimension(800, 400));
		mainPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 20, 20, 20),
				BorderFactory.createLineBorder(new Color(230, 230, 230))
		));

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
		textField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
		));
		textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
	}

	private void styleExploreButton(JButton button) {
		button.setPreferredSize(new Dimension(110, 40));
		button.setBackground(new Color(37, 99, 235));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(true);
		button.setFont(new Font("SansSerif", Font.BOLD, 14));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	private void styleResetButton(JButton button) {
		button.setPreferredSize(new Dimension(80, 30));
		button.setBackground(Color.WHITE);
		button.setForeground(Color.BLACK);
		button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
		button.setFocusPainted(true);
		button.setFont(new Font("SansSerif", Font.PLAIN, 14));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
						"node { size: 20px; fill-color: #777; text-offset: 0px, -15px; text-alignment: above; text-size: 20; text-color: #000000;} " +
								"node.start {fill-color: blue;} " +  // Royal Blue for start
								"node.path { fill-color: #2E8B57; } " +   // Sea Green for intermediate
								"node.end { fill-color: #DC143C; } " +    // Crimson for end
								"node.synonym { fill-color: #808080; } " + // Gray for synonyms
								"edge { fill-color: #000000;} " +
								"edge.path { fill-color: #4169E1; size: 2px; } " +
								"edge.synonym { fill-color: #808080; }");

				randomPath = synonymGraph.generateWordAtDepth(startWord, targetDepth);

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
	private void showDefinitions() {
		if (randomPath == null) {
			JOptionPane.showMessageDialog(this, "Please explore the synonym connection before viewing the definitions.",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		StringBuilder definitions = new StringBuilder();

		// Word Definitions Header
		definitions.append("<html><body>");
		definitions.append("<h2 style='text-align:center; font-weight:bold;'>Word Definitions:</h2>");

		// Path words and definitions
		for (String word : randomPath) {
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

	private void resetAll() {
		SwingUtilities.invokeLater(() -> {
			sourceWordField.setText("");
			depthLevelField.setText("");
			displayGraph.clear();
			analysisArea.setText("");
		});
		randomPath = null;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new GenerateRandomWordGUI().setVisible(true));
	}
}