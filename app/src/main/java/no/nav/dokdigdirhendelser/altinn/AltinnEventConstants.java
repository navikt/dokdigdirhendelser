package no.nav.dokdigdirhendelser.altinn;

import java.util.Set;

public final class AltinnEventConstants {

	public static final String VALIDATE_SUBSCRIPTION_EVENT_TYPE = "platform.events.validatesubscription";
	public static final Set<String> CORRESPONDENCE_EVENT_TYPES = Set.of(
			"no.altinn.correspondence.correspondencepublishfailed",
			"no.altinn.correspondence.correspondencereceiverread",
			"no.altinn.correspondence.correspondencereceiverconfirmed",
			"no.altinn.correspondence.correspondencenotificationcreationfailed",
			"no.altinn.correspondence.correspondencenotificationfailed",
			"no.altinn.correspondence.correspondencenotificationallfailed",
			"no.altinn.correspondence.correspondencepublished"
	);

	private AltinnEventConstants() {
	}

}
