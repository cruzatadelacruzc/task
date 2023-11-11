package aleph.engineering.note.config;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

/**
 * This class allows you to run unit and integration tests without an IdP.
 */
@TestConfiguration
public class TestSecurityConfiguration {
   
    @Bean
    ClientRegistration clientRegistration() {
        return clientRegistrationBuilder().build();
    }

    @Bean
    ReactiveClientRegistrationRepository clientRegistrationRepository(ClientRegistration clientRegistration) {
        return new InMemoryReactiveClientRegistrationRepository(clientRegistration);
    }

    private ClientRegistration.Builder clientRegistrationBuilder() {

        return ClientRegistration
            .withRegistrationId("fake")
            .issuerUri("{baseUrl}")
            .clientId("fake-client-id")
            .clientSecret("fake-client-secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
            .scope("openid", "profile", "email")
            .authorizationUri("https://fake-issuer.com/oauth2/authorize")
            .tokenUri("https://fake-issuer.com/oauth2/token")
            .userInfoUri("https://fake-issuer.com/userinfo")
            .jwkSetUri("https://fake-issuer.com/.well-known/jwks.json")
            .clientName("fake client")
            .userNameAttributeName(IdTokenClaimNames.SUB);
    }

    @Bean
    ReactiveJwtDecoder jwtDecoder() {
        return mock(ReactiveJwtDecoder.class);
    }

    @Bean
    ReactiveOAuth2AuthorizedClientService authorizedClientService(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
    }
}
