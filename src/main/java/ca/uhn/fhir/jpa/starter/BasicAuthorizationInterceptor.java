package ca.uhn.fhir.jpa.starter;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.ForbiddenOperationException;
import ca.uhn.fhir.rest.server.interceptor.auth.AuthorizationInterceptor;
import ca.uhn.fhir.rest.server.interceptor.auth.IAuthRule;
import ca.uhn.fhir.rest.server.interceptor.auth.RuleBuilder;
import java.util.List;

public class BasicAuthorizationInterceptor extends AuthorizationInterceptor {
  private String serverClientId;
  private String serverClientSecret;

  public BasicAuthorizationInterceptor(String serverClientId, String serverClientSecret) {
    super();
    this.serverClientId = serverClientId;
    this.serverClientSecret = serverClientSecret;
  }

  @Override
  public List<IAuthRule> buildRuleList(RequestDetails theRequestDetails) {
    final String authHeader = theRequestDetails.getHeader(Constants.HEADER_AUTHORIZATION);

    if (authHeader == null) {
      throw new AuthenticationException("No credentials provided.")
          .addAuthenticateHeaderForRealm("");
    }

    if (!authHeader.startsWith(Constants.HEADER_AUTHORIZATION_VALPREFIX_BASIC)) {
      throw new ForbiddenOperationException("Unsupported auth method.");
    }

    final String[] decodedCredentials = BasicAuth.getCredentialsFromAuthorizationHeader(authHeader);
    final String requestClientId = decodedCredentials[0];
    final String requestClientSecret = decodedCredentials[1];

    if (this.serverClientId.equals(requestClientId)
        && this.serverClientSecret.equals(requestClientSecret)) {
      return new RuleBuilder().allowAll().build();
    } else {
      throw new AuthenticationException("Invalid credentials.").addAuthenticateHeaderForRealm("");
    }
  }
}
