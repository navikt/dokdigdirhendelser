package no.nav.dokdigdirhendelser.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dokdigdirhendelser")
public record DokDigdirHendelserProperties(
		AltinnProperties altinn3,
		TopicsProperties topics
) {

	public record AltinnProperties(String endpoint, ScopeProperties scope) {
	}

	public record ScopeProperties(String serviceowner, String eventsSubscribe ) {
	}

	public record TopicsProperties(String altinnMeldingHendelse) {
	}
}
