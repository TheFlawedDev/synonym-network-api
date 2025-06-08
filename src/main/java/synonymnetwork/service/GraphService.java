package synonymnetwork.service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.stereotype.Service;
import synonymnetwork.domain.SynonymGraph;

/**
 * Service class that manages the SynonymGraph instance. Provides centralized access to the graph
 * functionality.
 */
@Service
public class GraphService {

  private SynonymGraph synonymGraph;

  /**
   * Initialize the synonym graph after the service is constructed. This method is called
   * automatically by Spring after dependency injection.
   */
  @PostConstruct
  public void initialize() {
    this.synonymGraph = new SynonymGraph();
  }

  /**
   * Gets the SynonymGraph instance.
   *
   * @return The initialized SynonymGraph
   */
  public SynonymGraph getSynonymGraph() {
    return this.synonymGraph;
  }

  /**
   * Checks if a word exists in the graph.
   *
   * @param word The word to check
   * @return true if word exists in graph, false otherwise
   */
  public boolean containsWord(String word) {
    return this.synonymGraph.truthOrFalse(word);
  }

  /**
   * returns all the words in the path of the starting word and an arbitrary depth in the graph
   *
   * @param start First word in the graph
   * @param depth The depth of the path into the graph
   * @return list of strings representing all the words.
   */
  public List<String> generateWordAtDepth(String start, int depth) {
    List<String> path;

    if (start != null && !start.isEmpty()) {
      if (depth < 1) {
        return null;
      }
    }

    path = synonymGraph.generateWordAtDepth(start, depth);

    return path;
  }

  /**
   * Gets basic statistics about the graph. If end is non-empty, finds stats for the path from start
   * to end. If end is empty and depth >= 1, gets the path from start to the node at the given
   * depth.
   *
   * @param start The starting word
   * @param end The ending word (can be empty)
   * @param targetDepth The depth to explore if end is empty
   * @return String containing graph statistics
   */
  public String getGraphStatistics(String start, String end, int targetDepth) {
    List<String> path;

    if (end == null || end.isEmpty()) {
      // Use the depth-based path
      if (targetDepth < 1) {
        return "Invalid depth. Must be >= 1.";
      }

      path = synonymGraph.generateWordAtDepth(start, targetDepth);
    } else {
      // Use the start-to-end word path
      path = synonymGraph.findPath(start, end);
    }

    if (path != null && !path.isEmpty()) {
      int nodeCount = path.size();
      int edgeCount = nodeCount - 1;
      return "Nodes: " + nodeCount + "\nEdges: " + edgeCount;
    }

    return "No Path Found.";
  }
}
