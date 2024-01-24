package aleph.engineering.note.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Properties specific to Note.
 * <p>
 * Properties are configured in the {@code application-{stage}.yml} file.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class AppProperties {

    private final CorsConfiguration cors = new CorsConfiguration();
    private final Security security = new Security();

    public CorsConfiguration getCors() {
        return cors;
    }

    public Security getSecurity() {
        return security;
    }
    
    public static class Security {
        private String contentSecurityPolicy = "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:";        

        public String getContentSecurityPolicy() {
            return contentSecurityPolicy;
        }

        public void setContentSecurityPolicy(String contentSecurityPolicy) {
            this.contentSecurityPolicy = contentSecurityPolicy;
        }
    }

}
