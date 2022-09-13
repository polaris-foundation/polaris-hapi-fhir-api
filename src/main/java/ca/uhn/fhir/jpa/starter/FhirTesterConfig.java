package ca.uhn.fhir.jpa.starter;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.server.util.ITestingUiClientFactory;
import ca.uhn.fhir.to.FhirTesterMvcConfig;
import ca.uhn.fhir.to.TesterConfig;
import com.google.common.base.Strings;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

// @formatter:off
/**
 * This spring config file configures the web testing module. It serves two purposes: 1. It imports
 * FhirTesterMvcConfig, which is the spring config for the tester itself 2. It tells the tester
 * which server(s) to talk to, via the testerConfig() method below
 */
@Configuration
@Import(FhirTesterMvcConfig.class)
public class FhirTesterConfig {

  /**
   * This bean tells the testing webpage which servers it should configure itself to communicate
   * with. In this example we configure it to talk to the local server, as well as one public
   * server. If you are creating a project to deploy somewhere else, you might choose to only put
   * your own server's address here.
   *
   * <p>Note the use of the ${serverBase} variable below. This will be replaced with the base URL as
   * reported by the server itself. Often for a simple Tomcat (or other container) installation,
   * this will end up being something like "http://localhost:8080/hapi-fhir-jpaserver-starter". If
   * you are deploying your server to a place with a fully qualified domain name, you might want to
   * use that instead of using the variable.
   */
  @Bean
  public TesterConfig testerConfig(AppProperties appProperties) {
    TesterConfig retVal = new TesterConfig();

    String server_client_id = appProperties.getServer_client_id();
    String server_client_secret = appProperties.getServer_client_secret();

    appProperties.getTester().entrySet().stream()
        .forEach(
            t -> {
              retVal
                  .addServer()
                  .withId(t.getKey())
                  .withFhirVersion(t.getValue().getFhir_version())
                  .withBaseUrl(t.getValue().getServer_address())
                  .withName(t.getValue().getName());

              retVal.setRefuseToFetchThirdPartyUrls(
                  t.getValue().getRefuse_to_fetch_third_party_urls());

              // The code below gives Tester WEB UI ability to attach authorization header to all
              // its outgoing requests.
              // This is actually a hack, since the header is being attached at the servlet layer,
              // not at the client side.
              if (!Strings.isNullOrEmpty(server_client_id)
                  && !Strings.isNullOrEmpty(server_client_secret)) {
                ITestingUiClientFactory clientFactory =
                    new ITestingUiClientFactory() {

                      @Override
                      public IGenericClient newClient(
                          FhirContext theFhirContext,
                          HttpServletRequest theRequest,
                          String theServerBaseUrl) {
                        IGenericClient client =
                            theFhirContext.newRestfulGenericClient(theServerBaseUrl);
                        String authHeader =
                            theRequest.getHeader(
                                Constants
                                    .HEADER_AUTHORIZATION); // just propagate the existing header
                        // without extracting credentials from
                        // it

                        client.registerInterceptor(
                            new IClientInterceptor() {
                              @Override
                              public void interceptRequest(IHttpRequest theRequest) {
                                if (authHeader != null) {
                                  theRequest.addHeader(Constants.HEADER_AUTHORIZATION, authHeader);
                                }
                              }

                              @Override
                              public void interceptResponse(IHttpResponse theResponse)
                                  throws IOException {
                                // do nothing
                              }
                            });
                        return client;
                      }
                    };
                retVal.setClientFactory(clientFactory);
              }
            });
    return retVal;
  }
}
// @formatter:on
