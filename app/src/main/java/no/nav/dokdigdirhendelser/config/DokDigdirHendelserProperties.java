package no.nav.dokdigdirhendelser.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dokdigdirhendelser")
public record DokDigdirHendelserProperties(
		TopicsProperties topics
) {

	public record TopicsProperties(String altinnMeldingHendelse) {
	}
}
