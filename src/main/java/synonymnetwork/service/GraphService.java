package synonymnetwork.service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

  public String getDefinition(String word) {
    return this.synonymGraph.findWordDefinition(word);
  }

  /**
   * Gets definitions for all words in a path.
   *
   * @param path List of words to get definitions for
   * @return Map of word to its definition
   */
  public Map<String, String> getDefinitionsForPath(List<String> path) {
    Map<String, String> definitions = new HashMap<>();
    if (path == null) {
      return definitions;
    }
    for (String word : path) {
      definitions.put(word, this.synonymGraph.findWordDefinition(word));
    }
    return definitions;
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

  public Map<String, Set<String>> getsPathToSynonyms(List<String> path) {
    return this.synonymGraph.getPathSynonyms(path);
  }

  /**
   * returns all the words in the path of the starting word and an arbitrary depth in the graph
   *
   * @param start First word in the graph
   * @param depth The depth of the path into the graph
   * @return list of strings representing all the words.
   */
  public List<String> generateWordAtDepth(String start, int depth) {
    if (start == null || start.isEmpty() || depth < 1) {
      return null;
    }
    return synonymGraph.generateWordAtDepth(start, depth);
  }

  /**
   * Gets basic statistics about the graph for a given path.
   *
   * @param path The path to analyze.
   * @return String containing graph statistics (node and edge counts).
   */
  public String getGraphStatistics(List<String> path) {
    if (path != null && !path.isEmpty()) {
      int nodeCount = path.size();
      int edgeCount = nodeCount > 0 ? nodeCount - 1 : 0;
      return "Nodes: " + nodeCount + "\nEdges: " + edgeCount;
    }

    return "No Path Found.";
  }
}
