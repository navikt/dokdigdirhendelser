package no.nav.dokdigdirhendelser.altinn;

import lombok.Builder;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record AltinnEvent(
		UUID id,
		String resource,
		UUID resourceinstance,
		URI source,
		String specversion,
		String type,
		String subject,
		String alternativesubject,
		OffsetDateTime time) {

	public static final String VALIDATE_SUBSCRIPTION_EVENT_TYPE = "platform.events.validatesubscription";
	public static final Set<String> CORRESPONDENCE_EVENT_TYPES = Set.of(
			"no.altinn.correspondence.attachmentinitialized",
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
			"no.altinn.correspondence.correspondencenotificationfailed",
			"no.altinn.correspondence.correspondencepublished",
			"no.altinn.correspondence.correspondencereceiverneverread",
			"no.altinn.correspondence.correspondencereceiverneverconfirmed"
	);

}
