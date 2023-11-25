package aleph.engineering.note.security;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

         Map<String, Object> problemDetail = new LinkedHashMap<>();
            problemDetail.put("title", "FORBIDDEN");
            problemDetail.put("status", HttpStatus.FORBIDDEN.value());
            problemDetail.put("detail", denied.getMessage());

        try {
            byte[] errorBytes = objectMapper.writeValueAsBytes(problemDetail);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errorBytes)));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }

    }    
}
