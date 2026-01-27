package no.nav.dokdigdirhendelser.altinn;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import no.nav.dokdigdirhendelser.altinn.eventvalidator.ValiderTime;

import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.UUID_REGEX;

@Builder
@ValiderTime
public record AltinnEvents(
		@NotBlank(message = "id kan ikke være tom")
		@Pattern(regexp = UUID_REGEX, message = "id må være en gyldig UUID")
		String id,
		@NotBlank(message = "resource kan ikke være tom")
		String resource,
		@Pattern(regexp = UUID_REGEX, message = "resourceinstance må være en gyldig UUID")
		String resourceinstance,
		@NotBlank(message = "source må være en gyldig Altinn instance URL")
		String source,
		@NotBlank(message = "specversion kan ikke være tom")
		String specversion,
		@NotBlank(message = "type kan ikke være tom")
		String type,
		String subject,
		@NotBlank(message = "alternativesubject kan ikke være tom")
		@Pattern(regexp = "^/organisation/\\d{9}$", message = "alternativesubject må være på formatet /organisation/{9 siffer}")
		String alternativesubject,
		String time) {
}
