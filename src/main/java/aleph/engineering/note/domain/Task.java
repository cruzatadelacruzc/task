package aleph.engineering.note.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Task(@Id String id, String body) {}
