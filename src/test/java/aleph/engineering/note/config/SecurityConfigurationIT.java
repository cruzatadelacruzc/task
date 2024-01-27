package aleph.engineering.note.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import aleph.engineering.note.IntegrationTest;


@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
public class SecurityConfigurationIT {
    
   @Autowired
    private WebTestClient webTestClient;
   

    @Test
    void shouldReturnUnauthorizedWhenUserIsNotAuthenticated () {

        webTestClient.mutateWith(csrf()).post()
                .uri("/api/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ \"query\": \"{ tasks { id body } }\"}")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void shouldReturnCustomMessageWhenUserIsNotAuthenticated () {

        webTestClient.mutateWith(csrf()).post()
                .uri("/api/graphql")
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
