package ca.uhn.fhir.jpa.starter;

import ca.uhn.fhir.rest.api.Constants;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityServletFilter extends HttpFilter {
  private static final long serialVersionUID = 1L;

  private String username;
  private String password;

  public SecurityServletFilter(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  protected void doFilter(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    String path = request.getRequestURI();
    if ("/websocket".equals(path) || "/fhir".equals(path)) {
      chain.doFilter(request, response);
      return;
    }

    String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);
    if (authHeader == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
      response.setHeader("WWW-Authenticate", "Basic realm=\"\"");
      return;
    }

    final String[] credentials = BasicAuth.getCredentialsFromAuthorizationHeader(authHeader);
    if (!this.username.equals(credentials[0]) || !this.password.equals(credentials[1])) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
      response.setHeader("WWW-Authenticate", "Basic realm=\"\"");
      return;
    }

    // allow the HttpRequest to go to Spring's DispatcherServlet
    // and @RestControllers/@Controllers.
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    // nothing
  }
}
