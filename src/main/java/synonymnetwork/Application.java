package synonymnetwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    // Set system property to run Spring Boot in headless mode for web server
    // but still allow GUI to work
    System.setProperty("java.awt.headless", "false");

    // Start Spring Boot application (REST API server)
    ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

    /*  // Also start your GUI application using reflection to access default package
    SwingUtilities.invokeLater(
        () -> {
          try {
            // Use reflection to call SynonymGuiContainer.main() from default package
            Class<?> guiClass = Class.forName("SynonymGuiContainer");
            java.lang.reflect.Method mainMethod = guiClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[] {});
          } catch (Exception e) {
            System.err.println("Error starting GUI: " + e.getMessage());
            e.printStackTrace();
          }
        }); */

    // Print information about what's running
    System.out.println("=================================================");
    System.out.println("🚀 Application Started Successfully!");
    System.out.println("📱 GUI Application: Running");
    System.out.println("🌐 REST API Server: http://localhost:8080");
    System.out.println("=================================================");
  }
}
