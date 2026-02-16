package no.nav.dokdigdirhendelser.altinnsubscription.naistexas;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("maskinporten")
public record MaskinportenProperties(
		@NotBlank
		String scopes) {
}

