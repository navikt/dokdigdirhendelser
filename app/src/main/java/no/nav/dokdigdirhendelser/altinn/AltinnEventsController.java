package no.nav.dokdigdirhendelser.altinn;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.dokdigdirhendelser.altinn.AltinnEvent.VALIDATE_SUBSCRIPTION_EVENT_TYPE;
import static no.nav.dokdigdirhendelser.altinn.eventvalidator.AltinnEventValidator.validerAltinnEvent;
import static no.nav.dokdigdirhendelser.utils.SafeLog.sanitize;

@Slf4j
@RestController
@RequestMapping("${altinn.webhook.path}")
public class AltinnEventsController {

	private final AltinnMeldingHendelse altinnMeldingHendelse;

	public AltinnEventsController(AltinnMeldingHendelse altinnMeldingHendelse) {
		this.altinnMeldingHendelse = altinnMeldingHendelse;
	}

	@PostMapping
	public ResponseEntity<String> mottakAltinnMelding(@RequestBody AltinnEvent altinnEvent) {
		if (isValidateSubscriptionEvent(altinnEvent)) {
			log.info("Subscription validert OK. id={}, source={}", altinnEvent.id(), altinnEvent.source());
			return ResponseEntity.ok().build();
		}

		log.info("Mottatt Altinn melding med id={}, resourceinstance={}, type={}",
				altinnEvent.id(), altinnEvent.resourceinstance(), sanitize(altinnEvent.type()));

		validerAltinnEvent(altinnEvent);
		altinnMeldingHendelse.publish(altinnEvent);
		return ResponseEntity.ok().build();
	}

	private boolean isValidateSubscriptionEvent(AltinnEvent altinnEvent) {
		return VALIDATE_SUBSCRIPTION_EVENT_TYPE.equals(altinnEvent.type());
	}
}
