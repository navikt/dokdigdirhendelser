package no.nav.dokdigdirhendelser.altinn.eventvalidator;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdigdirhendelser.altinn.AltinnEvents;

import static java.lang.String.format;
import static no.nav.dokdigdirhendelser.altinn.EventType.isValid;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_EVENTS_RESOURCE;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.SPEC_VERSION;

@Slf4j
public class ValiderAltinnEvents {

	public static void validerAltinnEvent(AltinnEvents altinnEvents) {
		if (!ALTINN_EVENTS_RESOURCE.equals(altinnEvents.resource())) {
			log.error("Ugyldig resource: med resourceinstance={} og type={}", altinnEvents.resourceinstance(), altinnEvents.type());
			throw new IllegalArgumentException("Ugyldig verdi: resource er ikke lik " + ALTINN_EVENTS_RESOURCE);
		}

		if (!isValid(altinnEvents.type())) {
			log.error("Ugyldig type: med resourceinstance={} og type={}", altinnEvents.resourceinstance(), altinnEvents.type());
			throw new IllegalArgumentException(format("%s er ugyldig Altinn event type", altinnEvents.type()));
		}

		if (!SPEC_VERSION.equals(altinnEvents.specversion())) {
			log.error("Ugyldig specversion og må være 1.0 med resourceinstance={} og type={}", altinnEvents.resourceinstance(), altinnEvents.type());
			throw new IllegalArgumentException("Ugyldig verdi: specversion må være 1.0");
		}
	}

	private ValiderAltinnEvents() {
	}
}
