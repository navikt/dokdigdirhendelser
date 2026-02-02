package no.nav.dokdigdirhendelser.altinn.eventvalidator;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdigdirhendelser.altinn.AltinnEvents;
import no.nav.dokdigdirhendelser.exception.DokDigdirHendelserTechnicalException;

import static java.lang.String.format;
import static no.nav.dokdigdirhendelser.altinn.AltinnEvents.ALTINN_EVENT_TYPES;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_EVENTS_RESOURCE;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.SPEC_VERSION;

@Slf4j
public class ValiderAltinnEvents {

	public static void validerAltinnEvent(AltinnEvents altinnEvents) {
		if (altinnEvents == null) {
			throw new IllegalArgumentException("altinnEvents kan ikke være null");
		}

		if (!ALTINN_EVENTS_RESOURCE.equals(altinnEvents.resource())) {
			throw new IllegalArgumentException("Ugyldig verdi: resource er ikke lik " + ALTINN_EVENTS_RESOURCE);
		}

		if (!ALTINN_EVENT_TYPES.contains(altinnEvents.type())) {
			throw new IllegalArgumentException(format("Ugyldig verdi: \"%s\"er ugyldig event type", altinnEvents.type()));
		}

		if (!SPEC_VERSION.equals(altinnEvents.specversion())) {
			throw new DokDigdirHendelserTechnicalException("Ugyldig verdi: specversion må være 1.0");
		}
	}

	private ValiderAltinnEvents() {
	}
}
