package aleph.engineering.note.web.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

public interface ResponseUtil {
    static <X> Mono<ResponseEntity<X>> foundOrNot(Mono<X> response) {
        return foundOrNot(response, null);
    }

    static <X> Mono<ResponseEntity<X>> foundOrNot(Mono<X> response, HttpHeaders httpHeaders) {
        return response.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(ResponseEntity.ok().headers(httpHeaders)::body);
    } 
}
