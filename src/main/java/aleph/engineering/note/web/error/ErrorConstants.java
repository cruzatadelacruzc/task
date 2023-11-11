package aleph.engineering.note.web.error;

import java.net.URI;

public final class ErrorConstants {
    public static final String PROBLEM_BASE_URL = "https://api.example.com/problem";
    public static final URI NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/not-found");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");

    private ErrorConstants() {}
}
