package aleph.engineering.note.services;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import aleph.engineering.note.services.dto.UserAccountDTO;
import reactor.core.publisher.Mono;

@Service
public class UserService {


    public Mono<UserAccountDTO> getUserFromAuthentication(AbstractAuthenticationToken token) {
        Map<String, Object> details = new HashMap<>();
        if (token instanceof OAuth2AuthenticationToken) {
            details = ((OAuth2AuthenticationToken) token).getPrincipal().getAttributes();
        } else if (token instanceof JwtAuthenticationToken) {
            details = ((JwtAuthenticationToken) token).getTokenAttributes();
        } else {
            throw new IllegalArgumentException("AuthenticationToken is not OAuth2 or JWT");
        }

        UserAccountDTO accountDTO = getUserDetails(details);
        
        accountDTO.setAuthorities(token.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet()));
        return Mono.just(accountDTO);                    
    }

    private static UserAccountDTO getUserDetails(Map<String, Object> details) {
        UserAccountDTO accountDTO = new UserAccountDTO();
        Boolean activated = Boolean.TRUE;

        // handle resource server JWT, where sub claim is email and uid is ID
        if (details.get("uid") != null) {
            accountDTO.setId((String) details.get("uid"));
            accountDTO.setLogin((String) details.get(StandardClaimNames.SUB));
        } else {
            accountDTO.setId((String) details.get(StandardClaimNames.SUB));
        }

        if (details.get(StandardClaimNames.PREFERRED_USERNAME) != null) {
            accountDTO.setLogin(((String) details.get(StandardClaimNames.PREFERRED_USERNAME)).toLowerCase());
        } else if (accountDTO.getLogin() == null) {
            accountDTO.setLogin(accountDTO.getId());
        }

        if (details.get(StandardClaimNames.EMAIL_VERIFIED) != null) {
            activated = (Boolean) details.get(StandardClaimNames.EMAIL_VERIFIED);
        }

        if (details.get(StandardClaimNames.GIVEN_NAME) != null) {
            accountDTO.setFirstName((String) details.get(StandardClaimNames.GIVEN_NAME));
        }
        if (details.get(StandardClaimNames.FAMILY_NAME) != null) {
            accountDTO.setLastName((String) details.get(StandardClaimNames.FAMILY_NAME));
        }

        if (details.get(StandardClaimNames.EMAIL) != null) {
            accountDTO.setEmail(((String) details.get(StandardClaimNames.EMAIL)).toLowerCase());
        } else {
            accountDTO.setEmail((String) details.get(StandardClaimNames.SUB));
        }

        if (details.get(StandardClaimNames.PICTURE) != null) {
            accountDTO.setImageUrl((String) details.get(StandardClaimNames.PICTURE));
        }

        accountDTO.setActivated(activated);

        return accountDTO;
    }
}
