package ca.uhn.fhir.jpa.starter;

import ca.uhn.fhir.rest.api.Constants;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuth {
  public static String encode(String username, String password) {
    final String credentialsString = username + ":" + password;
    final String encodedString = Base64.getEncoder().encodeToString(credentialsString.getBytes());
    return encodedString;
  }

  public static String[] decode(String base64Credentials) {
    byte[] credentialsDecoded = Base64.getDecoder().decode(base64Credentials);
    final String credentials = new String(credentialsDecoded, StandardCharsets.UTF_8);
    return credentials.split(":", 2);
  }

  public static String[] getCredentialsFromAuthorizationHeader(String authHeader) {
    final String base64Credentials = authHeader.substring("Basic".length()).trim();
    return BasicAuth.decode(base64Credentials);
  }

  public static String buildAuthorizationHeaderFromCredentials(String username, String password) {
    return Constants.HEADER_AUTHORIZATION_VALPREFIX_BASIC + BasicAuth.encode(username, password);
  }
}
