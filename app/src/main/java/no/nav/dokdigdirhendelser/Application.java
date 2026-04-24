package no.nav.dokdigdirhendelser;

import no.nav.dokdigdirhendelser.config.AltinnWebhookProperties;
import no.nav.dokdigdirhendelser.config.DokDigdirHendelserProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({DokDigdirHendelserProperties.class, AltinnWebhookProperties.class})
public class Application {
	static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}