package synonymnetwork.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import synonymnetwork.service.WordGenerationService;

@RestController
@RequestMapping("/api/generate")
public class WordGenerationController {

  @Autowired private WordGenerationService wordGenerationService;

  /**
   * Generates a random path of words from a starting word to a specified depth. Example: GET
   * /api/generate/random-path?startWord=random&depth=5
   */
  @GetMapping("/random-path")
  public ResponseEntity<List<String>> generateRandomWordPath(
      @RequestParam String startWord, @RequestParam int depth) {
    List<String> path = wordGenerationService.generateRandomWordPath(startWord, depth);
    return path.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(path);
  }
}
