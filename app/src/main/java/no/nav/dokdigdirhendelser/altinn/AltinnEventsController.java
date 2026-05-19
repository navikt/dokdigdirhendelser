package no.nav.dokdigdirhendelser.altinn;

import lombok.extern.slf4j.Slf4j;
import no.altinn.event.domain.CloudEvent;
import no.nav.dokdigdirhendelser.config.AltinnWebhookProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.dokdigdirhendelser.altinn.AltinnEventConstants.VALIDATE_SUBSCRIPTION_EVENT_TYPE;
import static no.nav.dokdigdirhendelser.altinn.CloudEventExtensions.getExtension;
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
	public ResponseEntity<String> mottakAltinnMelding(@RequestBody CloudEvent cloudEvent,
													  @RequestParam("code") String code) {
		if(!altinnWebhookCode.equals(code)) {
			log.error("Mottatt altinn hendelse behandles ikke, code matcher ikke");
			return ResponseEntity.ok().build();
		}

		if (cloudEvent.getId() == null || cloudEvent.getSource() == null || cloudEvent.getType() == null) {
			return ResponseEntity.badRequest().build();
		}

		if (isValidateSubscriptionEvent(cloudEvent)) {
			log.info("Subscription validert OK. id={}, source={}", cloudEvent.getId(), cloudEvent.getSource());
			return ResponseEntity.ok().build();
		}

		log.info("Mottatt Altinn hendelse med id={}, resourceinstance={}, type={}",
				cloudEvent.getId(), getExtension(cloudEvent, "resourceinstance"), sanitize(cloudEvent.getType()));

		validerAltinnEvent(cloudEvent);
		altinnMeldingHendelse.publish(cloudEvent);
		return ResponseEntity.ok().build();
	}

	private boolean isValidateSubscriptionEvent(CloudEvent cloudEvent) {
		return VALIDATE_SUBSCRIPTION_EVENT_TYPE.equals(cloudEvent.getType());
	}
}
