package synonymnetwork.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synonymnetwork.service.GraphService;

@RestController
@RequestMapping("/api/graph")
public class GraphController {

  @Autowired private GraphService graphService;

  /** Gets the definition for a single word. Example: GET /api/graph/definition?word=happy */
  @GetMapping("/definition")
  public ResponseEntity<String> getDefinition(@RequestParam String word) {
    String definition = graphService.getDefinition(word);
    return definition == null || definition.contains("not found")
        ? ResponseEntity.notFound().build()
        : ResponseEntity.ok(definition);
  }

  /** Checks if a word exists in the graph. Example: GET /api/graph/exists?word=happy */
  @GetMapping("/exists")
  public ResponseEntity<Boolean> containsWord(@RequestParam String word) {
    return ResponseEntity.ok(graphService.containsWord(word));
  }

  /**
   * Gets definitions for a list of words sent in the request body. Example: POST
   * /api/graph/definitions Body: ["love", "hate", "life"]
   */
  @PostMapping("/definitions")
  public ResponseEntity<Map<String, String>> getDefinitionsForPath(@RequestBody List<String> path) {
    Map<String, String> definitions = graphService.getDefinitionsForPath(path);
    return ResponseEntity.ok(definitions);
  }

  /**
   * Gets synonyms for a list of words sent in the request body. Example: POST /api/graph/synonyms
   * Body: ["love", "hate", "life"]
   */
  @PostMapping("/synonyms")
  public ResponseEntity<Map<String, Set<String>>> getPathSynonyms(@RequestBody List<String> path) {
    Map<String, Set<String>> synonyms = graphService.getsPathToSynonyms(path);
    return ResponseEntity.ok(synonyms);
  }

  /**
   * Gets statistics (node and edge count) for a given path. Example: POST /api/graph/statistics
   * Body: ["love", "emotion", "hate"]
   */
  @PostMapping("/statistics")
  public ResponseEntity<String> getGraphStatistics(@RequestBody List<String> path) {
    String stats = graphService.getGraphStatistics(path);
    return ResponseEntity.ok(stats);
  }
}
