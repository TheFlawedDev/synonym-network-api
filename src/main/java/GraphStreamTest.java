import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.spriteManager.*;
import org.graphstream.ui.view.Viewer;
import java.util.*;

public class GraphStreamTest {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("org.graphstream.ui", "swing");
        Graph displayGraph = new SingleGraph("Synonym Network");
        Layout layout = new SpringBox();
        displayGraph.setAttribute("layout", layout);

        displayGraph.setAttribute("ui.stylesheet",
                "node { size: 20px; fill-color: #777; text-size: 14; } " +
                        "node.path { fill-color: #ff0000; size: 25px; } " +
                        "node.synonym { fill-color: #0000ff; } " +
                        "edge { fill-color: #222; } " +
                        "edge.path { fill-color: #ff0000; size: 2px; } " +
                        "edge.synonym { fill-color: #0000ff; }"
        );

        SynonymGraph synonymGraph = new SynonymGraph();
        String startWord = "happy";
        String endWord = "rage";

        List<String> path = synonymGraph.findPath(startWord, endWord);
        Viewer viewer = displayGraph.display();

        if (path != null) {
            // Animate path nodes
            for (String word : path) {
                Thread.sleep(500);
                Node node = displayGraph.addNode(word);
                node.setAttribute("ui.label", word);
                node.setAttribute("ui.class", "path");
            }

            // Animate path edges
            for (int i = 1; i < path.size(); i++) {
                Thread.sleep(500);
                String edgeId = path.get(i-1) + "-" + path.get(i);
                Edge edge = displayGraph.addEdge(edgeId, path.get(i-1), path.get(i));
                edge.setAttribute("ui.class", "path");
            }

            // Animate synonyms
            Map<String, Set<String>> pathSynonyms = synonymGraph.getPathSynonyms(path);
            if (pathSynonyms != null) {
                for (Map.Entry<String, Set<String>> entry : pathSynonyms.entrySet()) {
                    String word = entry.getKey();
                    for (String synonym : entry.getValue()) {
                        try {
                            Thread.sleep(200);
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
    }
}