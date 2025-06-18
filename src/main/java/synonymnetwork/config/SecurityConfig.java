package synonymnetwork.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${api.security.key}")
  private String principalRequestValue;

  // ==================================================================
  // ADD THIS NEW BEAN TO IGNORE THE /health ENDPOINT
  // ==================================================================
  /**
   * This bean customizes web security to completely bypass Spring Security for certain paths. It's
   * the best way to handle non-sensitive, public endpoints like health checks, as it bypasses the
   * entire filter chain.
   */
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers("/health");
  }

  // ==================================================================

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // Create an instance of our custom API Key filter
    ApiKeyAuthFilter filter = new ApiKeyAuthFilter("x-api-key");
    filter.setAuthenticationManager(
        authentication -> {
          String principal = (String) authentication.getPrincipal();
          // Compare the key from the header with the one from properties
          if (!principalRequestValue.equals(principal)) {
            throw new BadCredentialsException("The API Key was not found or is invalid.");
          }
          authentication.setAuthenticated(true);
          return authentication;
        });

    // This is the main security configuration using the new lambda DSL
    http
        // Add the new CORS configuration here
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable()) // Disable CSRF
        .sessionManagement(
            session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS)) // Set session management to stateless
        .addFilterBefore(
            filter, UsernamePasswordAuthenticationFilter.class) // Add our custom filter
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/**")
                    .authenticated() // Secure all API endpoints
                    .anyRequest()
                    .permitAll() // Allow all other requests
            );

    return http.build();
  }

  /**
   * This Bean defines the CORS policy for your application. It configures which origins, methods,
   * and headers are allowed.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // **EDIT**: Specify your exact website domain(s) here.
    // This list allows requests from your primary domain, your Netlify subdomain, and your domain
    // alias.
    configuration.setAllowedOrigins(
        List.of(
            "https://jorgevelazque.me",
            "https://jorgevelazquez.tech",
            "https://jorgevelazquez.netlify.app"));

    // Specify the allowed HTTP methods (GET, POST, etc.)
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    // Specify the allowed headers. It's important to allow the 'x-api-key' and 'Content-Type'.
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "x-api-key"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    // Apply this configuration to all paths in your application
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
