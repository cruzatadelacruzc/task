package aleph.engineering.note.config;

import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MongoDBContainer;

@Configuration
@TestConfiguration(proxyBeanMethods = false)
public class MongoDBTestContainer {

    @Bean
    @ServiceConnection
    @RestartScope
     MongoDBContainer mongoDBContainer() {
         return new MongoDBContainer("mongo:4.4");
     }
    
}
