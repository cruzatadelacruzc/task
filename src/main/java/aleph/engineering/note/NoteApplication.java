package aleph.engineering.note;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import aleph.engineering.note.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
public class NoteApplication {


	public static void main(String[] args) {
		SpringApplication noteApp = new SpringApplication(NoteApplication.class);
		Map<String, Object> defProperties = new HashMap<>();
        defProperties.put("spring.profiles.active", "dev");
        noteApp.setDefaultProperties(defProperties);
		noteApp.run(args);
	}
}
