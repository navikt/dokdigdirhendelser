package no.nav.dokdigdirhendelser.altinn;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AltinnEvents(
		@NotBlank(message = "id kan ikke være tom")
		String id,
		@NotBlank(message = "resource kan ikke være tom")
		String resource,
		String resourceinstance,
		String source,
		String specversion,
		@NotBlank(message = "type kan ikke være tom")
		String type,
		String subject,
		@NotBlank(message = "alternativesubject kan ikke være tom")
		String alternativesubject,
		LocalDateTime time) {
}
