package no.nav.dokdigdirhendelser.altinnsubscription;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdigdirhendelser.config.DokDigdirHendelserProperties;
import no.nav.dokdigdirhendelser.exception.DokDigdirHendelserTechnicalException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class AltinnSubscriptionService {

	public static final String RESOURCE_FILTER = "urn:altinn:resource:nav_dokumentdistribusjon_taushetsbelagtpost";
	public static final String ALTERNATIVE_SUBJECT_FILTER = "/organisation/889640782";

	private final RestClient altinnRestClient;
	private final DokDigdirHendelserProperties.AbonnementProperties abonnementProperties;

	public AltinnSubscriptionService(RestClient altinnRestClient,
									 DokDigdirHendelserProperties dokDigdirHendelserProperties) {
		this.abonnementProperties = dokDigdirHendelserProperties.subscription();
		this.altinnRestClient = altinnRestClient;
	}

	public AltinnAbonnementResponse abonnerAltinnEvent() {
		try {
			return altinnRestClient.post()
					.uri("/events/api/v1/subscriptions")
					.body(mapAbonnement())
					.retrieve()
					.body(AltinnAbonnementResponse.class);
		} catch (Exception e) {
			log.warn("Kunne ikke registrere abonnementet. Feilmelding:{}", e.getMessage());
			throw new DokDigdirHendelserTechnicalException("Kunne ikke registrere abonnementet. Feilmelding=" + e.getMessage(), e);
		}
	}

	public AltinnAbonnementResponse hentAbonnement(String id) {
		try {
			return altinnRestClient.get()
					.uri(uriBuilder -> uriBuilder.path("/events/api/v1/subscriptions/{id}")
							.build(id))
					.retrieve()
					.body(AltinnAbonnementResponse.class);
		} catch (Exception e) {
			log.warn("Kunne ikke hente Altinn abonnement: {}", e.getMessage());
			throw new DokDigdirHendelserTechnicalException("Kunne ikke hente Altinn abonnement: {}" + e.getMessage(), e);
		}
	}

	private AbonnementRequest mapAbonnement() {
		return AbonnementRequest.builder()
				.endpoint(abonnementProperties.endPoint())
				.sourceFilter(abonnementProperties.sourceFilter())
				.resourceFilter(RESOURCE_FILTER)
				.alternativeSubjectFilter(ALTERNATIVE_SUBJECT_FILTER)
				.build();
	}
}
