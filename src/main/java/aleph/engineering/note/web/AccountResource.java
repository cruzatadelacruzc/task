package aleph.engineering.note.web;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import aleph.engineering.note.services.UserService;
import aleph.engineering.note.services.dto.UserAccountDTO;
import reactor.core.publisher.Mono;


@RestController
public class AccountResource {
    private static final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserService userService;
    private final MessageSource messageSource;

    private static class UserAccountDetailsException extends RuntimeException{
        private static final long serialVersionUID = 1L;

        private UserAccountDetailsException(String message) {
            super(message);
        }
    }
    

    public AccountResource(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    /**
     * Retrieves user account details based on the provided principal.
     *
     * @param principal The principal representing the current user.
     * @return A Mono containing the user account details.
     * @throws UserAccountDetailsException If the principal does not represent an authenticated user.
     */
    @GetMapping("/api/account")
    public Mono<UserAccountDTO> getAccountDetails(Principal principal) {
        log.debug("REST request to get User account details, principal {}", principal);
        if (principal instanceof AbstractAuthenticationToken) {
            return userService.getUserFromAuthentication((AbstractAuthenticationToken) principal);
        } else {
            throw new UserAccountDetailsException(messageSource.getMessage(
                    "account.notfound", null, LocaleContextHolder.getLocale()));
        }
    }    
}
