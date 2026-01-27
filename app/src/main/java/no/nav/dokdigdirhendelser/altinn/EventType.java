package no.nav.dokdigdirhendelser.altinn;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum EventType {
	ATTACHMENT_INITIALIZED("no.altinn.correspondence.attachmentinitialized"),
	ATTACHMENT_UPLOAD_PROCESSING("no.altinn.correspondence.attachmentuploadprocessing"),
	ATTACHMENT_PUBLISHED("no.altinn.correspondence.attachmentpublished"),
	ATTACHMENT_UPLOAD_FAILED("no.altinn.correspondence.attachmentuploadfailed"),
	ATTACHMENT_PURGED("no.altinn.correspondence.attachmentpurged"),
	CORRESPONDENCE_INITIALIZED("no.altinn.correspondence.correspondenceinitialized"),
	CORRESPONDENCE_ARCHIVED("no.altinn.correspondence.correspondencearchived"),
	CORRESPONDENCE_PURGED("no.altinn.correspondence.correspondencepurged"),
	CORRESPONDENCE_PUBLISH_FAILED("no.altinn.correspondence.correspondencepublishfailed"),
	CORRESPONDENCE_RECEIVER_READ("no.altinn.correspondence.correspondencereceiverread"),
	CORRESPONDENCE_RECEIVER_CONFIRMED("no.altinn.correspondence.correspondencereceiverconfirmed"),
	CORRESPONDENCE_RECEIVER_RESERVED("no.altinn.correspondence.correspondencereceiverreserved"),
	CORRESPONDENCE_NOTIFICATION_CREATION_FAILED("no.altinn.correspondence.correspondencenotificationcreationfailed"),
	CORRESPONDENCE_PUBLISHED("no.altinn.correspondence.correspondencepublished"),
	CORRESPONDENCE_RECEIVER_NEVER_READ("no.altinn.correspondence.correspondencereceiverneverread"),
	CORRESPONDENCE_RECEIVER_NEVER_CONFIRMED("no.altinn.correspondence.correspondencereceiverneverconfirmed");

	private final String value;

	public static boolean isValid(String type) {
		return Stream.of(values())
				.anyMatch(eventType -> eventType.value.equals(type));
	}

}
