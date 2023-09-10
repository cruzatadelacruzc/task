package aleph.engineering.note;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import aleph.engineering.note.config.AppProperties;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
public class NoteApplication {

	private static final Logger log = LoggerFactory.getLogger(NoteApplication.class);
	private static final String PROFILE_DEV = "dev";
	private static final String PROFILE_PROD = "prod";
	Environment environment;

	public NoteApplication(Environment environment) {
		this.environment = environment;
	}

	public static void main(String[] args) {
		SpringApplication noteApp = new SpringApplication(NoteApplication.class);
		addDefaultProfile(noteApp);
		Environment environment = noteApp.run(args).getEnvironment();	
		logApplicationStartup(environment);
	}
	
	private static void logApplicationStartup(Environment environment2) {
		String protocol = Optional.ofNullable(environment2.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
		String serverPort = environment2.getProperty("server.port");
		String contextPath = Optional
				.ofNullable(environment2.getProperty("server.servlet.context-path"))
				.filter(str -> str != null && !str.trim().isEmpty())
				.orElse("/");
		String hostAddress = "localhost";
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			log.warn("The host name could not be determined, using `localhost` as fallback.");
		}
		log.info(
			"\n----------------------------------------------------------\n\t" +
			"Application '{}' is running! Access URLs:\n\t" +
			"Local: \t\t{}://localhost:{}{}\n\t" +
			"External: \t{}://{}:{}{}\n\t" +
			"Profile(s): \t{}\n----------------------------------------------------------",
			environment2.getProperty("spring.application.name"),
			protocol,
			serverPort,
			contextPath,
			protocol,
			hostAddress,
			serverPort,
			contextPath,
			environment2.getActiveProfiles()
		);		
	}

	/**
	 * Initializes NoteApplication.
	 * <p>
	 * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
	 * <p>
	 */
	@PostConstruct
	public void initApplication() {
		Collection<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
		if (activeProfiles.contains(PROFILE_DEV) && activeProfiles.contains(PROFILE_PROD)) {
			log.error(
					"You have misconfigured your application! It should not run " + "with both the 'dev' and 'prod' profiles at the same time."
			);
		}
	}
	/**
	 * Set a default to use when no profile is configured.
	 *
	 * @param app the Spring application.
	 */
	public static void addDefaultProfile(SpringApplication app) {
        Map<String, Object> defProperties = new HashMap<>();
        defProperties.put("spring.profiles.active", PROFILE_DEV);
        app.setDefaultProperties(defProperties);
    }
}
