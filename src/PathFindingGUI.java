import javax.swing.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PathFindingGUI {
    private SynonymGraph synonymGraph;
    private List<String> synonymPath;
    private String sourceWord;
    private String targetWord;

    private JFrame frame;
    private JPanel panel;
    private JTextField sourceField;
    private JTextField targetField;
    private JTextArea resultArea;
    private JButton exploreButton;
    private JButton resetButton;

    public void initialize() {
        frame = new JFrame("Path Finding GUI");
        panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        sourceField = new JTextField("Enter source word");
        targetField = new JTextField("Enter target word");
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);

        exploreButton = new JButton("Explore Path");
        resetButton = new JButton("Reset");

        exploreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleExploreButton();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });

        panel.add(sourceField);
        panel.add(targetField);
        panel.add(exploreButton);
        panel.add(resetButton);
        panel.add(new JScrollPane(resultArea));

        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void handleExploreButton() {
        sourceWord = sourceField.getText();
        targetWord = targetField.getText();
        synonymPath = synonymGraph.findPath(sourceWord, targetWord);
        displayGraph();
    }

    public void displayGraph() {
        if (synonymPath != null) {
            resultArea.setText(String.join(" -> ", synonymPath));
        } else {
            resultArea.setText("No path found.");
        }
    }

    public void reset() {
        sourceField.setText("");
        targetField.setText("");
        resultArea.setText("");
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PathFindingGUI gui = new PathFindingGUI();
//                    gui.synonymGraph = new SynonymGraph("path/to/your/file.txt"); // Adjust the path as needed
                gui.initialize();
            }
        });
    }
}
