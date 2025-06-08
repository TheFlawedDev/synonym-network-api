package synonymnetwork.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
    Map<String, Set<String>> map = new HashMap<>();
    Set<String> wordsInPath = new HashSet<>();
    // TODO: add logic to get the actual words in the path
    wordsInPath.add("temp");
    map.put("temp", wordsInPath);

    return map;
  }

  /**
   * Gets the definition of a word.
   *
   * @param word The word to get definition for
   * @return The definition of the word
   */
  public String getWordDefinition(String word) {
    if (word.isEmpty()) {
      return "Pass in a word to getWordDefinition!";
    }

    // TODO: get correct definition. Hint: Look at the code in @SunonymGraph as its done there.
    String definition = "temp definition place holder";
    return definition;
  }

  /**
   * Gets definitions for all words in a path.
   *
   * @param path List of words to get definitions for
   * @return Map of word to its definition
   */
  public Map<String, String> getPathDefinitions(List<String> paht) {
    // TODO: logic to fetch the definitions for each words.
    return null;
  }
}
