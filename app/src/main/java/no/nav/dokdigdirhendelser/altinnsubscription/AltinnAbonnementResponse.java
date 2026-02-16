package no.nav.dokdigdirhendelser.altinnsubscription;

public record AltinnAbonnementResponse(
		String id,
		String endPoint,
		String sourceFilter,
		String subjectFilter,
		String alternativeSubjectFilter,
		String consumer,
		String createdBy,
		String created,
		boolean validated) {
}
