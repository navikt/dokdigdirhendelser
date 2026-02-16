package no.nav.dokdigdirhendelser.altinnsubscription.naistexas;

import no.nav.dokdigdirhendelser.config.NaisProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Service
public class NaisTexasConsumer {

	private final RestClient restClient;
	private final MaskinportenProperties maskinportenProperties;

	public NaisTexasConsumer(RestClient restClient,
							 NaisProperties naisProperties,
							 MaskinportenProperties maskinportenProperties) {
		this.maskinportenProperties = maskinportenProperties;
		this.restClient = restClient.mutate()
				.baseUrl(naisProperties.tokenEndpoint())
				.build();
	}

	/**
	 * Maskinporten token fra Texas
	 *
	 * @return Bearer token
	 */
	public String getMaskinportenToken() {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("identity_provider", "maskinporten");
		formData.add("target", maskinportenProperties.scopes());

		return requireNonNull(restClient.post()
				.contentType(APPLICATION_FORM_URLENCODED)
				.body(formData)
				.retrieve()
				.body(NaisTexasToken.class))
				.accessToken();
	}
}
