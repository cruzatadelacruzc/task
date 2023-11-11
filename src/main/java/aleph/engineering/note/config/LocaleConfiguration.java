package aleph.engineering.note.config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.i18n.LocaleContextResolver;

@Configuration
public class LocaleConfiguration {

    @Bean
    public WebFilter localeChangeFilter(LocaleContextResolver localeContextResolver) {
        return (exchange, chain) -> {
            String language = exchange.getRequest().getHeaders().getFirst(HttpHeaders.ACCEPT_LANGUAGE);
            boolean isLanguage = language != null && !language.trim().isEmpty() && !language.trim().isBlank();
            if (isLanguage) {
                List<Locale.LanguageRange> list = Locale.LanguageRange.parse(language);
                Locale locale = Locale.lookup(list, Arrays.asList(Locale.ENGLISH, new Locale("es")));
                LocaleContextHolder.setLocale(locale);
            }
            // Proceed in any case.
            return chain.filter(exchange);
        };
    }

}