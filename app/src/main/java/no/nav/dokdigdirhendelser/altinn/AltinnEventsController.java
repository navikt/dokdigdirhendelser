package no.nav.dokdigdirhendelser.altinn;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.dokdigdirhendelser.altinn.eventvalidator.AltinnEventValidator.validerAltinnEvent;

@Slf4j
@RestController
@RequestMapping("${altinn.webhook.path}")
public class AltinnEventsController {

	private final AltinnMeldingHendelse altinnMeldingHendelse;

	public AltinnEventsController(AltinnMeldingHendelse altinnMeldingHendelse) {
		this.altinnMeldingHendelse = altinnMeldingHendelse;
	}

	@PostMapping
	public ResponseEntity<String> mottakAltinnMelding(@Valid @RequestBody AltinnEvent altinnEvent) {
		log.info("Mottatt Altinn melding med id={}, resourceinstance={}, type={}",
				altinnEvent.id(), altinnEvent.resourceinstance(), altinnEvent.type());

		validerAltinnEvent(altinnEvent);

		altinnMeldingHendelse.publish(altinnEvent);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
