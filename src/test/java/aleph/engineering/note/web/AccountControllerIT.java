package aleph.engineering.note.web;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOidcLogin;
import static aleph.engineering.note.OAuth2TestUtil.oidcUserTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import aleph.engineering.note.IntegrationTest;
import aleph.engineering.note.security.AuthoritiesConstants;

@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
public class AccountControllerIT {

    private static final String CLAIM_SUB = "4029fe34-d0fe-42ff-9ce5-b0f3c8262e95";
    private static final String CLAIM_EMAIL = "jhon.doe@mail.com";
    private static final String CLAIM_PREFERRED_USERNAME = "jdoe";
    private static final String CLAIM_FIRST_NAME = "Jhon";
    private static final String CLAIM_LAST_NAME = "Doe";
    private static final String CLAIM_PICTURE = "https://picture.com";

    @Autowired
    private WebTestClient webTestClient;


    @Test
    void testGetExistingAccount() {
       Map<String, Object> claims = new HashMap<>();
       claims.put(StandardClaimNames.SUB, CLAIM_SUB);
       claims.put(StandardClaimNames.EMAIL, CLAIM_EMAIL);
       claims.put(StandardClaimNames.PICTURE, CLAIM_PICTURE);
       claims.put(StandardClaimNames.GIVEN_NAME, CLAIM_FIRST_NAME);
       claims.put(StandardClaimNames.FAMILY_NAME, CLAIM_LAST_NAME);
       claims.put(StandardClaimNames.PREFERRED_USERNAME, CLAIM_PREFERRED_USERNAME);
       claims.put("groups", Collections.singletonList(AuthoritiesConstants.TASK_WRITE));

       webTestClient.mutateWith(mockOidcLogin().oidcUser(oidcUserTest(claims)))
                    .get().uri("/api/account")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(CLAIM_SUB)
                    .jsonPath("$.email").isEqualTo(CLAIM_EMAIL)
                    .jsonPath("$.imageUrl").isEqualTo(CLAIM_PICTURE)
                    .jsonPath("$.lastName").isEqualTo(CLAIM_LAST_NAME)
                    .jsonPath("$.firstName").isEqualTo(CLAIM_FIRST_NAME)
                    .jsonPath("$.activated").isEqualTo(true)
                    .jsonPath("$.login").isEqualTo(CLAIM_PREFERRED_USERNAME)
                    .jsonPath("$.authorities").isEqualTo(AuthoritiesConstants.TASK_WRITE);

    }

    @Test
    @WithMockUser
    void testGetUnknownAccount() {
        webTestClient.get().uri("/api/account")
        .accept(MediaType.APPLICATION_JSON)
        .exchange().expectStatus().is5xxServerError();
    }
    
}
