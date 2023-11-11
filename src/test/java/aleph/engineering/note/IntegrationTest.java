package aleph.engineering.note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import aleph.engineering.note.config.MongoDBTestContainer;
import aleph.engineering.note.config.TestSecurityConfiguration;

@Target (ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = {NoteApplication.class, TestSecurityConfiguration.class, MongoDBTestContainer.class})
@AutoConfigureGraphQlTester
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {
    // 5s is the spring default https://github.com/spring-projects/spring-framework/blob/29185a3d28fa5e9c1b4821ffe519ef6f56b51962/spring-test/src/main/java/org/springframework/test/web/reactive/server/DefaultWebTestClient.java#L106
    String DEFAULT_TIMEOUT = "PT5S";

    String DEFAULT_ENTITY_TIMEOUT = "PT5S";
}