package no.nav.dokdigdirhendelser.altinn.eventvalidator;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdigdirhendelser.altinn.AltinnEvent;
import no.nav.dokdigdirhendelser.exception.DokDigdirHendelserTechnicalException;
import no.nav.dokdigdirhendelser.exception.HendelseTypeBehandlesIkkeException;

import static no.nav.dokdigdirhendelser.altinn.AltinnEvent.CORRESPONDENCE_EVENT_TYPES;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_EVENTS_RESOURCE;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.SPEC_VERSION;
import static no.nav.dokdigdirhendelser.utils.SafeLog.sanitize;

@Slf4j
public class AltinnEventValidator {

	public static void validerAltinnEvent(AltinnEvent altinnEvent) {
		if (altinnEvent == null) {
			throw new DokDigdirHendelserTechnicalException("AltinnEvent kan ikke være null");
		}

		if (!ALTINN_EVENTS_RESOURCE.equals(altinnEvent.resource())) {
			throw new IllegalArgumentException("Ugyldig verdi: resource er ikke lik " + ALTINN_EVENTS_RESOURCE);
		}

		if (!CORRESPONDENCE_EVENT_TYPES.contains(altinnEvent.type())) {
			throw new HendelseTypeBehandlesIkkeException("Hendelsestype=%s behandles ikke".formatted(sanitize(altinnEvent.type())));
		}

		if (!SPEC_VERSION.equals(altinnEvent.specversion())) {
			throw new DokDigdirHendelserTechnicalException("Ugyldig verdi: specversion må være 1.0");
		}
	}

	private AltinnEventValidator() {
	}
}
