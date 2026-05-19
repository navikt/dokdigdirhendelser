package no.nav.dokdigdirhendelser.altinn;

import no.altinn.event.domain.CloudEvent;

public final class CloudEventExtensions {

	private CloudEventExtensions() {
	}

	public static String getExtension(CloudEvent cloudEvent, String extensionName) {
		if (cloudEvent == null || cloudEvent.getExtensionAttributes() == null) {
			return null;
		}
		return cloudEvent.getExtensionAttributes().stream()
				.filter(attr -> extensionName.equals(attr.getName()))
				.map(attr -> attr.getType() != null ? attr.getType().getName() : null)
				.findFirst()
				.orElse(null);
	}
}
