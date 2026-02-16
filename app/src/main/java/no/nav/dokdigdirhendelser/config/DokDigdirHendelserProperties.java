package no.nav.dokdigdirhendelser.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "dokdigdirhendelser")
public record DokDigdirHendelserProperties(
		@NotBlank
		String altinnUrl,
		@Valid
		AbonnementProperties subscription,
		@Valid
		TopicsProperties topics
) {

	public record TopicsProperties(
			@NotBlank
			String altinnMeldingHendelse) {
	}

	public record AbonnementProperties(
			@NotBlank
			String endPoint,
			@NotBlank
			String sourceFilter,
			boolean enabled) {
	}
}
