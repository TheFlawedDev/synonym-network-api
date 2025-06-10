package synonymnetwork.service;

import java.util.Collections;
import java.util.HashMap; // Required for Map
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import synonymnetwork.domain.SynonymGraph; // Needed for direct calls from GraphService's

// getSynonymGraph()

/**
 * Service class that handles pathfinding operations between words. Extracts business logic from
 * PathFindingGUI.
 */
@Service
public class PathFindingService {

  @Autowired private GraphService graphService;

  /**
   * Finds the shortest path between two words in the graph.
   *
   * @param word1 The starting word.
   * @param word2 The target word.
   * @return List of words forming the shortest path from word1 to word2, or empty list if no path
   *     exists.
   */
  public List<String> findShortestPath(String word1, String word2) {
    // Retrieve the SynonymGraph instance from GraphService
    // just returning the instance from getSynonymGraph()).
    SynonymGraph sg = graphService.getSynonymGraph();
    if (sg == null) {
      return Collections.emptyList(); // Handle case where graph isn't initialized
    }
    List<String> path = sg.findPath(word1, word2);
    return path != null
        ? path
        : Collections.emptyList(); // Return empty list instead of null for no path
  }

  /**
   * Gets the connection level (path length) between two words.
   *
   * @param word1 The starting word.
   * @param word2 The target word.
   * @return The minimum number of synonym connections, or -1 if no path exists.
   */
  public int getConnectionLevel(String word1, String word2) {
    List<String> path = findShortestPath(word1, word2); // Use the already implemented method
    if (path.isEmpty()) { // Check if path is empty (meaning no path found)
      return -1; // Indicate no connection
    }
    return path.size() - 1; // [9]
  }

  /**
   * Gets synonyms for all words along the path from word1 to word2.
   *
   * @param word1 The starting word.
   * @param word2 The target word.
   * @return Map of each word in path to its limited set of synonyms, or null if no path exists.
   */
  public Map<String, Set<String>> getPathSynonyms(String word1, String word2) {
    List<String> path = findShortestPath(word1, word2); // Use the already implemented method
    if (path.isEmpty()) { // Check if path is empty (meaning no path found)
      return null;
    }
    SynonymGraph sg = graphService.getSynonymGraph(); // [15]
    if (sg == null) {
      return null;
    }
    return sg.getPathSynonyms(path); // [11-13]
  }

  /**
   * Determines if two words are connected in the graph.
   *
   * @param word1 The first word.
   * @param word2 The second word.
   * @return true if a path exists between the two words, false otherwise.
   */
  public boolean areWordsConnected(String word1, String word2) {
    // A path exists if findShortestPath returns a non-empty list.
    return !findShortestPath(word1, word2).isEmpty();
  }

  /**
   * Gets detailed information about the path between two words. This includes the path itself,
   * connection level, synonyms for words on the path, and definitions for all words in the path.
   *
   * @param word1 The starting word.
   * @param word2 The target word.
   * @return A PathInfo object containing details, or null if no path is found.
   */
  public PathInfo getPathInfo(String word1, String word2) {
    List<String> path = findShortestPath(word1, word2);
    if (path.isEmpty()) {
      return null; // No path found
    }

    int connectionLevel = path.size() - 1;
    Map<String, Set<String>> synonyms = getPathSynonyms(word1, word2);

    SynonymGraph sg = graphService.getSynonymGraph();
    Map<String, String> definitions = new HashMap<>();
    for (String word : path) {
      // Find word definition
      definitions.put(word, sg.findWordDefinition(word));
    }

    return new PathInfo(path, connectionLevel, synonyms, definitions);
  }

  /**
   * Simple DTO (Data Transfer Object) to encapsulate path information. This class is not part of
   * the original sources but is created to fulfill the return type requirement of getPathInfo and
   * to provide a structured response.
   */
  public static class PathInfo {
    private final List<String> path;
    private final int connectionLevel;
    private final Map<String, Set<String>> pathSynonyms;
    private final Map<String, String> wordDefinitions;

    public PathInfo(
        List<String> path,
        int connectionLevel,
        Map<String, Set<String>> pathSynonyms,
        Map<String, String> wordDefinitions) {
      this.path = path;
      this.connectionLevel = connectionLevel;
      this.pathSynonyms = pathSynonyms;
      this.wordDefinitions = wordDefinitions;
    }

    // Getters for all fields
    public List<String> getPath() {
      return path;
    }

    public int getConnectionLevel() {
      return connectionLevel;
    }

    public Map<String, Set<String>> getPathSynonyms() {
      return pathSynonyms;
    }

    public Map<String, String> getWordDefinitions() {
      return wordDefinitions;
    }
  }
}
