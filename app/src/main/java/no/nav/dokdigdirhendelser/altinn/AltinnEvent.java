package no.nav.dokdigdirhendelser.altinn;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
@Validated
public record AltinnEvent(
		@NotNull
		UUID id,
		@NotBlank
		String resource,
		UUID resourceinstance,
		URI source,
		String specversion,
		@NotBlank
		String type,
		String subject,
		String alternativesubject,
		OffsetDateTime time) {

	public static final String VALIDATE_SUBSCRIPTION_EVENT_TYPE = "platform.events.validatesubscription";
	public static final Set<String> CORRESPONDENCE_EVENT_TYPES = Set.of(
			"no.altinn.correspondence.correspondencepublishfailed",
			"no.altinn.correspondence.correspondencereceiverread",
			"no.altinn.correspondence.correspondencereceiverconfirmed",
			"no.altinn.correspondence.correspondencenotificationcreationfailed",
			"no.altinn.correspondence.correspondencenotificationallfailed",
			"no.altinn.correspondence.correspondencepublished"
	);

}