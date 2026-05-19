package no.nav.dokdigdirhendelser.altinn.eventvalidator;

import lombok.extern.slf4j.Slf4j;
import no.altinn.event.domain.CloudEvent;
import no.nav.dokdigdirhendelser.exception.DokDigdirHendelserTechnicalException;
import no.nav.dokdigdirhendelser.exception.HendelseTypeBehandlesIkkeException;

import static no.nav.dokdigdirhendelser.altinn.AltinnEventConstants.CORRESPONDENCE_EVENT_TYPES;
import static no.nav.dokdigdirhendelser.altinn.CloudEventExtensions.getExtension;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.ALTINN_EVENTS_RESOURCE;
import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.SPEC_VERSION;
import static no.nav.dokdigdirhendelser.utils.SafeLog.sanitize;

@Slf4j
public class AltinnEventValidator {

	public static void validerAltinnEvent(CloudEvent cloudEvent) {
		if (cloudEvent == null) {
			throw new DokDigdirHendelserTechnicalException("AltinnEvent kan ikke være null");
		}

		String resource = getExtension(cloudEvent, "resource");
		if (!ALTINN_EVENTS_RESOURCE.equals(resource)) {
			throw new IllegalArgumentException("Ugyldig verdi: resource er ikke lik " + ALTINN_EVENTS_RESOURCE);
		}

		if (!CORRESPONDENCE_EVENT_TYPES.contains(cloudEvent.getType())) {
			throw new HendelseTypeBehandlesIkkeException("hendelse type=%s behandles ikke".formatted(sanitize(cloudEvent.getType())));
		}

		String specversion = getExtension(cloudEvent, "specversion");
		if (!SPEC_VERSION.equals(specversion)) {
			throw new DokDigdirHendelserTechnicalException("Ugyldig verdi: specversion må være 1.0");
		}
	}

	private AltinnEventValidator() {
	}
}
