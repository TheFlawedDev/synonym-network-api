package synonymnetwork.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import synonymnetwork.service.PathFindingService;

@RestController
@RequestMapping("/api/path")
public class PathFindingController {

  @Autowired private PathFindingService pathFindingService;

  /**
   * Finds the shortest path between two words. Example URL:
   * http://localhost:8080/api/path/shortest?word1=wordone&word2=wordtwo
   */
  @GetMapping("/shortest")
  public ResponseEntity<List<String>> findShortestPath(
      @RequestParam String word1, @RequestParam String word2) {
    List<String> path = pathFindingService.findShortestPath(word1, word2);
    if (path.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(path);
  }

  /**
   * Gets detailed information about the path between two words. Example URL:
   * http://localhost:8080/api/path/info?word1=wordone&word2=wordtwo
   */
  @GetMapping("/info")
  public ResponseEntity<PathFindingService.PathInfo> getPathInfo(
      @RequestParam String word1, @RequestParam String word2) {
    PathFindingService.PathInfo pathInfo = pathFindingService.getPathInfo(word1, word2);
    if (pathInfo == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(pathInfo);
  }
}
