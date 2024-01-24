package aleph.engineering.note.web.filter;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/*
 * WebFlux filter for handling CSRF cookies in requests.
 * See https://github.com/spring-projects/spring-security/issues/5766#issuecomment-564636167
 */
public class CsrfCookieFilter implements WebFilter {

    /**
     * Filter for handling CSRF logic in a reactive environment.
     *
     * @param exchange The server web exchange.
     * @param chain    Web filter chain for continuing with the processing.
     * @return A {@code Mono<Void>} representing the asynchronous operation of the filter.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
         // Get the Mono representing the CSRF token from the exchange.
        Mono<CsrfToken> csrfTokenMono = exchange.getAttribute(CsrfToken.class.getName());

        // If csrfTokenMono is not null, execute chain.filter(exchange) after it completes successfully.
        // If csrfTokenMono is null, simply execute chain.filter(exchange).
        return csrfTokenMono != null
                ? csrfTokenMono.then(chain.filter(exchange))
                : chain.filter(exchange);
    }
}
