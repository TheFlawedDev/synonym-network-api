package synonymnetwork.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * Service class that handles word generation operations.
 * Extracts logic from GenerateRandomWordGUI*/
@Service
public class WordGenerationService {
  @Autowired private GraphService graphService;

  /**
   * Generates a random path of words from a starting word to a specified depth.
   *
   * @param startWord The word to start from
   * @param targetDepth The number of connections to make
   * @return List of words forming the path, or null if no path can be generated
   */
  public List<String> generateRandomWordPath(String startWord, int targetDepth) {
    if (startWord.isEmpty() || targetDepth < 1) {
      return Collections.emptyList();
    }
    return graphService.generateWordAtDepth(startWord, targetDepth);
  }

  /**
   * Gets definitions for all words in a path and formats them as an HTML string.
   *
   * @param path List of words to get definitions for
   * @return A StringBuilder containing the HTML formatted definitions.
   */
  public StringBuilder getPathDefinitions(List<String> path) {
    // **REFACTORED**: Use the new centralized method from GraphService
    Map<String, String> definitionsMap = graphService.getDefinitionsForPath(path);

    StringBuilder definitions = new StringBuilder();

    // Word Definitions Header
    definitions.append("<html><body>");
    definitions.append("<h2 style='text-align:center; font-weight:bold;'>Word Definitions:</h2>");

    for (Map.Entry<String, String> entry : definitionsMap.entrySet()) {
      String word = entry.getKey();
      String definition = entry.getValue();
      definitions.append("<b>").append(word).append(":</b> ").append(definition).append("<br><hr>");
    }

    definitions.append("</body></html>");
    return definitions;
  }
}
