package no.nav.dokdigdirhendelser.altinnsubscription;

import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.dokdigdirhendelser.altinnsubscription.naistexas.NaisTexasConsumer;
import no.nav.dokdigdirhendelser.config.DokDigdirHendelserProperties;
import no.nav.dokdigdirhendelser.exception.DokDigdirHendelserTechnicalException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static no.nav.dokdigdirhendelser.config.LokalCacheConfig.ALTINN_TOKEN_CACHE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
public class AltinnTokenExchangeConsumer {

	private static final String ALTINN_TOKEN_PATH = "/authentication/api/v1/exchange/maskinporten";

	private final RestClient restClient;
	private final NaisTexasConsumer naisTexasConsumer;

	public AltinnTokenExchangeConsumer(RestClient restClient,
									   DokDigdirHendelserProperties dokDigdirHendelserProperties,
									   NaisTexasConsumer naisTexasConsumer) {
		this.naisTexasConsumer = naisTexasConsumer;
		this.restClient = restClient.mutate()
				.baseUrl(dokDigdirHendelserProperties.altinnUrl())
				.build();
	}

	@Cacheable(ALTINN_TOKEN_CACHE)
	public String hentAltinnToken() {
		try {
			return restClient.get()
					.uri(ALTINN_TOKEN_PATH)
					.headers(httpHeaders -> {
						httpHeaders.setContentType(APPLICATION_JSON);
						httpHeaders.setBearerAuth(naisTexasConsumer.getMaskinportenToken());
					})
					.retrieve()
					.body(TextNode.class).asText();
		} catch (RestClientException e) {
			if (e.getCause() instanceof HttpMessageNotReadableException notReadableException) {
				throw new DokDigdirHendelserTechnicalException("Teknisk feil ved token-utveksling: Kunne ikke parse token med feilmelding=" + notReadableException.getMessage(), e);
			}
			throw new DokDigdirHendelserTechnicalException("Teknisk feil ved token-utveksling: Kunne ikke hente Altinn-token under exchange av maskinporten-token. Feilmelding" + e.getMessage(), e);
		} catch (Exception e) {
			throw new DokDigdirHendelserTechnicalException("Ukjent teknisk feil ved token-utveksling: Kunne ikke hente Altinn-token med feilmelding=" + e.getMessage(), e);
		}
	}


}
