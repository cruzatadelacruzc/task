package aleph.engineering.note.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import aleph.engineering.note.IntegrationTest;


@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
public class CustomAuthenticationEntryPointIT {
    
   @Autowired
    private WebTestClient webTestClient;
    
    @BeforeEach
    public void setUp(ApplicationContext applicationContext) {
        webTestClient = WebTestClient
                        .bindToApplicationContext(applicationContext)
                        .configureClient().baseUrl("/api/graphql").build();
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserIsNotAuthenticated () {

        webTestClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ \"query\": \"{ tasks { id body } }\"}")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void shouldReturnCustomMessageWhenUserIsNotAuthenticated () {

        webTestClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ \"query\": \"{ tasks { id body } }\"}")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Unauthorized")
                .jsonPath("$.detail").isEqualTo("Not Authenticated")
                .jsonPath("$.status").isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
