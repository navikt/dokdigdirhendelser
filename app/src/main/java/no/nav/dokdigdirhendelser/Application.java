package no.nav.dokdigdirhendelser;

import no.nav.dokdigdirhendelser.altinnsubscription.naistexas.MaskinportenProperties;
import no.nav.dokdigdirhendelser.config.DokDigdirHendelserProperties;
import no.nav.dokdigdirhendelser.config.NaisProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		DokDigdirHendelserProperties.class,
		MaskinportenProperties.class,
		NaisProperties.class})
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}