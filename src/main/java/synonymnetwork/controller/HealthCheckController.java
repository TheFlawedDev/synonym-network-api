package synonymnetwork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

  /**
   * A simple endpoint to verify that the API is running. It's lightweight and perfect for
   * "keep-alive" services. It will be accessible at YOUR_URL/health
   */
  @GetMapping("/health")
  public ResponseEntity<String> healthCheck() {
    // Returns a 200 OK status code with the text "Server is alive."
    return ResponseEntity.ok("Server is alive.");
  }
}
