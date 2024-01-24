package aleph.engineering.note.web.filter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import aleph.engineering.note.IntegrationTest;

@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
public class CookieCsrfFilterIT {
        
    @Autowired
    private WebTestClient webTestClient;    

    @Test
    public void testCsrfTokenInCookie(ApplicationContext context) {
        webTestClient = WebTestClient
                        .bindToApplicationContext(context)
                        .configureClient()
                        .baseUrl("/api/account")
                        .build();
        webTestClient.get()
            .exchange()
            .expectStatus().isUnauthorized()
            .expectHeader().valueMatches("Set-Cookie", "XSRF-TOKEN=(.*?);.*");       
    }
}
