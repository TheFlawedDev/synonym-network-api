package synonymnetwork.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

  private final String principalRequestHeader;

  public ApiKeyAuthFilter(String principalRequestHeader) {
    this.principalRequestHeader = principalRequestHeader;
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    // Get the API Key from the 'x-api-key' header
    return request.getHeader(principalRequestHeader);
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    // No credentials needed, we only use the principal (the API key)
    return "N/A";
  }
}
