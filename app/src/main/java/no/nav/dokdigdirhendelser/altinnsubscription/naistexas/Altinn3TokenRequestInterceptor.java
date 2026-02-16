package no.nav.dokdigdirhendelser.altinnsubscription.naistexas;

import no.nav.dokdigdirhendelser.altinnsubscription.AltinnTokenExchangeConsumer;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class Altinn3TokenRequestInterceptor implements ClientHttpRequestInterceptor {

	private final AltinnTokenExchangeConsumer altinnTokenExchangeConsumer;

	public Altinn3TokenRequestInterceptor(AltinnTokenExchangeConsumer altinnTokenExchangeConsumer) {
		this.altinnTokenExchangeConsumer = altinnTokenExchangeConsumer;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution next) throws IOException {
		request.getHeaders().setBearerAuth(
			altinnTokenExchangeConsumer.hentAltinnToken()
		);
		return next.execute(request, body);
	}
}
