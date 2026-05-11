package no.nav.dokdigdirhendelser.altinn.eventvalidator;

import lombok.extern.slf4j.Slf4j;
import no.altinn.event.domain.CloudEvent;
import no.nav.dokdigdirhendelser.exception.DokDigdirHendelserTechnicalException;
import no.nav.dokdigdirhendelser.exception.HendelseTypeBehandlesIkkeException;

import static no.nav.dokdigdirhendelser.altinn.AltinnEventType.CORRESPONDENCE_EVENT_TYPES;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_EVENTS_RESOURCE;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.SPEC_VERSION;
import static no.nav.dokdigdirhendelser.utils.SafeLog.sanitize;

@Slf4j
public class AltinnEventValidator {

	public static void validerAltinnEvent(CloudEvent altinnEvent) {
		if (altinnEvent == null) {
			throw new DokDigdirHendelserTechnicalException("AltinnEvent kan ikke være null");
		}

		if (!ALTINN_EVENTS_RESOURCE.equals(altinnEvent.getResource())) {
			throw new IllegalArgumentException("Ugyldig verdi: resource er ikke lik " + ALTINN_EVENTS_RESOURCE);
		}

		if (!CORRESPONDENCE_EVENT_TYPES.contains(altinnEvent.getType())) {
			throw new HendelseTypeBehandlesIkkeException("hendelse type=%s behandles ikke".formatted(sanitize(altinnEvent.getType())));
		}

		if (!SPEC_VERSION.equals(altinnEvent.getSpecversion())) {
			throw new DokDigdirHendelserTechnicalException("Ugyldig verdi: specversion må være 1.0");
		}
	}

	private AltinnEventValidator() {
	}
}
