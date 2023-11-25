package aleph.engineering.note.security;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> problemDetail = new LinkedHashMap<>();
            problemDetail.put("title", "UNAUTHORIZED");
            problemDetail.put("status", HttpStatus.UNAUTHORIZED.value());
            problemDetail.put("detail", ex.getMessage());

        try {
            byte[] errorBytes = objectMapper.writeValueAsBytes(problemDetail);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errorBytes)));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }    
}
