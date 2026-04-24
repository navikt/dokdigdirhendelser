package no.nav.dokdigdirhendelser.altinn;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdigdirhendelser.config.AltinnWebhookProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.dokdigdirhendelser.altinn.AltinnEvent.VALIDATE_SUBSCRIPTION_EVENT_TYPE;
import static no.nav.dokdigdirhendelser.altinn.eventvalidator.AltinnEventValidator.validerAltinnEvent;
import static no.nav.dokdigdirhendelser.utils.SafeLog.sanitize;

@Slf4j
@RestController
@RequestMapping("${altinn.webhook.path}")
public class AltinnEventsController {

	private final AltinnMeldingHendelse altinnMeldingHendelse;
	private final String altinnWebhookCode;

	public AltinnEventsController(AltinnMeldingHendelse altinnMeldingHendelse,
								  AltinnWebhookProperties altinnWebhookProperties) {
		this.altinnMeldingHendelse = altinnMeldingHendelse;
		this.altinnWebhookCode = altinnWebhookProperties.code();
	}

	@PostMapping
	public ResponseEntity<String> mottakAltinnMelding(@RequestBody AltinnEvent altinnEvent,
													  @RequestParam("code") String code) {
		if(!altinnWebhookCode.equals(code)) {
			log.error("Mottatt altinn hendelse behandles ikke, code matcher ikke");
			return ResponseEntity.ok().build();
		}

		if (isValidateSubscriptionEvent(altinnEvent)) {
			log.info("Subscription validert OK. id={}, source={}", altinnEvent.id(), altinnEvent.source());
			return ResponseEntity.ok().build();
		}

		log.info("Mottatt Altinn hendelse med id={}, resourceinstance={}, type={}",
				altinnEvent.id(), altinnEvent.resourceinstance(), sanitize(altinnEvent.type()));

		validerAltinnEvent(altinnEvent);
		altinnMeldingHendelse.publish(altinnEvent);
		return ResponseEntity.ok().build();
	}

	private boolean isValidateSubscriptionEvent(AltinnEvent altinnEvent) {
		return VALIDATE_SUBSCRIPTION_EVENT_TYPE.equals(altinnEvent.type());
	}
}
