import au.com.dius.pact.consumer.ConsumerPactTest;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class PactExampleTest extends ConsumerPactTest {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String APPLICATION_JSON = "application/json.*";
  private static final String APPLICATION_JSON_ = "application/json";
  private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=UTF-8";


  protected PactFragment createFragment(PactDslWithProvider builder) {

    // build response json
    DslPart jsonBody = new PactDslJsonBody()
        .stringValue("base", "EUR")
        .date("date")
        .object("rates")
        .decimalType("GBP")
        .decimalType("USD")
        .closeObject();

    
    return builder
        .uponReceiving("Check latest dollar and pound")
        .method("GET")
        .path("/latest")
        .matchHeader(CONTENT_TYPE, APPLICATION_JSON)
        .query("symbols=USD,GBP").
            willRespondWith()
            .status(200)
            .body(jsonBody)
            .toFragment();
  }

  protected String providerName() {
    return "currency";
  }

  protected String consumerName() {
    return "org.qinyu";
  }

  protected void runTest(String url) throws IOException {

    given()
        .baseUri(url)
        .queryParams("symbols", "USD,GBP")
        .contentType(APPLICATION_JSON_).
        when()
        .get("latest").
        then()
        .statusCode(200)
        .body("base", equalTo("EUR"));

  }
}
