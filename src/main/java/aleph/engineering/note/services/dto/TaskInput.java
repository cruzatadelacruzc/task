package aleph.engineering.note.services.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskInput(String id, @NotBlank String body) {} 
