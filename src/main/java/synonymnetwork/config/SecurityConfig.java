package synonymnetwork.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${api.security.key}")
  private String principalRequestValue;

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
    http.csrf(csrf -> csrf.disable()) // Disable CSRF
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
}
