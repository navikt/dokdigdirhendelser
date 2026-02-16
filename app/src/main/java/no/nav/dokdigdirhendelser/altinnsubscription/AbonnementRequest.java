package no.nav.dokdigdirhendelser.altinnsubscription;

import lombok.Builder;

@Builder
public record AbonnementRequest(
		String endpoint,
		String sourceFilter,
		String resourceFilter,
		String alternativeSubjectFilter) {
}
