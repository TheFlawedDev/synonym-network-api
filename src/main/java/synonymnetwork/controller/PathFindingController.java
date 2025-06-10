package synonymnetwork.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import synonymnetwork.service.PathFindingService;
import synonymnetwork.service.PathFindingService.PathInfo;

@RestController
@RequestMapping("/api/path")
public class PathFindingController {

  @Autowired private PathFindingService pathFindingService;

  /**
   * Finds the shortest path between two words. Example: GET
   * /api/path/shortest?word1=love&word2=hate
   */
  @GetMapping("/shortest")
  public ResponseEntity<List<String>> findShortestPath(
      @RequestParam String word1, @RequestParam String word2) {
    List<String> path = pathFindingService.findShortestPath(word1, word2);
    return path.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(path);
  }

  /**
   * Gets the connection level (number of edges) between two words. Example: GET
   * /api/path/level?word1=love&word2=hate
   */
  @GetMapping("/level")
  public ResponseEntity<Integer> getConnectionLevel(
      @RequestParam String word1, @RequestParam String word2) {
    int level = pathFindingService.getConnectionLevel(word1, word2);
    return level == -1 ? ResponseEntity.notFound().build() : ResponseEntity.ok(level);
  }

  /**
   * Gets synonyms for all words along the path between two words. Example: GET
   * /api/path/synonyms?word1=love&word2=hate
   */
  @GetMapping("/synonyms")
  public ResponseEntity<Map<String, Set<String>>> getPathSynonyms(
      @RequestParam String word1, @RequestParam String word2) {
    Map<String, Set<String>> pathSynonyms = pathFindingService.getPathSynonyms(word1, word2);
    return pathSynonyms == null
        ? ResponseEntity.notFound().build()
        : ResponseEntity.ok(pathSynonyms);
  }

  /**
   * Determines if a path exists between two words. Example: GET
   * /api/path/connected?word1=love&word2=hate
   */
  @GetMapping("/connected")
  public ResponseEntity<Boolean> areWordsConnected(
      @RequestParam String word1, @RequestParam String word2) {
    return ResponseEntity.ok(pathFindingService.areWordsConnected(word1, word2));
  }

  /**
   * Gets a complete information object for the path between two words. Example: GET
   * /api/path/info?word1=love&word2=hate
   */
  @GetMapping("/info")
  public ResponseEntity<PathInfo> getPathInfo(
      @RequestParam String word1, @RequestParam String word2) {
    PathInfo pathInfo = pathFindingService.getPathInfo(word1, word2);
    return pathInfo == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(pathInfo);
  }
}
