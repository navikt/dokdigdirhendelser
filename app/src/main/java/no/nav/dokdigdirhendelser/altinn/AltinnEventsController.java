package no.nav.dokdigdirhendelser.altinn;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("${altinn.webhook.path}")
public class AltinnEventsController {

	private final AltinnEventProducerService altinnEventProducerService;

	public AltinnEventsController(AltinnEventProducerService altinnEventProducerService) {
		this.altinnEventProducerService = altinnEventProducerService;
	}

	@PostMapping
	public ResponseEntity<String> mottakAltinnMelding(@Valid @RequestBody AltinnEvents altinnEvents) {

		try {
			altinnEventProducerService.validateAltinnEvent(altinnEvents);
			log.info("Mottatt Altinn melding med id={}, resourceinstance={}, type={}",
					altinnEvents.id(), altinnEvents.resourceinstance(), altinnEvents.type());

			altinnEventProducerService.publish(altinnEvents);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (IllegalArgumentException e) {
			log.error("Ugyldig Altinn hendelser: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Ugyldig hendelser: " + e.getMessage());
		} catch (Exception e) {
			log.error("Feil ved behandling av Altinn hendelser: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Feil ved behandling av altinn hendelser: " + e.getMessage());
		}
	}

}
