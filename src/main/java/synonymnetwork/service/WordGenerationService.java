package synonymnetwork.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * Service class that handles word generation operations.
 * Extracts logic from GenerateRandomWordGUI*/
@Service
public class WordGenerationService {
  @Autowired private GraphService graphService = new GraphService();

  /**
   * Generates a random path of words from a starting word to a specified depth.
   *
   * @param startWord The word to start from
   * @param targetDepth The number of connections to make
   * @return List of words forming the path, or null if no path can be generated
   */
  public List<String> generateRandomWordPath(String input, int graphDepth) {
    if (input.isEmpty() || (graphDepth == 0)) {
      return Collections.emptyList();
    }
    return graphService.generateWordAtDepth(input, graphDepth);
  }

  /**
   * Gets synonyms for all words in a given path.
   *
   * @param path List of words to get synonyms for
   * @return Map of word to its synonyms
   */
  public Map<String, Set<String>> getPathSynonyms(List<String> path) {
    if (path.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, Set<String>> map = graphService.getsPathToSynonyms(path);
    return map;
  }

  /**
   * Gets definitions for all words in a path.
   *
   * @param path List of words to get definitions for
   * @return Map of word to its definition
   */
  public StringBuilder getPathDefinitions(List<String> path) {
    StringBuilder definitions = new StringBuilder();

    // Word Definitions Header
    definitions.append("<html><body>");
    definitions.append("<h2 style='text-align:center; font-weight:bold;'>Word Definitions:</h2>");

    for (String word : path) {
      String definition = graphService.getDefinition(word);

      definitions.append("<b>").append(word).append(":</b> ").append(definition).append("<br><hr>");
    }
    definitions.append("</body></html>");
    return definitions;
  }
}
