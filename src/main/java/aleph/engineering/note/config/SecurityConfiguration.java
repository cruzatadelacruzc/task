package aleph.engineering.note.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.cors.reactive.CorsWebFilter;

import aleph.engineering.note.security.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    private final CorsWebFilter corsWebFilter;

    public SecurityConfiguration(CorsWebFilter corsWebFilter) {
        this.corsWebFilter = corsWebFilter;
    }


    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public SecurityWebFilterChain apiSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(ex -> ex
                                            .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                                            .accessDeniedHandler(new CustomAccessDeniedHandler()))                
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/graphql"))
                .authorizeExchange(request -> request.anyExchange().authenticated())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }


    @Bean
    public SecurityWebFilterChain managementSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .addFilterAfter(corsWebFilter, SecurityWebFiltersOrder.REACTOR_CONTEXT)                
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/management/**"))
                .authorizeExchange(request -> request
                    .pathMatchers("/management/info").permitAll()
                    .pathMatchers("/management/health").permitAll()
                    .pathMatchers("/management/health/**").permitAll()
                    .pathMatchers("/management/**").authenticated())
                    .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

    @Profile("dev")
    @Bean
    SecurityWebFilterChain webHttpSecurity(ServerHttpSecurity http) {                       
        http
            .authorizeExchange((exchanges) -> exchanges.anyExchange().authenticated())
            .oauth2Login(Customizer.withDefaults());                                         
        return http.build();
    }

}
