package aleph.engineering.note.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.server.header.XXssProtectionServerHttpHeadersWriter;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode;
import org.zalando.problem.spring.webflux.advice.security.SecurityProblemSupport;

import aleph.engineering.note.web.filter.CsrfCookieFilter;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration {

    private final SecurityProblemSupport problemSupport; 
    private final AppProperties appProperties;
    private final String PERMISSIONS_POLICY = "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()";


    public SecurityConfiguration(SecurityProblemSupport problemSupport, AppProperties appProperties) {
        this.problemSupport = problemSupport;
        this.appProperties = appProperties;
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public SecurityWebFilterChain apiSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())                                                           
                // See https://stackoverflow.com/q/74447118/65681                                          
                .csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                                  .csrfTokenRequestHandler(new ServerCsrfTokenRequestAttributeHandler()))
                // See https://github.com/spring-projects/spring-security/issues/5766                              
                .addFilterAt(new CsrfCookieFilter(), SecurityWebFiltersOrder.REACTOR_CONTEXT)                                                                            
                .headers(header -> header
                                    .xssProtection(xssp -> xssp.headerValue(XXssProtectionServerHttpHeadersWriter.HeaderValue.ENABLED_MODE_BLOCK))
                                    .frameOptions(fo -> fo.mode(Mode.DENY))
                                    .contentSecurityPolicy(csp -> csp.policyDirectives(appProperties.getSecurity().getContentSecurityPolicy()))
                                    .hsts(Customizer.withDefaults())
                                    .referrerPolicy(rp -> rp.policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                                    .permissionsPolicy(perm -> perm.policy(PERMISSIONS_POLICY)))                                
                .exceptionHandling(ex -> ex.authenticationEntryPoint(problemSupport).accessDeniedHandler(problemSupport))                
                .authorizeExchange(request -> request
                                            .pathMatchers("/").permitAll()
                                            .pathMatchers("/api/management/info").permitAll()
                                            .pathMatchers("/api/management/health").permitAll()
                                            .pathMatchers("/api/management/health/**").permitAll()
                                            .pathMatchers("/api/management/**").authenticated()
                                            .pathMatchers("/api/**").authenticated())
                .oauth2Login(Customizer.withDefaults())
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
