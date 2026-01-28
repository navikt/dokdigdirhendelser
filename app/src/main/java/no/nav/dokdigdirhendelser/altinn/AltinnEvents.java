package no.nav.dokdigdirhendelser.altinn;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import no.nav.dokdigdirhendelser.altinn.eventvalidator.ValiderTime;

import java.util.Set;

import static no.nav.dokdigdirhendelser.config.DokDigdirHendelserConstant.UUID_REGEX;

@Builder
@ValiderTime
public record AltinnEvents(
		@NotBlank(message = "id kan ikke være tom")
		@Pattern(regexp = UUID_REGEX, message = "id kan være en UUID type")
		String id,
		@NotBlank(message = "resource kan ikke være tom")
		String resource,
		@Pattern(regexp = UUID_REGEX, message = "resourceinstance kan være en UUID type")
		String resourceinstance,
		@NotBlank(message = "source kan være en gyldig URI type")
		String source,
		@NotBlank(message = "specversion kan ikke være tom")
		String specversion,
		@NotBlank(message = "type kan ikke være tom")
		String type,
		String subject,
		@NotBlank(message = "alternativesubject kan ikke være tom")
		@Pattern(regexp = "^/organisation/\\d{9}$", message = "alternativesubject må være på formatet /organisation/{9 siffer}")
		String alternativesubject,
		String time) {

	public static final Set<String> ALTINN_EVENT_TYPES = Set.of("no.altinn.correspondence.attachmentinitialized",
			"no.altinn.correspondence.attachmentuploadprocessing",
			"no.altinn.correspondence.attachmentpublished",
			"no.altinn.correspondence.attachmentuploadfailed",
			"no.altinn.correspondence.attachmentpurged",
			"no.altinn.correspondence.correspondenceinitialized",
			"no.altinn.correspondence.correspondencearchived",
			"no.altinn.correspondence.correspondencepurged",
			"no.altinn.correspondence.correspondencepublishfailed",
			"no.altinn.correspondence.correspondencereceiverread",
			"no.altinn.correspondence.correspondencereceiverconfirmed",
			"no.altinn.correspondence.correspondencereceiverreserved",
			"no.altinn.correspondence.correspondencenotificationcreationfailed",
			"no.altinn.correspondence.correspondencepublished",
			"no.altinn.correspondence.correspondencereceiverneverread",
			"no.altinn.correspondence.correspondencereceiverneverconfirmed");
}
