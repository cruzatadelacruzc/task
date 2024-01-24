package aleph.engineering.note;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import aleph.engineering.note.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
public class NoteApplication {


	public static void main(String[] args) {
		SpringApplication.run(NoteApplication.class, args);
	}
}
