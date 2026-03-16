package no.nav.dokdigdirhendelser.altinn;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record AltinnEvent(
		@NotNull(message = "id kan ikke være null og må være en gyldig UUID.")
		UUID id,
		@NotBlank(message = "resource kan ikke være tom")
		String resource,
		@NotNull(message = "resourceinstance kan ikke være null og må være en gyldig UUID.")
		UUID resourceinstance,
		@NotNull(message = "source kan ikke være null og må være en gyldig URI type")
		URI source,
		@NotBlank(message = "specversion kan ikke være tom")
		String specversion,
		@NotBlank(message = "type kan ikke være tom")
		String type,
		@NotNull(message = "time kan ikke være null")
		OffsetDateTime time) {

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
